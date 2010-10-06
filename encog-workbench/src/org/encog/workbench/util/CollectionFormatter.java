/*
 * Encog(tm) Workbench v2.5
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
package org.encog.workbench.util;

import java.util.Collection;

/**
 * A class that contains collection formatting utilities.
 *
 */
public class CollectionFormatter {
	
	/**
	 * Private constructor
	 */
	private CollectionFormatter()
	{
		
	}
	
	/**
	 * Format a collection as a string with commas.
	 * @param collection The collection to format.
	 * @return The collection formatted with commas.
	 */
	public static String formatCollection(Collection<?> collection)
	{
		StringBuilder result = new StringBuilder();
		for(Object obj: collection)
		{
			if( result.length()!=0)
				result.append(',');
			result.append(obj.toString());
		}
		return result.toString();
	}
}
