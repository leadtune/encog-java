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
package org.encog.workbench.process.generate;

import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.dialogs.GenerateCode;
import org.encog.workbench.frames.TextFrame;
import org.encog.workbench.process.validate.ValidateTraining;

public class CodeGeneration {

	public void processCodeGeneration() {
		final GenerateCode dialog = new GenerateCode(EncogWorkBench
				.getInstance().getMainWindow());
		if (dialog.process()) {

			final TextFrame text = new TextFrame("Generated code", true, false);

			Generate gen = null;

			switch (dialog.getLanguage()) {
			case Java:
				gen = new GenerateJava();
				break;
			case CS:
				gen = new GenerateCSharp();
				break;
			case VB:
				gen = new GenerateVB();
				break;
			}

			if (gen != null) {
				final String source = gen.generate(dialog.getNetwork());
				text.setText(source);
				text.setVisible(true);
			}
		}
	}


}
