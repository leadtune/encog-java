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
package org.encog.workbench.process.training;

import java.awt.Frame;

import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.Format;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.logic.SOMLogic;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.training.svm.SVMTrain;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.dialogs.training.ChooseTrainingMethodDialog;
import org.encog.workbench.dialogs.training.adaline.InputAdaline;
import org.encog.workbench.dialogs.training.adaline.ProgressAdaline;
import org.encog.workbench.dialogs.training.anneal.InputAnneal;
import org.encog.workbench.dialogs.training.anneal.ProgressAnneal;
import org.encog.workbench.dialogs.training.backpropagation.InputBackpropagation;
import org.encog.workbench.dialogs.training.backpropagation.ProgressBackpropagation;
import org.encog.workbench.dialogs.training.genetic.InputGenetic;
import org.encog.workbench.dialogs.training.genetic.ProgressGenetic;
import org.encog.workbench.dialogs.training.instar.InputInstar;
import org.encog.workbench.dialogs.training.instar.ProgressInstar;
import org.encog.workbench.dialogs.training.lma.InputLMA;
import org.encog.workbench.dialogs.training.lma.ProgressLMA;
import org.encog.workbench.dialogs.training.manhattan.InputManhattan;
import org.encog.workbench.dialogs.training.manhattan.ProgressManhattan;
import org.encog.workbench.dialogs.training.neat.InputNEAT;
import org.encog.workbench.dialogs.training.neat.ProgressNEAT;
import org.encog.workbench.dialogs.training.outstar.InputOutstar;
import org.encog.workbench.dialogs.training.outstar.ProgressOutstar;
import org.encog.workbench.dialogs.training.resilient.InputResilient;
import org.encog.workbench.dialogs.training.resilient.ProgressResilient;
import org.encog.workbench.dialogs.training.scg.InputSCG;
import org.encog.workbench.dialogs.training.scg.ProgressSCG;
import org.encog.workbench.dialogs.training.som.InputSOM;
import org.encog.workbench.dialogs.training.som.ProgressSOM;
import org.encog.workbench.dialogs.training.svm.ImputSearchSVM;
import org.encog.workbench.dialogs.training.svm.InputSVM;
import org.encog.workbench.dialogs.training.svm.ProgressSVM;
import org.encog.workbench.process.validate.ValidateTraining;

public class Training {
	
	private BasicNetwork network;
	private NeuralDataSet training;
	
	public void initTraining(NeuralDataSet training, BasicNetwork network)
	{
	}
		
	public void performAnneal() {
		final InputAnneal dialog = new InputAnneal(EncogWorkBench.getInstance()
				.getMainWindow());
		if (dialog.process()) {			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(),  dialog.getTrainingSet());

			if (!validate.validateIsSupervised() ) {
				return;
			}		
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}


