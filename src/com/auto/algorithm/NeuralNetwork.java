package com.auto.algorithm;

import java.util.ArrayList;

/*
 * @Description: NeuralNetwork is used to make decision for the car; decide that it
 * should turn right or turn left or go straight. It may contain many hidden NeuronsLayers 
 * and 1 output NeuronsLayer. These layers will constantly update to get new values from genomes
 * each time the car crashs to the wall. The network will use new values from Genetic to make
 * dicision for the next try of car. 
 */
public class NeuralNetwork {
	private int inputAmount;
	private int outputAmount;
	private ArrayList<Double> outputs;
	private ArrayList<Double> inputs;
	// HiddenLayer produces the input for OutputLayer
	private ArrayList<NeuronsLayer> hiddenLayers;
	// OutputLayer will receive input from HiddenLayer
	private NeuronsLayer outputLayer;

	public NeuralNetwork() {
		outputs = new ArrayList<Double>();
		inputs = new ArrayList<Double>();
	}

	/*
	 * receive input from sensors of car which is normalized distance from
	 * center of car to the wall.
	 */
	public void setInput(double[] normalizedDepths) {
		inputs.clear();
		for (int i = 0; i < normalizedDepths.length; i++) {
			inputs.add(normalizedDepths[i]);
		}
	}

	@SuppressWarnings("unchecked")
	public void update() {
		outputs.clear();
		for (int i = 0; i < hiddenLayers.size(); i++) {
			if (i > 0) {
				inputs = outputs;
			}
			// each hidden layer calculates the outputs based on inputs
			// from sensors of the car
			hiddenLayers.get(i).evaluate(inputs, outputs);
			System.out.println("Output of hiddden layers: "
					+ outputs.toString());
		}
		// the outputs of HiddenLayers will be used as input for
		// OutputLayer
		inputs = (ArrayList<Double>) outputs.clone();
		// The output layer will give out the final outputs
		outputLayer.evaluate(inputs, outputs);
	}

	public double getOutput(int index) {
		if (index >= outputAmount)
			return 0.0f;
		return outputs.get(index);
	}

	/*
	 * Initiate NeuronsNetwork
	 */
	/*
	 * public void createNet(int numOfHiddenLayers, int numOfInputs, int
	 * neuronsPerHidden, int numOfOutputs) { inputAmount = numOfInputs;
	 * outputAmount = numOfOutputs; hiddenLayers = new
	 * ArrayList<NeuronsLayer>(); for (int i = 0; i < numOfHiddenLayers; i++) {
	 * NeuronsLayer layer = new NeuronsLayer();
	 * layer.populateLayer(neuronsPerHidden, numOfInputs);
	 * hiddenLayers.add(layer); } outputLayer = new NeuronsLayer();
	 * outputLayer.populateLayer(numOfOutputs, neuronsPerHidden); }
	 */
	public void releaseNet() {
		// inputLayer = null;
		outputLayer = null;
		hiddenLayers = null;
	}

	/*
	 * Neural network receives weights from genome to make new HiddenLayers and
	 * OutputLayer.
	 */
	public void fromGenome(Genome genome, int numOfInputs,
			int neuronsPerHidden, int numOfOutputs) {
		if (genome == null)
			return;
		releaseNet();
		hiddenLayers = new ArrayList<NeuronsLayer>();
		outputAmount = numOfOutputs;
		inputAmount = numOfInputs;
		NeuronsLayer hidden = new NeuronsLayer();
		ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		for (int i = 0; i < neuronsPerHidden; i++) {
			ArrayList<Double> weights = new ArrayList<Double>();
			for (int j = 0; j < numOfInputs + 1; j++) {
				weights.add(genome.weights.get(i * neuronsPerHidden + j));
			}
			Neuron n = new Neuron();
			n.init(weights, numOfInputs);
			neurons.add(n);
		}
		hidden.loadLayer(neurons);
		hiddenLayers.add(hidden);

		// Clear weights and reasign the weights to the output.
		ArrayList<Neuron> neuronsOut = new ArrayList<Neuron>();
		for (int i = 0; i < numOfOutputs; i++) {
			ArrayList<Double> weights = new ArrayList<Double>();
			for (int j = 0; j < neuronsPerHidden + 1; j++) {
				weights.add(genome.weights.get(i * neuronsPerHidden + j));
			}
			Neuron n = new Neuron();
			n.init(weights, neuronsPerHidden);
			neuronsOut.add(n);
		}
		outputLayer = new NeuronsLayer();
		outputLayer.loadLayer(neuronsOut);
	}

}
