/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */

package org.encog.neural.pattern;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.SOMLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A self organizing map is a neural network pattern with an input and output
 * layer. There is no hidden layer. The winning neuron, which is that neuron
 * with the higest output is the winner, this winning neuron is often used to
 * classify the input into a group.
 * 
 * @author jheaton
 * 
 */
public class SOMPattern implements NeuralNetworkPattern {

	/**
	 * The number of input neurons.
	 */
	private int inputNeurons;

	/**
	 * The number of output neurons.
	 */
	private int outputNeurons;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Add a hidden layer. SOM networks do not have hidden layers, so this will
	 * throw an error.
	 * 
	 * @param count
	 *            The number of hidden neurons.
	 */
	public void addHiddenLayer(final int count) {
		final String str = "A SOM network does not have hidden layers.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Clear out any hidden neurons.
	 */
	public void clear() {
	}

	/**
	 * Generate the RSOM network.
	 * 
	 * @return The neural network.
	 */
	public BasicNetwork generate() {
		final Layer input = new BasicLayer(new ActivationLinear(), false,
				this.inputNeurons);
		final Layer output = new BasicLayer(new ActivationLinear(), false,
				this.outputNeurons);
		int y = PatternConst.START_Y;
		final BasicNetwork network = new BasicNetwork(new SOMLogic());
		network.addLayer(input);
		network.addLayer(output);
		input.setX(PatternConst.START_X);
		output.setX(PatternConst.START_X);
		input.setY(y);
		y += PatternConst.INC_Y;
		output.setY(y);
		network.getStructure().finalizeStructure();
		network.reset();
		return network;
	}

	/**
	 * Set the activation function. A SOM uses a linear activation function, so
	 * this method throws an error.
	 * 
	 * @param activation
	 *            The activation function to use.
	 */
	public void setActivationFunction(final ActivationFunction activation) {
		final String str = "A SOM network can't define an activation function.";
		if (this.logger.isErrorEnabled()) {
			this.logger.error(str);
		}
		throw new PatternError(str);

	}

	/**
	 * Set the input neuron count.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	public void setInputNeurons(final int count) {
		this.inputNeurons = count;

	}

	/**
	 * Set the output neuron count.
	 * 
	 * @param count
	 *            The number of neurons.
	 */
	public void setOutputNeurons(final int count) {
		this.outputNeurons = count;
	}
}