			final ProgressAnneal train = new ProgressAnneal(EncogWorkBench
					.getInstance().getMainWindow(), network, training, dialog
					.getMaxError().getValue(),
					dialog.getStartTemp().getValue(), dialog.getEndTemp()
							.getValue(), dialog.getCycles().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Anneal");
		}
	}

	public void performBackpropagation() {
		final InputBackpropagation dialog = new InputBackpropagation(
				EncogWorkBench.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(),  dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}

			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final ProgressBackpropagation train = new ProgressBackpropagation(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getLearningRate().getValue(), dialog
							.getMomentum().getValue(), dialog.getMaxError()
							.getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Backprop");
		}
	}

	public void performGenetic() {
		final InputGenetic dialog = new InputGenetic(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}


			final ProgressGenetic train = new ProgressGenetic(EncogWorkBench
					.getInstance().getMainWindow(), network, training, dialog
					.getMaxError().getValue(), dialog.getPopulationSize()
					.getValue(), dialog.getMutationPercent().getValue(), dialog
					.getPercentToMate().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Genetic");
		}
	}

	public void performSOM() {
		final InputSOM dialog = new InputSOM(EncogWorkBench.getInstance()
				.getMainWindow());

		if (dialog.process()) {
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());

			if (!validate.validateIsUnsupervised()) {
				return;
			}
			
			if( !validate.validateLogicType(SOMLogic.class)) {
				return;
			}
			
			if( !validate.validateInputSize() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}


			final ProgressSOM train = new ProgressSOM(EncogWorkBench
					.getInstance().getMainWindow(), network, training, dialog
					.getMaxError().getValue(), dialog.getLearningRate()
					.getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "SOM");
		}

	}

	public void performResilient() {
		final InputResilient dialog = new InputResilient(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(),  dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}
			
			EncogCLDevice device = dialog.getDevice();

			final ProgressResilient train = new ProgressResilient(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getInitialUpdate().getValue(), dialog
							.getMaxStep().getValue(), dialog.getMaxError()
							.getValue());
			train.setDevice(device);

			EncogWorkBench.getInstance().getMainWindow().openModalTab(train, "RPROP");
		}

	}

	public void performManhattan() {
		final InputManhattan dialog = new InputManhattan(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);			

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}
			
			final ProgressManhattan train = new ProgressManhattan(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getFixedDelta().getValue(), dialog
							.getMaxError().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Manhattan");
		}

	}
	
	public void performADALINE()
	{
		final InputAdaline dialog = new InputAdaline(
				EncogWorkBench.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);			

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}
			
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}

			final ProgressAdaline train = new ProgressAdaline(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getLearningRate().getValue(), dialog.getMaxError()
							.getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "ADALINE");
		}
	}
	
	public void performInstar()
	{
		final InputInstar dialog = new InputInstar(
				EncogWorkBench.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final ProgressInstar train = new ProgressInstar(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getLearningRate().getValue(), dialog.getMaxError()
							.getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Instar");
		}
	}
	
	public void performOutstar()
	{
		final InputOutstar dialog = new InputOutstar(
				EncogWorkBench.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);			

			if (!validate.validateIsSupervised() ) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final ProgressOutstar train = new ProgressOutstar(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getLearningRate().getValue(), dialog.getMaxError()
							.getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "Outstar");
		}
	}

	public void perform(Frame owner, BasicNetwork network) {
		ChooseTrainingMethodDialog dialog = new ChooseTrainingMethodDialog(
				owner, network);
		
		try
		{
			if(!EncogWorkBench.getInstance().getMainWindow().getTabManager().checkTrainingOrNetworkOpen())
				return;
			
			if (dialog.process()) {
				switch (dialog.getType()) {
				case PropagationResilient:
					performResilient();
					break;
				case PropagationBack:
					performBackpropagation();
					break;
				case PropagationManhattan:
					performManhattan();
					break;
				case LevenbergMarquardt:
					performLMA();
					break;
				case Genetic:
					performGenetic();
					break;
				case Annealing:
					performAnneal();
					break;
				case SOM:
					performSOM();
					break;
				case ADALINE:
					performADALINE();
					break;
				case SCG:
					performSCG();
					break;
				case NEAT:
					performNEAT();
					break;
				case SVMSimple:
					performSVMSimple();
					break;
				case SVMCross:
					performSVMSearch();
					break;
				}
				
			}
		}
		catch(Throwable t)
		{
			EncogWorkBench.displayError("Error While Training", t, this.network, this.training);
		}
	}
	
	public void performSCG() {
		final InputSCG dialog = new InputSCG(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());

			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);			

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final ProgressSCG train = new ProgressSCG(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getMaxError().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "SCG");
		}

	}
	
	public void performLMA() {
		final InputLMA dialog = new InputLMA(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog
					.getNetwork(), dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( !validate.validateFeedforwardOrSRN() ) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final ProgressLMA train = new ProgressLMA(
					EncogWorkBench.getInstance().getMainWindow(), network,
					training, dialog.getMaxError().getValue(),
					dialog.getUseBayesian().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "LMA");
		}

	}
	
	public void performNEAT() {
		final InputNEAT dialog = new InputNEAT(EncogWorkBench
				.getInstance().getMainWindow());
		dialog.getMaxError().setValue(EncogWorkBench.getInstance().getConfig().getDefaultError());
		
		dialog.getActivationMutationRate().setValue(0.1);
		dialog.getChanceAddLink().setValue(0.07);
		dialog.getChanceAddNode().setValue(0.04);
		dialog.getChanceAddRecurrentLink().setValue(0.05);
		dialog.getCompatibilityThreshold().setValue(0.26);
		dialog.getCrossoverRate().setValue(0.7);
		dialog.getMaxActivationPerturbation().setValue(0.1);
		dialog.getMaxNumberOfSpecies().setValue(0);
		dialog.getMaxPermittedNeurons().setValue(100);
		dialog.getMaxWeightPerturbation().setValue(0.5);
		dialog.getMutationRate().setValue(0.2);
		dialog.getNumAddLinkAttempts().setValue(5);
		dialog.getNumGensAllowedNoImprovement().setValue(15);
		dialog.getNumTrysToFindLoopedLink().setValue(5);
		dialog.getNumTrysToFindOldLink().setValue(5);
		dialog.getProbabilityWeightReplaced().setValue(0.1);
		
		if (dialog.process()) {
			final ValidateTraining validate = new ValidateTraining(dialog.getNetwork(), 
				 dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			if (!validate.validateIsSupervised()) {
				return;
			}
			
			if( (!validate.validateNEAT())) {
				return;
			}
			
			if( !validate.validateInputAndOuputSizes() ) {
				return;
			}
			
			if( !validate.validateHasLayers() ) {
				return;
			}

			final NeuralDataSet training = dialog.getTrainingSet();

			final ProgressNEAT train = new ProgressNEAT(
					EncogWorkBench.getInstance().getMainWindow(),
					training,
					dialog.getPopulation(),
					dialog.getNetwork(),
					dialog.getMaxError().getValue());
			
			train.setParamActivationMutationRate(dialog.getActivationMutationRate().getValue());
			train.setParamChanceAddLink(dialog.getChanceAddLink().getValue());
			train.setParamChanceAddNode(dialog.getChanceAddNode().getValue());
			train.setParamChanceAddRecurrentLink(dialog.getChanceAddRecurrentLink().getValue());
			train.setParamCompatibilityThreshold(dialog.getCompatibilityThreshold().getValue());
			train.setParamCrossoverRate(dialog.getCrossoverRate().getValue());
			train.setParamMaxActivationPerturbation(dialog.getMaxActivationPerturbation().getValue());
			train.setParamMaxNumberOfSpecies(dialog.getMaxNumberOfSpecies().getValue());
			train.setParamMaxPermittedNeurons(dialog.getMaxPermittedNeurons().getValue());
			train.setParamMaxWeightPerturbation(dialog.getMaxWeightPerturbation().getValue());
			train.setParamMutationRate(dialog.getMutationRate().getValue());
			train.setParamNumAddLinkAttempts(dialog.getNumAddLinkAttempts().getValue());
			train.setParamNumGensAllowedNoImprovement(dialog.getNumGensAllowedNoImprovement().getValue());
			train.setParamNumTrysToFindLoopedLink(dialog.getNumTrysToFindLoopedLink().getValue());
			train.setParamNumTrysToFindOldLink(dialog.getNumTrysToFindOldLink().getValue());
			train.setParamProbabilityWeightReplaced(dialog.getProbabilityWeightReplaced().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "NEAT");
		}

	}
	
	public void performSVMSimple()
	{
		InputSVM dialog = new InputSVM(EncogWorkBench
				.getInstance().getMainWindow());
		dialog.getC().setValue(1);
		dialog.getGamma().setValue(0);
		
		if( dialog.process() )
		{
			final ValidateTraining validate = new ValidateTraining(dialog.getNetwork(), 
					dialog.getTrainingSet());
			
			this.network = dialog.getNetwork();
			this.training = dialog.getTrainingSet();
			initTraining(this.training,this.network);

			if (!validate.validateIsSupervised()) {
				return;
			}
					
			
			if( !validate.validateSVM() ) {
				return;
			}

			final NeuralDataSet trainingSet = dialog.getTrainingSet();
			final SVMNetwork network = (SVMNetwork)dialog.getNetwork();
			
			SVMTrain training = new SVMTrain(network,trainingSet);
			training.train(dialog.getGamma().getValue(),dialog.getC().getValue());
			this.network = training.getNetwork();
			EncogWorkBench.displayMessage("Training Complete", "Final error: " + 
					Format.formatPercent(this.network.calculateError(trainingSet)));
		}
	}
	
	public void performSVMSearch()
	{
		ImputSearchSVM dialog = new ImputSearchSVM(EncogWorkBench
				.getInstance().getMainWindow());
		
		dialog.getBeginningGamma().setValue(SVMTrain.DEFAULT_GAMMA_BEGIN);
		dialog.getEndingGamma().setValue(SVMTrain.DEFAULT_GAMMA_END);
		dialog.getStepGamma().setValue(SVMTrain.DEFAULT_GAMMA_STEP);
		dialog.getBeginningC().setValue(SVMTrain.DEFAULT_CONST_BEGIN);
		dialog.getEndingC().setValue(SVMTrain.DEFAULT_CONST_END);
		dialog.getStepC().setValue(SVMTrain.DEFAULT_CONST_STEP);
		
		if( dialog.process() )
		{
			final ValidateTraining validate = new ValidateTraining(dialog.getNetwork(), 
					(BasicNeuralDataSet) dialog.getTrainingSet());

			if (!validate.validateIsSupervised()) {
				return;
			}
					
			
			if( !validate.validateSVM() ) {
				return;
			}

			final NeuralDataSet trainingSet = dialog.getTrainingSet();
			final SVMNetwork network = (SVMNetwork)dialog.getNetwork();

			final ProgressSVM train = new ProgressSVM(
					EncogWorkBench.getInstance().getMainWindow(), network,
					trainingSet, 
					dialog.getBeginningGamma().getValue(),
					dialog.getEndingGamma().getValue(),
					dialog.getStepGamma().getValue(),
					dialog.getBeginningC().getValue(),
					dialog.getEndingC().getValue(),
					dialog.getEndingGamma().getValue(),
					dialog.getMaxError().getValue());

			EncogWorkBench.getInstance().getMainWindow().openTab(train, "SVM");
	

		}
	}
}
