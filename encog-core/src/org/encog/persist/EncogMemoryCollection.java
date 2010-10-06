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

package org.encog.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.parse.tags.read.ReadXML;
import org.encog.persist.location.PersistenceLocation;
import org.encog.persist.persistors.PersistorUtil;

/**
 * A memory based collection of Encog objects. Does not use the more complex
 * temp file structure like EncogPersistedCollection, but also can't handle
 * gigantic files. This class can load and save from/to Encog EG files.
 */
public class EncogMemoryCollection implements EncogCollection {

	/**
	 * The location this collection is saved at.
	 */
	private PersistenceLocation location;
	
	/**
	 * The contents of this collection.
	 */
	private final Map<String, EncogPersistedObject> contents = new HashMap<String, EncogPersistedObject>();

	/**
	 * Populated during a load, the file version.
	 */
	private int fileVersion;

	/**
	 * Populated during a load, the Encog version that created this file.
	 */
	private String encogVersion;

	/**
	 * Populated during a load, the platform that this was loaded to.
	 */
	private String platform;

	/**
	 * The elements in this collection.
	 */
	private final List<DirectoryEntry> directory = new ArrayList<DirectoryEntry>();

	/**
	 * Add an element to the collection.
	 * 
	 * @param name
	 *            The name of the element being added.
	 * @param obj
	 *            The object to be added.
	 */
	public void add(final String name, final EncogPersistedObject obj) {
		this.contents.put(name, obj);
		obj.setCollection(this);
		obj.setName(name);
		buildDirectory();
	}

	/**
	 * Build the directory. This allows the contents of the collection to be
	 * listed.
	 */
	public void buildDirectory() {
		this.directory.clear();
		for (final EncogPersistedObject obj : this.contents.values()) {
			final DirectoryEntry entry = new DirectoryEntry(obj);
			this.directory.add(entry);
		}
	}

	/**
	 * Clear all elements from the collection.
	 */
	public void clear() {
		this.contents.clear();
		buildDirectory();
	}

	/**
	 * Delete an object from the collection.
	 * 
	 * @param object
	 *            The object to be deleted.
	 */
	public void delete(final DirectoryEntry object) {
		this.contents.remove(object.getName());
		buildDirectory();
	}

	/**
	 * Delete a key from the collection.
	 * 
	 * @param key
	 *            The key to delete.
	 */
	public void delete(final String key) {
		this.contents.remove(key);
		buildDirectory();
	}

	/**
	 * Determine if the specified key exists.
	 * 
	 * @param key
	 *            The key.
	 * @return True, if the key exists.
	 */
	public boolean exists(final String key) {
		return this.contents.containsKey(key);
	}

	/**
	 * Find the object that corresponds to the specified directory entry, return
	 * null, if not found.
	 * 
	 * @param entry
	 *            The entry to find.
	 * @return The object that was found, or null, if no object was found.
	 */
	public EncogPersistedObject find(final DirectoryEntry entry) {
		return this.contents.get(entry.getName());
	}

	/**
	 * Find the object that corresponds to the specified key.
	 * 
	 * @param key
	 *            The key to search for.
	 * @return The object that corresponds to the specified key.
	 */
	public EncogPersistedObject find(final String key) {
		return this.contents.get(key);
	}

	/**
	 * @return The objects that were loaded from this file.
	 */
	public Map<String, EncogPersistedObject> getContents() {
		return this.contents;
	}

	/**
	 * @return A list of all the objects in the collection, specified by
	 *         DirectoryEntry objects.
	 */
	public List<DirectoryEntry> getDirectory() {

		return this.directory;
	}

	/**
	 * @return The version of Encog that this file was created with.
	 */
	public String getEncogVersion() {
		return this.encogVersion;
	}

	/**
	 * @return The Encog file version.
	 */
	public int getFileVersion() {
		return this.fileVersion;
	}

	/**
	 * @return The platform that this file was created on.
	 */
	public String getPlatform() {
		return this.platform;
	}

	/**
	 * Load the contents of a location.
	 * 
	 * @param location
	 *            The location to load from.
	 */
	public void load(final PersistenceLocation location) {
		PersistReader reader = null;

		try {
			this.location = location;
			reader = new PersistReader(location);
			final Map<String, String> header = reader.readHeader();
			if (header != null) {
				this.fileVersion = Integer.parseInt(header.get("fileVersion"));
				this.encogVersion = header.get("encogVersion");
				this.platform = header.get("platform");
			}
			reader.advanceObjectsCollection();
			final ReadXML in = reader.getXMLInput();

			while (in.readToTag()) {
				if (in.is(PersistReader.TAG_OBJECTS, false)) {
					break;
				}

				final String type = in.getTag().getName();
				final String name = in.getTag().getAttributeValue("name");

				final Persistor persistor = PersistorUtil.createPersistor(type);

				if (persistor == null) {
					throw new PersistError("Do not know how to load: " + type);
				}
				final EncogPersistedObject obj = persistor.load(in);
				
				this.contents.put(name, obj);
				obj.setCollection(this);
			}
		} finally {
			buildDirectory();
			if (reader != null) {
				reader.close();
			}
		}

	}

	/**
	 * Save the contents of this collection to a location.
	 * 
	 * @param location
	 *            The location to save to.
	 */
	public void save(final PersistenceLocation location) {
		PersistWriter writer = null;

		writer = new PersistWriter(location);
		try {
			this.location = location;
			writer.begin();
			writer.writeHeader();
			writer.beginObjects();
			for (final EncogPersistedObject obj : this.contents.values()) {

				writer.writeObject(obj);
			}
			writer.endObjects();
		} finally {
			buildDirectory();
			writer.end();
			writer.close();
		}
	}

	/**
	 * Update the properties of an element in the collection. This allows the
	 * element to be renamed, if needed.
	 * 
	 * @param name
	 *            The name of the object that is being updated.
	 * @param newName
	 *            The new name, can be the same as the old name, if the
	 *            description is to be updated only.
	 * @param desc
	 *            The new description.
	 */
	public void updateProperties(final String name, final String newName,
			final String desc) {
		final EncogPersistedObject obj = this.contents.get(name);
		obj.setName(newName);
		obj.setDescription(desc);
		this.contents.remove(name);
		this.contents.put(newName, obj);
		buildDirectory();
	}
	
	public PersistenceLocation getLocation()
	{
		return this.location;
	}
}
