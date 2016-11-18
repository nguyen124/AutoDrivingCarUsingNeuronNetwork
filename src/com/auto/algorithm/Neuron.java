package com.auto.algorithm;

import java.util.ArrayList;

/*
 * @Description: Neuron is the basic element in the neuron network. Each neuron has
 * a certain number of inputs. In this program Neuron from HiddenLayer has 5 inputs which
 * are from the car's 5 sensors and Neuron in OutputLayer has 8 inputs which are from
 * 8 HiddenLayers
 */
public class Neuron {
	protected int numberOfInputs;
	protected ArrayList<Double> weights;

	public void init(ArrayList<Double> weightsIn, int numOfInputs) {
		this.numberOfInputs = numOfInputs;
		weights = weightsIn;
	}
}
