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

package org.encog.solve.genetic.genes;

/**
 * A gene that holds a single character.
 */
public class CharGene extends BasicGene {

	/**
	 * The character value of the gene.
	 */
	private char value;

	/**
	 * Copy another gene to this gene.
	 * @param gene The source gene.
	 */
	@Override
	public void copy(final Gene gene) {
		this.value = ((CharGene) gene).getValue();
	}

	/**
	 * @return The value of this gene.
	 */
	public char getValue() {
		return this.value;
	}

	/**
	 * Set the value of this gene.
	 * @param value The new value of this gene.
	 */
	public void setValue(final char value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "" + this.value;
	}
}
