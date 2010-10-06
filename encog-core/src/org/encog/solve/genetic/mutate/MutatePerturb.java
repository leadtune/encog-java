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

package org.encog.solve.genetic.mutate;

import org.encog.solve.genetic.genes.DoubleGene;
import org.encog.solve.genetic.genes.Gene;
import org.encog.solve.genetic.genome.Chromosome;

/**
 * A simple mutation based on random numbers.
 */
public class MutatePerturb implements Mutate {

	/**
	 * The amount to perturb by.
	 */
	private final double perturbAmount;

	/**
	 * Construct a perturb mutation.
	 * @param perturbAmount The amount to mutate by(percent).
	 */
	public MutatePerturb(final double perturbAmount) {
		this.perturbAmount = perturbAmount;
	}

	/**
	 * Perform a perturb mutation on the specified chromosome.
	 * @param chromosome The chromosome to mutate.
	 */
	public void performMutation(final Chromosome chromosome) {
		for (final Gene gene : chromosome.getGenes()) {
			if (gene instanceof DoubleGene) {
				final DoubleGene doubleGene = (DoubleGene) gene;
				double value = doubleGene.getValue();
				value += (perturbAmount - (Math.random() * perturbAmount * 2));
				doubleGene.setValue(value);
			}
		}
	}
}
