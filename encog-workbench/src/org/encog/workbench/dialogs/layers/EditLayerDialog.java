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
package org.encog.workbench.dialogs.layers;

import java.awt.Frame;

import org.encog.workbench.dialogs.common.BuildingListField;
import org.encog.workbench.dialogs.common.BuildingListListener;
import org.encog.workbench.dialogs.common.EncogPropertiesDialog;
import org.encog.workbench.dialogs.common.IntegerField;

public class EditLayerDialog extends EncogPropertiesDialog implements BuildingListListener {

	private BuildingListField tags;
	private IntegerField neuronCount;
	
	public EditLayerDialog(Frame owner) {
		super(owner);
		addProperty(this.neuronCount = new IntegerField("neuron count",
				"Neuron Count", true, 1, 100000));
		addProperty(this.tags = new BuildingListField("tags",
		"Tags"));
	}
	
	public void add(BuildingListField list, int index) {
		AddEditTagDialog dialog = new AddEditTagDialog(this);
		if( dialog.process() )
		{
			String tag = dialog.getTag().getSelectedValue().toString();
			if( !list.getModel().contains(tag) )
				list.getModel().addElement(tag);
		}
		
	}

	public void del(BuildingListField list, int index) {
		if (index != -1) {
			list.getModel().remove(index);
		}
		
	}

	public void edit(BuildingListField list, int index) {
		
		AddEditTagDialog dialog = new AddEditTagDialog(this);
		if( dialog.process() )
		{
			// delete
			if (index != -1) {
				list.getModel().remove(index);
			}
			
			// add
			String tag = dialog.getTag().getSelectedValue().toString();
			if( !list.getModel().contains(tag) )
				list.getModel().addElement(tag);
		}
		
	}

	public BuildingListField getTags() {
		return tags;
	}

	public IntegerField getNeuronCount() {
		return neuronCount;
	}
	
	
	

}
