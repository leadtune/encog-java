/*
 * Encog(tm) Workbench v2.5
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
package org.encog.workbench.process.validate;

import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.Layer;
import org.encog.neural.networks.logic.FeedforwardLogic;
import org.encog.neural.networks.logic.SimpleRecurrentLogic;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.encog.neural.networks.synapse.neat.NEATSynapse;
import org.encog.workbench.EncogWorkBench;

public class ValidateTraining {

	BasicNetwork network;
	NeuralDataSet training;

	public ValidateTraining(final BasicNetwork network,
			final NeuralDataSet training) {
		this.network = network;
		this.training = training;
	}

	@SuppressWarnings("unchecked")
	public boolean validateContainsLayer(final Class layerType) {
		for (final Layer layer : this.network.getStructure().getLayers()) {
			if (layer.getClass().getName().equals(layerType.getName())) {
				return true;
			}
		}

		EncogWorkBench.displayError("Training Error",
				"This sort of training requires that at least one layer be of type:\n"
						+ layerType.getSimpleName());
		return false;
	}
	
	public boolean validateFeedforwardOrSRN()
	{
		if( this.network.getLogic().getClass() != FeedforwardLogic.class &&
			this.network.getLogic().getClass() != SimpleRecurrentLogic.class ) {
			EncogWorkBench.displayError("Training Error",
			"This sort of training requires either feed forward or simple recurrent logic.\n");	
			return false;
		}
		
		for( Synapse synapse: this.network.getStructure().getSynapses() ) {
			if( synapse instanceof NEATSynapse ) {
				EncogWorkBench.displayError("Training Error",
				"This sort of training will not work with a NEAT synapse.\n");	
				return false;
			}
		}
			
		return true;
	}
	
	public boolean validateLogicType(final Class logicType) {
		if (this.network.getLogic().getClass().getName().equals(logicType.getName())) {
			return true;
		}

		EncogWorkBench.displayError("Training Error",
				"This sort of training requires neural logic type of:\n"
						+ logicType.getSimpleName());
		return false;
	}



	public boolean validateInputSize() {		
		int inputNeurons=0;
		
		Layer layer = this.network.getLayer(BasicNetwork.TAG_INPUT);
		
		if( layer!=null )
			inputNeurons = layer.getNeuronCount();
		
		final int trainingInputs = this.training.getInputSize();

		if (inputNeurons != trainingInputs) {

			EncogWorkBench.displayError("Training Error",
					"Training input size must match the number of input neurons.\n Input neurons:"
							+ inputNeurons + "\nTraining Input Size: "
							+ trainingInputs);
			return false;
		}
		return true;
	}

	public boolean validateIsSupervised() {
		if (!this.training.isSupervised()) {
			EncogWorkBench
					.displayError(
							"Training Error",
							"This sort of training requires a suprvised training set,\n which means that it must have ideal data provided.");
			return false;
		}

		return true;
	}

	public boolean validateIsUnsupervised() {
		if (this.training.isSupervised()) {
			EncogWorkBench
					.displayError(
							"Training Error",
							"This sort of training requires an unsuprvised training set,\n which means that it must not have ideal data provided.");
			return false;
		}

		return true;
	}

	public boolean validateOutputSize() {
		int outputNeurons = 0;
		Layer layer = this.network.getLayer(BasicNetwork.TAG_OUTPUT);
		
		if( layer!=null )
			outputNeurons = layer.getNeuronCount();
		
		
		final int trainingOutputs = this.training.getIdealSize();

		if (outputNeurons != trainingOutputs) {

			EncogWorkBench
					.displayError(
							"Training Error",
							"Training ideal size must match the number of output neurons.\n Output neurons:"
									+ outputNeurons
									+ "\nTraining Ideal Size: "
									+ trainingOutputs);
			return false;
		}
		return true;
	}

	public boolean validateInputAndOuputSizes()
	{
		if(!validateOutputSize() )
			return false;
		
		if(!validateInputSize() )
			return false;
		
		return true;
		
	}

	public boolean validateNEAT() {
		
		boolean problem = false;
		
		if( this.network.getStructure().getLayers().size()!=2 )
			problem = true;

		else
		{
			Layer input = this.network.getLayer(BasicNetwork.TAG_INPUT);
			if( input==null )
				problem = true;
			else
			{
				Synapse synapse = input.getNext().get(0);
				if( !(synapse instanceof NEATSynapse) ) {
					problem = true;
				}
			}
		}
		
		
		if( problem )
		{
			EncogWorkBench.displayError(
					"Training Error", 
					"Only works with a NEAT network, which is a 2-layer network with a NEAT synapse.");
			return false;
		}
		return true;
	}

	public boolean validateSVM() {
		if( !(this.network instanceof SVMNetwork) ) {
			EncogWorkBench.displayError(
					"Training Error", 
					"This training only works with a Support Vector Machine (SVM).");
			return false;
		}
		
		return true;
	}
	
	public boolean validateHasLayers()
	{
		if( this.network.getLayer(BasicNetwork.TAG_INPUT)==null || this.network.getLayer(BasicNetwork.TAG_OUTPUT)==null )
		{
			EncogWorkBench.displayError(
					"Training Error", 
					"This training only works with a neural network that has both an input and output layer.");
			return false;
		}
		return true;
	}


}
