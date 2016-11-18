package com.auto.algorithm;

import java.util.Random;

/*
 * Description: Global helper functions
 */
public class HelperFunction {
	/*
	 * normalize value to make it from 1 to -1
	 */
	public static double Sigmoid(float a, float p) {
		float ap = (-a) / p;
		return (1 / (1 + Math.exp(ap)));
	}

	/*
	 * random number from -1 to 1;
	 */
	public static double RandomSigmoid() {
		Random ran = new Random(System.nanoTime());
		double r = ran.nextDouble() - ran.nextDouble();
		return r;
	}
	/*
	 * compare value of a to b and c. If is smaller then b or greater than c, a will become b or c
	 */
	public static double getValueInRange(double a, double b, double c) {
		if (a < b) {
			return b;
		} else if (a > c) {
			return c;
		}
		return a;
	}
}
