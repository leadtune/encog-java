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

package org.encog.neural.networks.training.cpn;

import org.encog.engine.util.ErrorCalculation;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.structure.FlatUpdateNeeded;
import org.encog.neural.networks.training.BasicTraining;
import org.encog.neural.networks.training.LearningRate;

/**
 * Used for Instar training of a CPN neural network. A CPN network is a hybrid
 * supervised/unsupervised network. The Outstar training handles the supervised
 * portion of the training.
 * 
 */
public class TrainOutstar extends BasicTraining implements LearningRate {

	/**
	 * The learning rate.
	 */
	private double learningRate;

	/**
	 * The network being trained.
	 */
	private final BasicNetwork network;

	/**
	 * The training data. Supervised training, so both input and ideal must be
	 * provided.
	 */
	private final NeuralDataSet training;

	/**
	 * If the weights have not been initialized, then they must be initialized
	 * before training begins. This will be done on the first iteration.
	 */
	private boolean mustInit = true;

	/**
	 * The parts of this CPN network.
	 */
	private final FindCPN parts;

	/**
	 * Construct the outstar trainer.
	 * 
	 * @param network
	 *            The network to train.
	 * @param training
	 *            The training data, must provide ideal outputs.
	 * @param learningRate
	 *            The learning rate.
	 */
	public TrainOutstar(final BasicNetwork network,
			final NeuralDataSet training, final double learningRate) {
		this.network = network;
		this.training = training;
		this.learningRate = learningRate;
		this.parts = new FindCPN(this.network);
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
	 * Approximate the weights based on the input values.
	 */
	private void initWeight() {
		for (int i = 0; i 
			< this.parts.getOutstarLayer().getNeuronCount(); i++) {
			int j = 0;
			for (final NeuralDataPair pair : this.training) {
				this.parts.getOutstarSynapse().getMatrix().set(j++, i,
						pair.getIdeal().getData(i));
			}
		}
		this.mustInit = false;
		this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {

		if (this.mustInit) {
			initWeight();
		}

		final ErrorCalculation error = new ErrorCalculation();

		for (final NeuralDataPair pair : this.training) {
			final NeuralData out = this.parts.getInstarSynapse().compute(
					pair.getInput());

			error.updateError(out.getData(), pair.getIdeal().getData());

			final int j = this.parts.winner(out);
			for (int i = 0; i 
				< this.parts.getOutstarLayer().getNeuronCount(); i++) {
				final double delta = this.learningRate
						* (pair.getIdeal().getData(i) - this.parts
								.getOutstarSynapse().getMatrix().get(j, i));
				this.parts.getOutstarSynapse().getMatrix().add(j, i, delta);
			}

		}

		setError(error.calculate());
		this.network.getStructure().setFlatUpdate(FlatUpdateNeeded.Flatten);
	}

	/**
	 * Set the learning rate.
	 * @param rate The new learning rate.
	 */
	public void setLearningRate(final double rate) {
		this.learningRate = rate;
	}
}
