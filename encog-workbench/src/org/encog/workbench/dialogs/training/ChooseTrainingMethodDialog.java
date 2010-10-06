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
package org.encog.workbench.dialogs.training;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.encog.neural.networks.BasicNetwork;
import org.encog.workbench.dialogs.common.EncogCommonDialog;
import org.encog.workbench.dialogs.common.ValidationException;
import org.encog.workbench.dialogs.createnetwork.NeuralNetworkType;
import org.encog.workbench.tabs.network.TrainingType;

public class ChooseTrainingMethodDialog extends EncogCommonDialog implements
		ListSelectionListener {

	private DefaultListModel model = new DefaultListModel();
	private JList list = new JList(model);
	private JTextArea text = new JTextArea();
	private JScrollPane scroll1 = new JScrollPane(list);
	private JScrollPane scroll2 = new JScrollPane(text);
	private TrainingType type;

	public ChooseTrainingMethodDialog(Frame owner, BasicNetwork network) {
		super(owner);
		setTitle("Create a Training Method");

		this.setSize(500, 250);
		this.setLocation(50, 100);
		final Container content = getBodyPanel();
		content.setLayout(new GridLayout(1, 2));

		content.add(this.scroll1);
		content.add(this.scroll2);

		this.model.addElement("Propagation - Scaled Conjugate Gradient (SCG)");
		this.model.addElement("Propagation - Resilient");
		this.model.addElement("Propagation - Backpropagation");
		this.model.addElement("Propagation - Manhattan");
		this.model.addElement("Levenberg-Marquardt");
		this.model.addElement("Genetic Algorithm");
		this.model.addElement("Simulated Annealing");
		this.model.addElement("Self Organizing Map Training(SOM)");
		this.model.addElement("ADALINE");
		this.model.addElement("Instar");
		this.model.addElement("Outstar");
		this.model.addElement("NeuroEvolution of Augmenting Topologies (NEAT)");
		this.model.addElement("Support Vector Machine - Simple");
		this.model.addElement("Support Vector Machine - Cross");

		this.list.addListSelectionListener(this);
		this.text.setLineWrap(true);
		this.text.setWrapStyleWord(true);
		this.text.setEditable(false);
		scroll2
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.type = TrainingType.PropagationResilient;
	}

	@Override
	public void collectFields() throws ValidationException {
		switch (list.getSelectedIndex()) {
		case 0:
			this.type = TrainingType.SCG;
			break;
		case 1:
			this.type = TrainingType.PropagationResilient;
			break;
		case 2:
			this.type = TrainingType.PropagationBack;
			break;
		case 3:
			this.type = TrainingType.PropagationManhattan;
			break;
		case 4:
			this.type = TrainingType.LevenbergMarquardt;
			break;	
		case 5:
			this.type = TrainingType.Genetic;
			break;
		case 6:
			this.type = TrainingType.Annealing;
			break;
		case 7:
			this.type = TrainingType.SOM;
			break;
		case 8:
			this.type = TrainingType.ADALINE;
			break;
		case 9:
			this.type = TrainingType.Instar;
			break;
		case 10:
			this.type = TrainingType.Outstar;
			break;
		case 11:
			this.type = TrainingType.NEAT;
			break;
		case 12:
			this.type = TrainingType.SVMSimple;
			break;
		case 13:
			this.type = TrainingType.SVMCross;
			break;
		}
	}

	@Override
	public void setFields() {
		switch (type) {
		case SCG:
			this.list.setSelectedIndex(0);
			break;
		case PropagationResilient:
			this.list.setSelectedIndex(1);
			break;
		case PropagationBack:
			this.list.setSelectedIndex(2);
			break;
		case PropagationManhattan:
			this.list.setSelectedIndex(3);
			break;
		case LevenbergMarquardt:
			this.list.setSelectedIndex(4);
			break;
		case Genetic:
			this.list.setSelectedIndex(5);
			break;
		case Annealing:
			this.list.setSelectedIndex(6);
			break;
		case SOM:
			this.list.setSelectedIndex(7);
			break;
		case ADALINE:
			this.list.setSelectedIndex(8);
			break;
		case Instar:
			this.list.setSelectedIndex(9);
			break;
		case Outstar:
			this.list.setSelectedIndex(10);
			break;
		case NEAT:
			this.list.setSelectedIndex(11);
			break;
		case SVMSimple:
			this.list.setSelectedIndex(12);
			break;
		case SVMCross:
			this.list.setSelectedIndex(13);
			break;
		}
	}

	public TrainingType getType() {
		return type;
	}

	public void setType(TrainingType type) {
		this.type = type;
	}

	public void valueChanged(ListSelectionEvent e) {
		switch (list.getSelectedIndex()) {
		case 0:
			this.text.setText("Scaled Conjugate Gradient (SCG) is an effective training algorithms available for Encog.  It is a supervised learning method. Training is accomplished by use of the Scaled Conjugate Gradient function minimization technique.");
			break;
		case 1:
			this.text
					.setText("Resilient Propagation is one of the fastest training algorithms available for Encog.  Resilient propagation is a supervised learning method.  It works similarly to Backpropagation, except that an individual delta is calculated for each connection.  These delta values are gradually changed until the neural network weight matrix converges on a potentially ideal weight matrix.  Resilient propagation allows several parameters to be set, but it is rare that these training parameters need to be changed beyond their default values.  Resilient propagation can be used with feedforward and simple recurrent neural networks.");
			break;
		case 2:
			this.text
					.setText("Backpropagation is one of the oldest training techniques for feedforward and simple recurrent neural networks.  It is a supervised learning method, and is an implementation of the Delta rule. The term is an abbreviation for \"backwards propagation of errors\". Backpropagation requires that the activation function used by the layers is differentiable.  For most training situations where backpropagation could be applied, resilient propagation is a better solution.");
			break;
		case 3:
			this.text
					.setText("The Manhattan update rule works similarly to Backpropagation, except a single update delta is provided.  It is a supervised learning method.  This update value must be chosen correctly for the neural network to properly train.  The Manhattan update rule can be used with feedforward and simple recurrent neural networks.  For most cases where the Manhattan update rule could be applied, Resilient propagation is a better choice.");
			break;
		case 4:
			this.text
					.setText("The Levenberg Marquardt training algorithm is one of the fastest training algorithms available for Encog.  It is based on the LevenbergMarquardt method for minimizing a function.  This training algorithm can only be used for neural networks that contain a single output neuron.  This is a supervised training method.");
			break;
		case 5:
			this.text
					.setText("A genetic algorithm trains a neural network with a process that emulates biological mutation and natural selection.  This is implemented as a supervised training method.  Many neural networks are created that will simulate different organisms.  These neural networks will compete to see which has the lowest error rate.  Those neural networks that have the lowest error rates will have elements of their weight matrix combined to produce offspring.  Some offspring will have random mutations introduced.  This cycle continues and hopefully produces lower error rates from the top neural networks with each iteration.");
			break;
		case 6:
			this.text
					.setText("Simulated annealing is a process where the weights are randomized according to a temperature.  As this temperature decreases, the weights are kept if they improve the error rate of the neural network.  This training technique is implemented as supervised training.  This process simulates the metallurgical process of annealing where metals are slowly cooled to produce a more stable molecular structure.");
			break;
		case 7:
			this.text
					.setText("Self Organizing Maps(SOM) are trained in such a way that similar input patterns will cause a single neuron to become the winner.  The winner is the neuron with the highest activation.  This is an unsupervised training technique.  The input data will be grouped into categories defined by the number of output neurons.");
			break;
		case 8:
			this.text
					.setText("An Adaptive Linear Neural (ADALINE) network is trained with a very simple method based on the delta rule.  This is a supervised training technique that allows each Adaline element to recognize a single input pattern.)");
			break;
		case 9:
			this.text
					.setText("Instar training is generally used to train a counterpropagation neural network (CPN). Instar training is unsupervised, and teaches the Instar layer of the neural network to categorize the input into a number of categories expressed by the number of neurons in the instar layer. Instar training should be done before outstar, though they are accomplished in two discrete steps.");
			break;
		case 10:
			this.text
					.setText("Outstar training is generally used to train a counterpropagation neural network (CPN).  Outstar training is supervised, and teaches the Outstar layer of the neural network to produce output that is close to the expected output.  Outstar training should be done after instar, though they are accomplished in two discrete steps.");
			break;
			
		case 11:
			this.text
					.setText("NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the generation of evolving artificial neural networks.  It is used to train a NEAT population of NEAT neural networks.  This training method does not work for regular feedforward neural networks.");
			break;
			
		case 12:
			this.text
					.setText("This training method can only be used with Support Vector Machines.  Do a very fast train of a SVM, for RBF kernel type SVM's a gamma and C will be used to train the network.  Training will be fairly quick.");
			break;	
		case 13:
			this.text.setText("This training method can only be used with Support Vector Machines that use an RBF kernel.  For other SVM's just use SVM simple.  This method will cycle through values of gamma and C searching for an optimial pair to use to train the network. ");
			break;
		}

		this.text.setSelectionStart(0);
		this.text.setSelectionEnd(0);

	}

}
