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

package org.encog.neural.networks.training.simple;

import org.encog.engine.util.ErrorCalculation;
import org.encog.neural.NeuralNetworkError;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;

/**
 * Train an ADALINE neural network.
 */
public class TrainAdaline extends BasicTraining implements LearningRate {

	/**
	 * The network to train.
	 */
	private final BasicNetwork network;

	/**
	 * The synapse to train.
	 */
	private final Synapse synapse;

	/**
	 * The training data to use.
	 */
	private final NeuralDataSet training;

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * Construct an ADALINE trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data.
	 * @param learningRate
	 *            The learning rate.
	 */
	public TrainAdaline(final BasicNetwork network,
			final NeuralDataSet training, final double learningRate) {
		if (network.getStructure().getLayers().size() > 2) {
			throw new NeuralNetworkError(
					"An ADALINE network only has two layers.");
		}
		this.network = network;

		final Layer input = network.getLayer(BasicNetwork.TAG_INPUT);

		this.synapse = input.getNext().get(0);
		this.training = training;
		this.learningRate = learningRate;
	}

	/**
	 * @return The learning rate.
	 */
	public double getLearningRate() {
		return this.learningRate;
	}

	/**
	 * @return The network being trained.
	 */
	public BasicNetwork getNetwork() {
		return this.network;
	}

	/**
	 * Perform a training iteration.
	 */
	public void iteration() {

		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final Layer inputLayer = this.network.getLayer(BasicNetwork.TAG_INPUT);
		final Layer outputLayer = this.network
				.getLayer(BasicNetwork.TAG_OUTPUT);

		for (final NeuralDataPair pair : this.training) {
			// calculate the error
			final NeuralData output = this.network.compute(pair.getInput());

			for (int currentAdaline = 0; currentAdaline < output.size(); 
				currentAdaline++) {
				final double diff = pair.getIdeal().getData(currentAdaline)
						- output.getData(currentAdaline);

				// weights
				for (int i = 0; i < inputLayer.getNeuronCount(); i++) {
					final double input = pair.getInput().getData(i);
					this.synapse.getMatrix().add(i, currentAdaline,
							this.learningRate * diff * input);
				}

				// bias
				double t = outputLayer.getBiasWeight(currentAdaline);
				t += this.learningRate * diff;
				outputLayer.setBiasWeight(currentAdaline, t);
			}
			
			this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);

			errorCalculation.updateError(output.getData(), pair.getIdeal().getData());
		}

		// set the global error
		setError(errorCalculation.calculate());
	}

	/**
	 * Set the learning rate.
	 * 
	 * @param rate
	 *            The new learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}

}
