package com.auto.car.entity;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.auto.algorithm.GeneticAlgorithm;
import com.auto.algorithm.Genome;
import com.auto.algorithm.NeuralNetwork;
import com.auto.car.entity.mob.Car;
import com.auto.car.level.Level;
import com.auto.car.level.SpawnLevel;
import com.auto.car.level.tile.TileCoordinate;
import com.auto.graphics.Screen;

/*
 * Description: This class manages important entities such as: Car, GeneticAlgorithm
 * and NeuralNetwork
 */
public class EntityManager {
	public static final int NN_OUTPUT_COUNT = 2;

	public static enum NeuralNetOuputs {
		NN_OUTPUT_RIGHT_FORCE, NN_OUTPUT_LEFT_FORCE,
	}

	public static final int HIDDEN_LAYER_NEURONS = 8;
	public static final int MAX_GENOME_POPULATION = 30;
	public static final float CHECK_POINT_BONUS = 15.0f;
	public static final double DEFAULT_ROTATION = -Math.PI / 3;
	public static TileCoordinate tileCoor = new TileCoordinate(5, 10);
	public static Point2D DEFAULT_POSITION = new Point2D.Double(
			tileCoor.getX() - 12, tileCoor.getY() - 12);
	// fitness is used to measure how far the car could go without collision
	double currentAgentFitness;
	double bestFitness;
	private int levelIdx;
	private Car car;
	private ArrayList<Checkpoint> checkpoints;
	private NeuralNetwork neuralNet;
	private ArrayList<Level> levels;
	private GeneticAlgorithm genAlg;

	/*
	 * Constructor of Entity Manager. Inside this, we initiate the algorithm,
	 * neural network, car and levels
	 */
	public EntityManager() {
		int totalWeights = Car.FEELER_COUNT * HIDDEN_LAYER_NEURONS
				+ HIDDEN_LAYER_NEURONS * NN_OUTPUT_COUNT + HIDDEN_LAYER_NEURONS
				+ NN_OUTPUT_COUNT;
		currentAgentFitness = 0.0;
		bestFitness = 0.0;
		genAlg = new GeneticAlgorithm();
		genAlg.generateNewPopulation(MAX_GENOME_POPULATION, totalWeights);
		neuralNet = new NeuralNetwork();
		neuralNet.fromGenome(genAlg.getNextGenome(), Car.FEELER_COUNT,
				HIDDEN_LAYER_NEURONS, NN_OUTPUT_COUNT);
		levels = new ArrayList<Level>();
		levels.add(new SpawnLevel("/levels/SecondLevel.png"));
		levels.add(new SpawnLevel("/levels/SecondLevel2.png"));
		levels.add(new SpawnLevel("/levels/SecondLevel3.png"));
		checkpoints = loadCheckPoints("/checkpoints2.csv");
		levelIdx = 0;
		car = new Car((int) DEFAULT_POSITION.getX(),
				(int) DEFAULT_POSITION.getY(), DEFAULT_ROTATION,
				levels.get(levelIdx));
		car.attach(neuralNet);

	}

	public ArrayList<Checkpoint> getCheckpoints() {
		return checkpoints;
	}

