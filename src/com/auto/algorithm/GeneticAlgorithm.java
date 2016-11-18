package com.auto.algorithm;

import java.util.ArrayList;
import java.util.Random;

/*
 * @Description: GeneticAlgorithm is used to train neuron network. It has a list of 
 * genomes called population. Each population will have some of the best genomes with highest
 * fitness. Fitness is the sum of distance that how far a car could go. The best genomes will be used
 * to create other genomes by mixing them up (crossing over between 2 genomes) and mutate
 * some of their genes. It is a little bit different with basic genetic algorithm, the mutate will not
 * turn of and off a gene but they randomly change the weight of genes.
 */
public class GeneticAlgorithm {
	public static final float MAX_PERMUTATION = 0.3f;
	public static final float MUTATION_RATE = 0.15f;
	private int currentGenome;
	private int totalPopulation;
	private int genomeID;
	private int generation;
	private ArrayList<Genome> population;

	public GeneticAlgorithm() {
		currentGenome = -1;
		totalPopulation = 0;
		genomeID = 0;
		generation = 1;
		population = new ArrayList<Genome>();
	}

	/*
	 * Generate genomes population with ID, fitness and random Sigmoid weights
	 */
	public void generateNewPopulation(int totalPop, int totalWeights) {
		generation = 1;
		clearPopulation();
		currentGenome = -1;
		totalPopulation = totalPop;
		for (int i = 0; i < totalPopulation; i++) {
			Genome genome = new Genome();
			genome.ID = genomeID;
			genome.fitness = 0.0;
			genome.weights = new ArrayList<Double>();
			for (int j = 0; j < totalWeights; j++) {
				genome.weights.add(HelperFunction.RandomSigmoid());
			}
			genomeID++;
			population.add(genome);
		}
	}

	public void setGenomeFitness(double fitness, int index) {
		if (index >= population.size() || index < 0)
			return;
		population.get(index).fitness = fitness;
	}

	public Genome getNextGenome() {
		currentGenome++;
		if (currentGenome >= population.size())
			return null;
		return population.get(currentGenome);
	}

	public void clearPopulation() {
		population.clear();
	}

	/*
	 * This function will generate new population of genomes based on best 4
	 * genomes (genomes which have highest fitness). The best genomes will be
	 * mixed up and mutated to create new genomes.
	 */
	public void breedPopulation() {
		ArrayList<Genome> bestGenomes = new ArrayList<Genome>();
		// Find the 4 best genomes which have highest fitness.
		bestGenomes = getBestGenomes(4);
		ArrayList<Genome> children = new ArrayList<Genome>();
		// Carry on the best genome.
		Genome bestGenome = new Genome();
		bestGenome.fitness = 0.0;
		bestGenome.ID = bestGenomes.get(0).ID;
		bestGenome.weights = bestGenomes.get(0).weights;
		// mutate few gene of genome to create new genome
		mutate(bestGenome);
		children.add(bestGenome);
		// Child genomes.
		ArrayList<Genome> crossedOverGenomes;
		// Breed with genome 0.
		crossedOverGenomes = crossOver(bestGenomes.get(0), bestGenomes.get(1));
		mutate(crossedOverGenomes.get(0));
		mutate(crossedOverGenomes.get(1));
		children.add(crossedOverGenomes.get(0));
		children.add(crossedOverGenomes.get(1));
		crossedOverGenomes = crossOver(bestGenomes.get(0), bestGenomes.get(2));
		mutate(crossedOverGenomes.get(0));
		mutate(crossedOverGenomes.get(1));
		children.add(crossedOverGenomes.get(0));
		children.add(crossedOverGenomes.get(1));
		crossedOverGenomes = crossOver(bestGenomes.get(0), bestGenomes.get(3));
		mutate(crossedOverGenomes.get(0));
		mutate(crossedOverGenomes.get(1));
		children.add(crossedOverGenomes.get(0));
		children.add(crossedOverGenomes.get(1));

		// Breed with genome 1.
		crossedOverGenomes = crossOver(bestGenomes.get(1), bestGenomes.get(2));
		mutate(crossedOverGenomes.get(0));
		mutate(crossedOverGenomes.get(1));
		children.add(crossedOverGenomes.get(0));
		children.add(crossedOverGenomes.get(1));
		crossedOverGenomes = crossOver(bestGenomes.get(1), bestGenomes.get(3));
		mutate(crossedOverGenomes.get(0));
		mutate(crossedOverGenomes.get(1));
		children.add(crossedOverGenomes.get(0));
		children.add(crossedOverGenomes.get(1));
		// For the remainding n population, add some random genomes.
		int remainingChildren = (totalPopulation - children.size());
		for (int i = 0; i < remainingChildren; i++) {
			children.add(this.createNewGenome(bestGenomes.get(0).weights.size()));
		}
		clearPopulation();
		population = children;
		currentGenome = -1;
		generation++;
	}

