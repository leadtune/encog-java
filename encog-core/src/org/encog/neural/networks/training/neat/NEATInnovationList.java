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
import org.encog.persist.annotations.EGReference;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;
import org.encog.solve.genetic.innovation.BasicInnovationList;
import org.encog.solve.genetic.innovation.Innovation;
import org.encog.solve.genetic.population.Population;

/**
 * Implements a NEAT innovation list.
 * 
 * NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the
 * generation of evolving artificial neural networks. It was developed by Ken
 * Stanley while at The University of Texas at Austin.
 * 
 * http://www.cs.ucf.edu/~kstanley/
 * 
 */
public class NEATInnovationList extends BasicInnovationList {

	/**
	 * The next neuron id.
	 */
	private long nextNeuronID = 0;

	/**
	 * The population.
	 */
	@EGReference
	private Population population;

	/**
	 * The default constructor, used mainly for persistance.
	 */
	public NEATInnovationList() {

	}

	/**
	 * Construct an innovation list.
	 * 
	 * @param population
	 *            The population.
	 * @param links
	 *            The links.
	 * @param neurons
	 *            THe neurons.
	 */
	public NEATInnovationList(final Population population,
			final Chromosome links, final Chromosome neurons) {

		this.population = population;
		for (final Gene gene : neurons.getGenes()) {
			final NEATNeuronGene neuronGene = (NEATNeuronGene) gene;

			final NEATInnovation innovation = new NEATInnovation(neuronGene,
					population.assignInnovationID(), assignNeuronID());
			add(innovation);
		}

		for (final Gene gene : links.getGenes()) {
			final NEATLinkGene linkGene = (NEATLinkGene) gene;
			final NEATInnovation innovation = new NEATInnovation(linkGene
					.getFromNeuronID(), linkGene.getToNeuronID(),
					NEATInnovationType.NewLink, this.population
							.assignInnovationID());
			add(innovation);

		}
	}

	/**
	 * Assign a neuron ID.
	 * 
	 * @return The neuron id.
	 */
	private long assignNeuronID() {
		return this.nextNeuronID++;
	}

	/**
	 * Check to see if we already have an innovation.
	 * 
	 * @param in
	 *            The input neuron.
	 * @param out
	 *            THe output neuron.
	 * @param type
	 *            The type.
	 * @return The innovation, either new or existing if found.
	 */
	public NEATInnovation checkInnovation(final long in, final long out,
			final NEATInnovationType type) {
		for (final Innovation i : getInnovations()) {
			final NEATInnovation innovation = (NEATInnovation) i;
			if ((innovation.getFromNeuronID() == in)
					&& (innovation.getToNeuronID() == out)
					&& (innovation.getInnovationType() == type)) {
				return innovation;
			}
		}

		return null;
	}

	/**
	 * Create a new neuron gene from an id.
	 * 
	 * @param neuronID
	 *            The neuron id.
	 * @return The neuron gene.
	 */
	public NEATNeuronGene createNeuronFromID(final long neuronID) {
		final NEATNeuronGene result = new NEATNeuronGene(NEATNeuronType.Hidden,
				0, 0, 0);

		for (final Innovation i : getInnovations()) {
			final NEATInnovation innovation = (NEATInnovation) i;
			if (innovation.getNeuronID() == neuronID) {
				result.setNeuronType(innovation.getNeuronType());
				result.setId(innovation.getNeuronID());
				result.setSplitY(innovation.getSplitY());
				result.setSplitX(innovation.getSplitX());

				return result;
			}
		}

		return result;
	}

	/**
	 * Create a new innovation.
	 * 
	 * @param in
	 *            The input neuron.
	 * @param out
	 *            The output neuron.
	 * @param type
	 *            The type.
	 */
	public void createNewInnovation(final long in, final long out,
			final NEATInnovationType type) {
		final NEATInnovation newInnovation = new NEATInnovation(in, out, type,
				this.population.assignInnovationID());

		if (type == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(assignNeuronID());
		}

		add(newInnovation);
	}

	/**
	 * Create a new innovation.
	 * 
	 * @param from
	 *            The from neuron.
	 * @param to
	 *            The to neuron.
	 * @param innovationType
	 *            THe innovation type.
	 * @param neuronType
	 *            The neuron type.
	 * @param x
	 *            The x-coordinate.
	 * @param y
	 *            The y-coordinate.
	 * @return The new innovation.
	 */
	public long createNewInnovation(final long from, final long to,
			final NEATInnovationType innovationType,
			final NEATNeuronType neuronType, final double x, final double y) {
		final NEATInnovation newInnovation = new NEATInnovation(from, to,
				innovationType, this.population.assignInnovationID(),
				neuronType, x, y);

		if (innovationType == NEATInnovationType.NewNeuron) {
			newInnovation.setNeuronID(assignNeuronID());
		}

		add(newInnovation);

		return (this.nextNeuronID - 1); // ??????? should it be innov?
	}
}
