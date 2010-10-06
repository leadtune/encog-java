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

package org.encog.neural.networks.training.propagation.scg;

import org.encog.engine.network.train.prop.TrainFlatNetworkResilient;
import org.encog.engine.network.train.prop.TrainFlatNetworkSCG;
import org.encog.engine.util.BoundNumbers;
import org.encog.engine.util.EngineArray;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.structure.NetworkCODEC;
import org.encog.neural.networks.training.propagation.Propagation;

/**
 * This is a training class that makes use of scaled conjugate 
 * gradient methods.  It is a very fast and efficient training
 * algorithm.
 *
 */
public class ScaledConjugateGradient extends Propagation {


	/**
	 * Construct a training class.
	 * @param network The network to train.
	 * @param training The training data.
	 */
	public ScaledConjugateGradient(final BasicNetwork network,
			final NeuralDataSet training) {
		super(network, training);

		TrainFlatNetworkSCG rpropFlat = new TrainFlatNetworkSCG(
				network.getStructure().getFlat(),
				this.getTraining()); 
		this.setFlatTraining( rpropFlat );
	}


}
