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

package org.encog.neural.networks.training.propagation;

import java.util.HashMap;
import java.util.Map;

import org.encog.persist.BasicPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.TrainingContinuationPersistor;

/**
 * Allows training to be continued.
 * 
 */
public class TrainingContinuation extends BasicPersistedObject {

	/**
	 * The serial ID.
	 */
	private static final long serialVersionUID = -3649790586015301015L;
			
	/**
	 * The contents of this object.
	 */
	private final Map<String, Object> contents = new HashMap<String, Object>();

	/**
	 * @return A persistor for this object.
	 */
	public Persistor createPersistor() {
		return new TrainingContinuationPersistor();
	}

	/**
	 * Get an object by name.
	 * @param name The name of the object.
	 * @return The object requested.
	 */
	public Object get(final String name) {
		return this.contents.get(name);
	}

	/**
	 * @return The contents.
	 */
	public Map<String, Object> getContents() {
		return this.contents;
	}

	/**
	 * Save a list of doubles.
	 * @param key The key to save them under.
	 * @param list The list of doubles.
	 */
	public void put(final String key, final double[] list) {
		this.contents.put(key, list);
	}

	/**
	 * Set a value to a string.
	 * @param name The value to set.
	 * @param value The value.
	 */
	public void set(final String name, final Object value) {
		this.contents.put(name, value);
	}
}
