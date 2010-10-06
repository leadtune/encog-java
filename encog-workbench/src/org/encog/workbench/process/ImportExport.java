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
package org.encog.workbench.process;

import java.io.File;

import org.encog.neural.data.buffer.BinaryDataLoader;
import org.encog.neural.data.buffer.codec.CSVDataCODEC;
import org.encog.neural.data.buffer.codec.DataSetCODEC;
import org.encog.neural.data.buffer.codec.ExcelCODEC;
import org.encog.util.csv.CSVFormat;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.dialogs.ImportExportDialog;
import org.encog.workbench.dialogs.binary.DialogBinary2External;
import org.encog.workbench.dialogs.binary.DialogCSV;
import org.encog.workbench.dialogs.binary.DialogExternal2Binary;
import org.encog.workbench.util.ExtensionFilter;
import org.encog.workbench.util.TaskComplete;

public class ImportExport {

	public static void performBin2External() {
		performBin2External(null, null);
	}

	public static File performExternal2Bin(File inBinaryFile, TaskComplete done) {

		File binaryFile = inBinaryFile;

		DialogExternal2Binary dialog = new DialogExternal2Binary(EncogWorkBench
				.getInstance().getMainWindow());

		if (binaryFile != null) {
			dialog.getBinaryFile().setValue(binaryFile.toString());
		}

		if (dialog.process()) {
			binaryFile = new File(dialog.getBinaryFile().getValue());
			File externFile = new File(dialog.getExternalFile().getValue());
			int fileType = dialog.getFileType().getSelectedIndex();
			int inputCount = dialog.getInputCount().getValue();
			int idealCount = dialog.getIdealCount().getValue();

			// no extension
			if (ExtensionFilter.getExtension(binaryFile) == null) {
				binaryFile = new File(binaryFile.getPath() + ".egb");
			}

			DataSetCODEC codec;
			BinaryDataLoader loader;

			if (fileType == 0) {
				DialogCSV dialog2 = new DialogCSV(EncogWorkBench.getInstance()
						.getMainWindow());
				if (dialog2.process()) {
					boolean headers = dialog2.getHeaders().getValue();
					CSVFormat format;

					if (dialog2.getDecimalComma().getValue())
						format = CSVFormat.DECIMAL_COMMA;
					else
						format = CSVFormat.DECIMAL_POINT;

					codec = new CSVDataCODEC(externFile, format, headers,
							inputCount, idealCount);
					loader = new BinaryDataLoader(codec);
					ImportExportDialog dlg = new ImportExportDialog(loader,
							binaryFile, true);
					dlg.process(done);
				}
			} else if (fileType == 1) {
				codec = new ExcelCODEC(externFile, 
						inputCount, idealCount);
				loader = new BinaryDataLoader(codec);
				ImportExportDialog dlg = new ImportExportDialog(loader,
						binaryFile, true);
				dlg.process(done);
			}
			return binaryFile;
		} else
			return null;
	}

	public static void performBin2External(File inBinaryFile, TaskComplete done) {
		DialogBinary2External dialog = new DialogBinary2External(EncogWorkBench
				.getInstance().getMainWindow());
		
		if( inBinaryFile!=null )
			dialog.getBinaryFile().setValue(inBinaryFile.toString());
		if (dialog.process()) {
			File binaryFile = new File(dialog.getBinaryFile().getValue());
			File externFile = new File(dialog.getExternalFile().getValue());
			int fileType = dialog.getFileType().getSelectedIndex();
			DataSetCODEC codec;
			BinaryDataLoader loader;

			if (fileType == 0) {
				DialogCSV dialog2 = new DialogCSV(EncogWorkBench.getInstance()
						.getMainWindow());
				if (dialog2.process()) {
					boolean headers = dialog2.getHeaders().getValue();
					CSVFormat format;

					if (dialog2.getDecimalComma().getValue())
						format = CSVFormat.DECIMAL_COMMA;
					else
						format = CSVFormat.DECIMAL_POINT;

					codec = new CSVDataCODEC(externFile, format);
					loader = new BinaryDataLoader(codec);

					ImportExportDialog dlg = new ImportExportDialog(loader,
							binaryFile, false);
					dlg.process(done);
				}
			} else if (fileType == 1) {
				codec = new ExcelCODEC(externFile);
				loader = new BinaryDataLoader(codec);

				ImportExportDialog dlg = new ImportExportDialog(loader,
						binaryFile, false);
				dlg.process(done);

			}
		}

	}

}
