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

package org.encog.util.csv;

import java.util.StringTokenizer;

/**
 * Class used to handle lists of numbers.
 */
public final class NumberList {

	/**
	 * Get an array of double's from a string of comma separated text.
	 * @param format The format to use.
	 * @param str The string to parse.
	 * @return An array of doubles parsed from the string.
	 */
	public static synchronized double[] fromList(final CSVFormat format, final String str) {
		// first count the numbers
		int count = 0;
		final StringTokenizer tok = new StringTokenizer(str, ""
				+ format.getSeparator());
		while (tok.hasMoreTokens()) {
			tok.nextToken();
			count++;
		}

		// now allocate an object to hold that many numbers
		final double[] result = new double[count];

		// and finally parse the numbers
		int index = 0;
		final StringTokenizer tok2 = new StringTokenizer(str, ""
				+ format.getSeparator());
		while (tok2.hasMoreTokens()) {
			final String num = tok2.nextToken();
			final double value = format.parse(num);
			result[index++] = value;
		}

		return result;
	}

	/**
	 * Convert an array of doubles to a comma separated list.
	 * @param format The format to use.
	 * @param result
	 *            This string will have the values appended to it.
	 * @param data
	 *            The array of doubles to use.
	 */
	public static void toList(final CSVFormat format,
			final StringBuilder result, final double[] data) {
		result.setLength(0);
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				result.append(format.getSeparator());
			}
			result.append(format.format(data[i], 20));
		}
	}

	/**
	 * Private constructor.
	 */
	private NumberList() {

	}
}
