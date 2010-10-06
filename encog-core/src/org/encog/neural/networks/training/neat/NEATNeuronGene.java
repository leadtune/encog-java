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

package org.encog.neural.networks.training.neat;

import org.encog.neural.networks.synapse.neat.NEATNeuronType;
import org.encog.persist.annotations.EGAttribute;
import org.encog.solve.genetic.genes.BasicGene;
import org.encog.solve.genetic.genes.Gene;

/**
 * Implements a NEAT neuron gene.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATNeuronGene extends BasicGene {

	/**
	 * The activation response, the slope of the activation function.
	 */
	@EGAttribute
	private double activationResponse;

	/**
	 * The neuron type.
	 */
	@EGAttribute
	private NEATNeuronType neuronType;

	/**
	 * True if this is recurrent.
	 */
	@EGAttribute
	private boolean recurrent;

	/**
	 * The x-split.
	 */
	@EGAttribute
	private double splitX;

	/**
	 * The y-split.
	 */
	@EGAttribute
	private double splitY;

	/**
	 * The default constructor.
	 */
	public NEATNeuronGene() {

	}

	/**
	 * Construct a gene.
	 * 
	 * @param type
	 *            The type of neuron.
	 * @param id
	 *            The id of this gene.
	 * @param splitY
	 *            The split y.
	 * @param splitX
	 *            The split x.
	 */
	public NEATNeuronGene(final NEATNeuronType type, final long id,
			final double splitY, final double splitX) {
		this(type, id, splitY, splitX, false, 1.0);
	}

	/**
	 * Construct a neuron gene.
	 * 
	 * @param type
	 *            The type of neuron.
	 * @param id
	 *            The id of this gene.
	 * @param splitY
	 *            The split y.
	 * @param splitX
	 *            The split x.
	 * @param recurrent
	 *            True if this is a recurrent link.
	 * @param act
	 *            The activation response.
	 */
	public NEATNeuronGene(final NEATNeuronType type, final long id,
			final double splitY, final double splitX, final boolean recurrent,
			final double act) {
		this.neuronType = type;
		setId(id);
		this.splitX = splitX;
		this.splitY = splitY;
		this.recurrent = recurrent;
		this.activationResponse = act;
	}

	/**
	 * Copy another gene to this one.
	 * 
	 * @param gene
	 *            The other gene.
	 */
	public void copy(final Gene gene) {
		final NEATNeuronGene other = (NEATNeuronGene) gene;
		this.activationResponse = other.activationResponse;
		setId(other.getId());
		this.neuronType = other.neuronType;
		this.recurrent = other.recurrent;
		this.splitX = other.splitX;
		this.splitY = other.splitY;

	}

	/**
	 * @return The activation response.
	 */
	public double getActivationResponse() {
		return this.activationResponse;
	}

	/**
	 * @return The type for this neuron.
	 */
	public NEATNeuronType getNeuronType() {
		return this.neuronType;
	}

	/**
	 * @return The split x value.
	 */
	public double getSplitX() {
		return this.splitX;
	}

	/**
	 * @return The split y value.
	 */
	public double getSplitY() {
		return this.splitY;
	}

	/**
	 * @return True if this is recurrent.
	 */
	public boolean isRecurrent() {
		return this.recurrent;
	}

	/**
	 * Set the activation response.
	 * 
	 * @param activationResponse
	 *            The activation response.
	 */
	public void setActivationResponse(final double activationResponse) {
		this.activationResponse = activationResponse;
	}

	/**
	 * Set the neuron type.
	 * 
	 * @param neuronType
	 *            The neuron type.
	 */
	public void setNeuronType(final NEATNeuronType neuronType) {
		this.neuronType = neuronType;
	}

	/**
	 * Set if this is a recurrent neuron.
	 * 
	 * @param recurrent
	 *            True if this is a recurrent neuron.
	 */
	public void setRecurrent(final boolean recurrent) {
		this.recurrent = recurrent;
	}

	/**
	 * Set the split x.
	 * 
	 * @param splitX
	 *            The split x.
	 */
	public void setSplitX(final double splitX) {
		this.splitX = splitX;
	}

	/**
	 * Set the split y.
	 * 
	 * @param splitY
	 *            The split y.
	 */
	public void setSplitY(final double splitY) {
		this.splitY = splitY;
	}

}
