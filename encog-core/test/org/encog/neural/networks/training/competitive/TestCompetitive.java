/*
 * Encog(tm) Unit Tests v2.5 - Java Version
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

package org.encog.neural.networks.training.competitive;

import junit.framework.TestCase;

import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.training.competitive.neighborhood.NeighborhoodSingle;
import org.encog.neural.pattern.SOMPattern;
import org.encog.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;

public class TestCompetitive extends TestCase  {

	public static double SOM_INPUT[][] = { { 0.0, 0.0, 1.0, 1.0 },
			{ 1.0, 1.0, 0.0, 0.0 } };
	
	// Just a random starting matrix, but it gives us a constant starting point
	public static final double[][] MATRIX_ARRAY = {
			{0.9950675732277183, -0.09315692732658198}, 
			{0.9840257865083011, 0.5032129897356723}, 
			{-0.8738960119753589, -0.48043680531294997}, 
			{-0.9455207768842442, -0.8612565984447569}
			};

	private Synapse findSynapse(BasicNetwork network)
	{
		Layer input = network.getLayer(BasicNetwork.TAG_INPUT);
		return input.getNext().get(0);
	}
	
	@Test
	public void testSOM() {
		Logging.stopConsoleLogging();

		// create the training set
		final NeuralDataSet training = new BasicNeuralDataSet(
				TestCompetitive.SOM_INPUT, null);

		// Create the neural network.
		SOMPattern pattern = new SOMPattern();
		pattern.setInputNeurons(4);
		pattern.setOutputNeurons(2);
		BasicNetwork network = pattern.generate();
		
		Synapse synapse = findSynapse(network);
		synapse.setMatrix(new Matrix(MATRIX_ARRAY));

		final CompetitiveTraining train = new CompetitiveTraining(network, 0.4,
				training, new NeighborhoodSingle());
		train.setForceWinner(true);
		int iteration = 0;

		for (iteration = 0; iteration <= 100; iteration++) {
			train.iteration();
		}

		final NeuralData data1 = new BasicNeuralData(
				TestCompetitive.SOM_INPUT[0]);
		final NeuralData data2 = new BasicNeuralData(
				TestCompetitive.SOM_INPUT[1]);
		
		int result1 = network.winner(data1);
		int result2 = network.winner(data2);
		
		Assert.assertTrue(result1!=result2);

	}

}