	/*
	 * load predefined checkpoints of map, checkpoints could be optional. I may
	 * help the car learn faster.
	 */
	public ArrayList<Checkpoint> loadCheckPoints(String filename) {
		BufferedReader br = null;
		String line = null;
		String splitBy = ",";
		ArrayList<Checkpoint> result = new ArrayList<Checkpoint>();
		try {
			br = new BufferedReader(new FileReader(SpawnLevel.class
					.getResource(filename).getFile()));
			while ((line = br.readLine()) != null) {
				String[] points = line.split(splitBy);
				TileCoordinate startCoor = new TileCoordinate(
						(int) Double.parseDouble(points[0]),
						(int) Double.parseDouble(points[1]));
				TileCoordinate endCoor = new TileCoordinate(
						(int) Double.parseDouble(points[2]),
						(int) Double.parseDouble(points[3]));
				Point2D start = new Point2D.Float();
				Point2D end = new Point2D.Float();
				start.setLocation(startCoor.getX(), startCoor.getY());
				end.setLocation(endCoor.getX(), endCoor.getY());
				result.add(new Checkpoint(start, end, true));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/*
	 * Generate new test subject after the car fails. The new genome will be fed
	 * in for neural network.
	 */
	public void nextTestSubject() {
		genAlg.setGenomeFitness(currentAgentFitness,
				genAlg.getCurrentGenomeIndex());
		currentAgentFitness = 0;
		Genome genome = genAlg.getNextGenome();
		neuralNet.fromGenome(genome, Car.FEELER_COUNT,
				EntityManager.HIDDEN_LAYER_NEURONS,
				EntityManager.NN_OUTPUT_COUNT);
		car = new Car((int) DEFAULT_POSITION.getX(),
				(int) DEFAULT_POSITION.getY(), DEFAULT_ROTATION,
				levels.get(levelIdx));
		car.attach(neuralNet);
		car.clearFailure();
		// Reset the checkpoint flags
		for (int i = 0; i < checkpoints.size(); i++) {
			checkpoints.get(i).setActive(true);
		}
	}

	/*
	 * public void breedNewPopulation() { genAlg.clearPopulation(); int
	 * totalWeights = Car.FEELER_COUNT * HIDDEN_LAYER_NEURONS +
	 * HIDDEN_LAYER_NEURONS * NN_OUTPUT_COUNT + HIDDEN_LAYER_NEURONS +
	 * NN_OUTPUT_COUNT; genAlg.generateNewPopulation(MAX_GENOME_POPULATION,
	 * totalWeights); }
	 */

	/*
	 * Make new population after the old population failed to help the car finishing the track.
	 */
	public void evolveGenomes() {
		genAlg.breedPopulation();
		nextTestSubject();
	}

	public void update() {
		if (car.hasFailed()) {
			if (genAlg.getCurrentGenomeIndex() == MAX_GENOME_POPULATION - 1) {
				evolveGenomes();
			} else {
				nextTestSubject();
			}
		}
		car.update();
		currentAgentFitness += car.getDistanceDelta() / 2.0;
		if (currentAgentFitness > bestFitness) {
			bestFitness = currentAgentFitness;
		}
		// Test the agent against the active checkpoints.
		for (int i = 0; i < checkpoints.size(); i++) {
			// See if the checkpoint has already been hit.
			if (!checkpoints.get(i).isActive())
				continue;
			Rectangle2D rec = car.getCarBound();
			if (checkpoints.get(i).checkIntersect(rec)) {
				// Each time a check point hit, the fitness gets a bonus of 15
				currentAgentFitness += CHECK_POINT_BONUS;
				checkpoints.get(i).setActive(false);
				if (currentAgentFitness > bestFitness) {
					bestFitness = currentAgentFitness;
				}
				return;
			}
		}

	}
	
	/*
	 * Force to create new genomes in case the car is stucked moving in circle.
	 */
	public void forceToNextAgent() {
		if (genAlg.getCurrentGenomeIndex() == MAX_GENOME_POPULATION - 1) {
			evolveGenomes();
			return;
		}
		nextTestSubject();
	}

	/*
	 * The map is manipulated by individual pixels.
	 */
	public void renderByPixels(Screen screen) {
		// The car will actually stay at a fixed position, just only screen
		// move. That why we need to set offset for screen when the car "move"
		int xScroll = car.getX() - screen.getWidth() / 2;
		int yScroll = car.getY() - screen.getHeight() / 2;
		levels.get(levelIdx).render(xScroll, yScroll, screen);
	}
	/*
	 * Except the map, all other entities are using built in functions of java
	 * graphics to render.
	 */
	public void renderByGraphics(Screen screen) {
		car.render(screen);
		for (int i = 0; i < checkpoints.size(); i++) {
			if (!checkpoints.get(i).isActive()) {
				continue;
			} else {
				checkpoints.get(i).render(screen);
			}
		}
		int genomeNumber = genAlg.getCurrentGenomeIndex() + 1;
		ArrayList<String> info = new ArrayList<String>();
		info.add("Genome: " + genomeNumber + " of " + MAX_GENOME_POPULATION);
		info.add("Fitness: " + this.currentAgentFitness);
		info.add("Generation: " + genAlg.getCurrentGeneration());
		info.add("Best Fitness To date: " + bestFitness);
		screen.renderStatistics(info);
	}

	/*
	 * Change the map to see how the car move with new map
	 */
	public void nextMapIndex() {
		levelIdx++;
		if (levelIdx >= levels.size()) {
			levelIdx = 0;
		}
		switch (levelIdx) {
		case 0:
			TileCoordinate tileCoor0 = new TileCoordinate(5, 10);
			DEFAULT_POSITION.setLocation(tileCoor0.getX(), tileCoor0.getY());
			car.setPosition(tileCoor0.getX(), tileCoor0.getY());
			car.setLevel(levels.get(levelIdx));
			car.attach(neuralNet);
			checkpoints = loadCheckPoints("/checkpoints2.csv");
			break;
		case 1:
			TileCoordinate tileCoor1 = new TileCoordinate(8, 17);
			DEFAULT_POSITION.setLocation(tileCoor1.getX(), tileCoor1.getY());
			car.setPosition(tileCoor1.getX(), tileCoor1.getY());
			car.setLevel(levels.get(levelIdx));
			car.attach(neuralNet);
			checkpoints = new ArrayList<Checkpoint>();
			break;
		case 2:
			TileCoordinate tileCoor2 = new TileCoordinate(17, 22);
			DEFAULT_POSITION.setLocation(tileCoor2.getX(), tileCoor2.getY());
			car.setPosition(tileCoor2.getX(), tileCoor2.getY());
			car.setLevel(levels.get(levelIdx));
			car.attach(neuralNet);
			checkpoints = new ArrayList<Checkpoint>();
			break;
		}
	}

}
