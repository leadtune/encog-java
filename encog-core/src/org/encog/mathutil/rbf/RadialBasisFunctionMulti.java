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

package org.encog.mathutil.rbf;

/**
 * A multi-dimension RBF.
 */
public interface RadialBasisFunctionMulti {
	/**
	 * Calculate the RBF result for the specified value.
	 * 
	 * @param x
	 *            The value to be passed into the RBF.
	 * @return The RBF value.
	 */
	double calculate(double[] x);

	/**
	 * Get the center of this RBD.
	 * @param dimension The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getCenter(int dimension);

	/**
	 * Get the center of this RBD.
	 * @return The center of the RBF.
	 */
	double getPeak();

	/**
	 * Get the center of this RBD.
	 * @param dimension The dimension to get the center for.
	 * @return The center of the RBF.
	 */
	double getWidth(int dimension);
	
	/**
	 * @return The dimensions in this RBF.
	 */
	int getDimensions();

	/**
	 * Set the width.
	 * @param radius The width.
	 */
	void setWidth(double radius);	
}
