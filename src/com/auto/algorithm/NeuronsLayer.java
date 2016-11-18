package com.auto.algorithm;

import java.util.ArrayList;

/*
 * @Description: NeuronsLayer contains Neurons. It evaluates these nerons to give
 * out decision.
 */
public class NeuronsLayer {
	public static final float BIAS = -1.0f;
	private int totalNeurons;
	// int totalInputs;
	private ArrayList<Neuron> neurons;

	/*
	 * Evaluate the inputs from sensors or HiddenLayer and give out the output
	 */
	public void evaluate(ArrayList<Double> inputs, ArrayList<Double> outputs) {
		int inputIndex = 0;
		for (int i = 0; i < totalNeurons; i++) {
			float activation = 0.0f;
			int numOfInputs = neurons.get(i).numberOfInputs;
			Neuron neuron = neurons.get(i);
			// sum the weights up to numberOfInputs-1 and add the bias
			for (int j = 0; j < numOfInputs - 1; j++) {
				if (inputIndex < inputs.size()) {
					activation += inputs.get(inputIndex)
							* neuron.weights.get(j);
					inputIndex++;
				}
			}
			// Add the bias.
			activation += neuron.weights.get(numOfInputs) * BIAS;
			outputs.add(HelperFunction.Sigmoid(activation, 1.0f));
			inputIndex = 0;
		}
	}

	public ArrayList<Double> getWeights() {
		// Calculate the size of the output vector by calculating the amount of
		// weights in each neurons.
		ArrayList<Double> out = new ArrayList<Double>();
		for (int i = 0; i < this.totalNeurons; i++) {
			Neuron n = neurons.get(i);
			for (int j = 0; j < n.weights.size(); j++) {
				out.add(n.weights.get(j));
			}
		}
		return out;
	}

	public void loadLayer(ArrayList<Neuron> neurons) {
		totalNeurons = neurons.size();
		this.neurons = neurons;
	}
}
