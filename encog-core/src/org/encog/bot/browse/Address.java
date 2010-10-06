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

package org.encog.bot.browse;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A URL address. Holds both the URL object, as well as original text.
 *
 * @author jheaton
 *
 */
public class Address {
	/**
	 * The original text from the address.
	 */
	private final String original;

	/**
	 * The address as a URL.
	 */
	private URL url;

	/**
	 * The logger.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct the address from a URL.
	 *
	 * @param u
	 *            The URL to use.
	 */
	public Address(final URL u) {
		this.url = u;
		this.original = u.toString();
	}

	/**
	 * Construct a URL using a perhaps relative URL and a base URL.
	 *
	 * @param base
	 *            The base URL.
	 * @param original
	 *            A full URL or a URL relative to the base.
	 */
	public Address(final URL base, final String original) {
		this.original = original;
		try {
			this.url = new URL(base, original);
		} catch (final MalformedURLException e) {
			this.logger.debug("Malformed URL", e);
		}
	}

	/**
	 * @return The original text from this URL.
	 */
	public String getOriginal() {
		return this.original;
	}

	/**
	 * @return THe URL.
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (this.url != null) {
			return this.url.toString();
		} else {
			return this.original;
		}
	}

}
