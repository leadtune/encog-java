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

package org.encog.engine.network.activation;

import org.encog.engine.util.BoundMath;

/**
 * The softmax activation function.
 * 
 * @author jheaton
 */
public class ActivationSoftMax implements ActivationFunction {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -960489243250457611L;

	/**
	 * The parameters.
	 */
	private double[] params;
	
	/**
	 * Construct the soft-max activation function.
	 */
	public ActivationSoftMax() {
		this.params = new double[0];
	}
	
	/**
	 * @return The object cloned;
	 */
	@Override
	public ActivationFunction clone() {
		return new ActivationSoftMax();
	}

	/**
	 * @return Return false, softmax has no derivative.
	 */
	public boolean hasDerivative() {
		return true;
	}
		


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activationFunction(final double[] x, final int start,
			final int size) {
		double sum = 0;
		for (int i = start; i < start + size; i++) {
			x[i] = BoundMath.exp(x[i]);
			sum += x[i];
		}
		for (int i = start; i < start + size; i++) {
			x[i] = x[i] / sum;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double derivativeFunction(final double d) {
		return 1.0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getParamNames() {
		final String[] result = {};
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double[] getParams() {
		return this.params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParam(final int index, final double value) {
		this.params[index] = value;		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOpenCLExpression(final boolean derivative, 
			final boolean allSlopeOne) {
		return null;
	}

}
