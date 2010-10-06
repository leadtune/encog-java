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

package org.encog.mathutil.randomize;

import org.encog.EncogError;
import org.encog.mathutil.matrices.Matrix;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.synapse.Synapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A randomizer that attempts to create starting weight values that are
 * conducive to propagation training.
 * 
 * This is one of the best randomizers offered in Encog, however, the Nguyen
 * Widrow method generally performs better.
 * 
 * From:
 * 
 * Neural Networks - A Comprehensive Foundation, Haykin, chapter 6.7
 * 
 * @author jheaton
 * 
 */
public class FanInRandomizer extends BasicRandomizer {

	/**
	 * Error message. Can't use fan-in on a single number.
	 */
	static final String ERROR = "To use FanInRandomizer you must "
			+ "present a Matrix or 2D array type value.";

	/**
	 * The default boundary.
	 */
	private static final double DEFAULT_BOUNDARY = 2.4;

	/** The lower bound. */
	private final double lowerBound;

	/** The upper bound. */
	private final double upperBound;

	/**
	 * Should the square root of the number of rows be used?
	 */
	private final boolean sqrt;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Create a fan-in randomizer with default values.
	 */
	public FanInRandomizer() {
		this(-FanInRandomizer.DEFAULT_BOUNDARY,
				FanInRandomizer.DEFAULT_BOUNDARY, false);
	}

	/**
	 * Construct a fan-in randomizer along the specified boundary. The min will
	 * be -boundary and the max will be boundary.
	 * 
	 * @param boundary
	 *            The boundary for the fan-in.
	 * @param sqrt
	 *            Should the square root of the rows to be used in the
	 *            calculation.
	 */
	public FanInRandomizer(final double boundary, final boolean sqrt) {
		this(-boundary, boundary, sqrt);

	}

	/**
	 * Construct a fan-in randomizer. Use the specified bounds.
	 * 
	 * @param aLowerBound
	 *            The lower bound.
	 * @param anUpperBound
	 *            The upper bound.
	 * @param sqrt
	 *            True if the square root of the rows should be used in the
	 *            calculation.
	 */
	public FanInRandomizer(final double aLowerBound, final double anUpperBound,
			final boolean sqrt) {
		this.lowerBound = aLowerBound;
		this.upperBound = anUpperBound;
		this.sqrt = sqrt;
	}

	/**
	 * Calculate the fan-in value.
	 * 
	 * @param rows
	 *            The number of rows.
	 * @return The fan-in value.
	 */
	private double calculateValue(final int rows) {
		double rowValue;

		if (this.sqrt) {
			rowValue = Math.sqrt(rows);
		} else {
			rowValue = rows;
		}

		return (this.lowerBound / rowValue) + Math.random()
				* ((this.upperBound - this.lowerBound) / rowValue);
	}

	/**
	 * Throw an error if this class is used improperly.
	 */
	private void causeError() {
		if (this.logger.isErrorEnabled()) {
			this.logger.error(FanInRandomizer.ERROR);
		}
		throw new EncogError(FanInRandomizer.ERROR);
	}

	/**
	 * Randomize a synapse, only randomize those connections that are actually
	 * connected.
	 * 
	 * @param network
	 *            The network the synapse belongs to.
	 * @param synapse
	 *            The synapse to randomize.
	 */
	@Override
	public void randomize(final BasicNetwork network, final Synapse synapse) {
		if (synapse.getMatrix() != null) {
			boolean limited = network.getStructure().isConnectionLimited();
			final double[][] d = synapse.getMatrix().getData();
			for (int fromNeuron = 0; fromNeuron 
				< synapse.getMatrix().getRows(); fromNeuron++) {
				for (int toNeuron = 0; toNeuron 
					< synapse.getMatrix().getCols(); toNeuron++) {
					if (!limited
							|| network.isConnected(synapse, fromNeuron,
									toNeuron)) {
						d[fromNeuron][toNeuron] = calculateValue(synapse
								.getMatrix().getRows());
					}
				}
			}

		}
	}

	/**
	 * Starting with the specified number, randomize it to the degree specified
	 * by this randomizer. This could be a totally new random number, or it
	 * could be based on the specified number.
	 * 
	 * @param d
	 *            The number to randomize.
	 * @return A randomized number.
	 */
	public double randomize(final double d) {
		causeError();
		return 0;
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = calculateValue(1);
		}
	}

	/**
	 * Randomize the array based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final Double[] d) {
		for (int i = 0; i < d.length; i++) {
			d[i] = calculateValue(1);
		}
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}

	/**
	 * Randomize the 2d array based on an array, modify the array. Previous
	 * values may be used, or they may be discarded, depending on the
	 * randomizer.
	 * 
	 * @param d
	 *            An array to randomize.
	 */
	@Override
	public void randomize(final Double[][] d) {
		for (int row = 0; row < d.length; row++) {
			for (int col = 0; col < d[0].length; col++) {
				d[row][col] = calculateValue(d.length);
			}
		}
	}

	/**
	 * Randomize the matrix based on an array, modify the array. Previous values
	 * may be used, or they may be discarded, depending on the randomizer.
	 * 
	 * @param m
	 *            A matrix to randomize.
	 */
	@Override
	public void randomize(final Matrix m) {
		for (int row = 0; row < m.getRows(); row++) {
			for (int col = 0; col < m.getCols(); col++) {
				m.set(row, col, calculateValue(m.getRows()));
			}
		}
	}

}
