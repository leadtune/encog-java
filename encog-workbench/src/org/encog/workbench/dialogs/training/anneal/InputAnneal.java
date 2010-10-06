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
package org.encog.workbench.dialogs.training.anneal;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.encog.workbench.dialogs.common.ComboBoxField;
import org.encog.workbench.dialogs.common.DoubleField;
import org.encog.workbench.dialogs.common.IntegerField;
import org.encog.workbench.dialogs.common.ValidationException;
import org.encog.workbench.dialogs.training.BasicTrainingInput;

/**
 * Dialog box to input data for simulated annealing training.
 */
public class InputAnneal extends BasicTrainingInput {

	/**
	 * The serial id for this class.
	 */
	private static final long serialVersionUID = 1L;
	
	private DoubleField startTemp;
	private DoubleField endTemp;
	private IntegerField cycles;
	
	/**
	 * Construct the dialog box.
	 * @param owner The owner of the dialog box.
	 */
	public InputAnneal(final Frame owner) {
		super(owner);
		setTitle("Train Simulated Annealing");

		addProperty(this.startTemp = new DoubleField("starting temperature","Starting Temperature",true,-1,-1));
		addProperty(this.endTemp = new DoubleField("ending temperature","Ending Temperature",true,-1,-1));
		addProperty(this.cycles = new IntegerField("cycles","Ending Temperature",true,0,-1));
		render();
		
		this.startTemp.setValue(1);
		this.endTemp.setValue(20);
		this.cycles.setValue(10);
	}

	public DoubleField getStartTemp() {
		return startTemp;
	}

	public DoubleField getEndTemp() {
		return endTemp;
	}

	public IntegerField getCycles() {
		return cycles;
	}

	

}