	private Genome createNewGenome(int totalWeights) {
		Genome genome = new Genome();
		genome.ID = genomeID;
		genome.fitness = 0.0f;
		genome.weights = new ArrayList<Double>();
		for (int j = 0; j < totalWeights; j++) {
			genome.weights.add(HelperFunction.RandomSigmoid());
		}
		genomeID++;
		return genome;
	}

	/*
	 * This function will mix up two genomes to create 2 other new genomes
	 */
	private ArrayList<Genome> crossOver(Genome g1, Genome g2) {
		Random random = new Random(System.nanoTime());
		// Select a random cross over point.
		int totalWeights = g1.weights.size();
		int crossover = Math.abs(random.nextInt()) % totalWeights;
		ArrayList<Genome> genomes = new ArrayList<Genome>();
		Genome genome1 = new Genome();
		genome1.ID = genomeID;
		genome1.weights = new ArrayList<Double>();
		genomeID++;
		Genome genome2 = new Genome();
		genome2.ID = genomeID;
		genome2.weights = new ArrayList<Double>();
		genomeID++;
		// Go from start to crossover point, copying the weights from g1 to children.
		for (int i = 0; i < crossover; i++) {
			genome1.weights.add(g1.weights.get(i));
			genome2.weights.add(g2.weights.get(i));
		}
		// Go from start to crossover point, copying the weights from g2 to children.
		for (int i = crossover; i < totalWeights; i++) {
			genome1.weights.add(g2.weights.get(i));
			genome2.weights.add(g1.weights.get(i));
		}
		genomes.add(genome1);
		genomes.add(genome2);
		return genomes;
	}

	/*
	 * Generate a random chance of mutating the weight in the genome.
	 */
	private void mutate(Genome genome) {
		for (int i = 0; i < genome.weights.size(); ++i) {
			double randomSigmoid = HelperFunction.RandomSigmoid();
			if (randomSigmoid < MUTATION_RATE) {
				genome.weights.set(i, genome.weights.get(i)
						+ (randomSigmoid * MAX_PERMUTATION));
			}
		}
	}

	/*
	 * Get the best genomes to breed new population
	 */
	private ArrayList<Genome> getBestGenomes(int totalGenomes) {
		int genomeCount = 0;
		int runCount = 0;
		ArrayList<Genome> bestGenomes = new ArrayList<Genome>();
		while (genomeCount < totalGenomes) {
			if (runCount > 10) {
				break;
			}
			runCount++;
			// Find the best cases for cross breeding based on fitness score.
			double bestFitness = 0;
			int bestIndex = -1;
			for (int i = 0; i < this.totalPopulation; i++) {
				if (population.get(i).fitness > bestFitness) {
					boolean isUsed = false;
					for (int j = 0; j < bestGenomes.size(); j++) {
						if (bestGenomes.get(j).ID == population.get(i).ID) {
							isUsed = true;
						}
					}
					if (isUsed == false) {
						bestIndex = i;
						bestFitness = population.get(bestIndex).fitness;
					}
				}
			}
			if (bestIndex != -1) {
				genomeCount++;
				bestGenomes.add(population.get(bestIndex));
			}

		}
		return bestGenomes;
	}

	public int getCurrentGeneration() {
		return generation;
	}

	public int getCurrentGenomeIndex() {
		return currentGenome;
	}

}
