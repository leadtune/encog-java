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
package org.encog.workbench.frames.document;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.engine.util.ErrorCalculation;
import org.encog.engine.util.ErrorCalculationMode;
import org.encog.engine.util.Format;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.PropertyData;
import org.encog.neural.data.TextData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.data.buffer.BufferedNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.neural.networks.training.neat.NEATGenome;
import org.encog.normalize.DataNormalization;
import org.encog.persist.DirectoryEntry;
import org.encog.persist.EncogPersistedCollection;
import org.encog.script.EncogScript;
import org.encog.solve.genetic.population.BasicPopulation;
import org.encog.util.file.Directory;
import org.encog.workbench.EncogWorkBench;
import org.encog.workbench.config.EncogWorkBenchConfig;
import org.encog.workbench.dialogs.BenchmarkDialog;
import org.encog.workbench.dialogs.EditEncogObjectProperties;
import org.encog.workbench.dialogs.EvaluateDialog;
import org.encog.workbench.dialogs.PopulationDialog;
import org.encog.workbench.dialogs.config.EncogConfigDialog;
import org.encog.workbench.dialogs.createobject.CreateObjectDialog;
import org.encog.workbench.dialogs.createobject.ObjectType;
import org.encog.workbench.dialogs.newdoc.CreateNewDocument;
import org.encog.workbench.dialogs.trainingdata.CreateTrainingDataDialog;
import org.encog.workbench.dialogs.trainingdata.TrainingDataType;
import org.encog.workbench.frames.EncogCommonFrame;
import org.encog.workbench.frames.query.NetworkQueryFrame;
import org.encog.workbench.process.CreateNeuralNetwork;
import org.encog.workbench.process.CreateTrainingData;
import org.encog.workbench.process.cloud.CloudProcess;
import org.encog.workbench.process.generate.CodeGeneration;
import org.encog.workbench.process.validate.ResourceNameValidate;
import org.encog.workbench.tabs.BinaryDataTab;
import org.encog.workbench.tabs.BrowserFrame;
import org.encog.workbench.tabs.EncogScriptTab;
import org.encog.workbench.tabs.PropertyDataTab;
import org.encog.workbench.tabs.SVMTab;
import org.encog.workbench.tabs.TextDataTab;
import org.encog.workbench.tabs.TrainingDataTab;
import org.encog.workbench.tabs.network.NetworkTab;
import org.encog.workbench.tabs.population.PopulationTab;
import org.encog.workbench.util.ExtensionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncogDocumentOperations {

	private EncogDocumentFrame owner;
	private int browser = 0;

	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public EncogDocumentOperations(EncogDocumentFrame owner) {
		this.owner = owner;
	}

	public void openItem(final Object item) {

		DirectoryEntry entry = (DirectoryEntry) item;
		if (EncogWorkBench.getInstance().getCurrentFile().find(entry) == null) {
			EncogWorkBench.displayError("Object", "Can't find that object.");
			return;
		}

		if (entry.getType().equals(EncogPersistedCollection.TYPE_TRAINING)) {

			if (owner.getTabManager().checkBeforeOpen(entry,
					TrainingDataTab.class)) {
				BasicNeuralDataSet set = (BasicNeuralDataSet) EncogWorkBench
						.getInstance().getCurrentFile().find(entry);
				final TrainingDataTab tab = new TrainingDataTab(set);
				this.owner.openTab(tab);
			}
		} else if (entry.getType().equals(
				EncogPersistedCollection.TYPE_BASIC_NET)) {

			final DirectoryEntry net = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(net, BasicNetwork.class)) {
				BasicNetwork net2 = (BasicNetwork) EncogWorkBench.getInstance()
						.getCurrentFile().find(net);
				final NetworkTab tab = new NetworkTab(net2);
				this.owner.openTab(tab);
			}
		} else if (entry.getType().equals(EncogPersistedCollection.TYPE_TEXT)) {
			DirectoryEntry text = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(text, TextData.class)) {
				TextData text2 = (TextData) EncogWorkBench.getInstance()
						.getCurrentFile().find(text);
				final TextDataTab tab = new TextDataTab(text2);
				this.owner.openTab(tab);
			}
		} else if (entry.getType().equals(
				EncogPersistedCollection.TYPE_PROPERTY)) {
			DirectoryEntry prop = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(prop, PropertyData.class)) {
				PropertyData prop2 = (PropertyData) EncogWorkBench
						.getInstance().getCurrentFile().find(prop);
				final PropertyDataTab tab = new PropertyDataTab(owner
						.getDocumentTabs(), prop2);
				owner.openTab(tab);
			}
		} else if (entry.getType().equals(
				EncogPersistedCollection.TYPE_POPULATION)) {
			DirectoryEntry prop = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(prop,
					BasicPopulation.class)) {
				BasicPopulation pop2 = (BasicPopulation) EncogWorkBench
						.getInstance().getCurrentFile().find(prop);
				final PopulationTab tab = new PopulationTab(pop2);
				owner.openTab(tab);
			}
		} else if (entry.getType().equals(EncogPersistedCollection.TYPE_SVM)) {
			DirectoryEntry svm = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(svm,
					SVMNetwork.class)) {
				SVMNetwork svn2 = (SVMNetwork) EncogWorkBench.getInstance()
						.getCurrentFile().find(svm);
				final SVMTab tab = new SVMTab(svn2);
				owner.openTab(tab);
			}

		} else if (entry.getType().equals(EncogPersistedCollection.TYPE_BINARY)) {
			DirectoryEntry link = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(link,
					BasicPopulation.class)) {
				BufferedNeuralDataSet link2 = (BufferedNeuralDataSet) EncogWorkBench.getInstance()
						.getCurrentFile().find(link);
				final BinaryDataTab tab = new BinaryDataTab(link2);
				owner.openTab(tab);
			}

		} else if (entry.getType().equals(EncogPersistedCollection.TYPE_SCRIPT)) {
			DirectoryEntry script = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(script,
					BasicPopulation.class)) {
				EncogScript script2 = (EncogScript) EncogWorkBench.getInstance()
						.getCurrentFile().find(script);
				final EncogScriptTab tab = new EncogScriptTab(script2);
				owner.openTab(tab);
			}

		} 		
		/*else if (entry.getType().equals(EncogPersistedCollection.TYPE_NORMALIZATION)) {
			DirectoryEntry norm = (DirectoryEntry) item;
			if (owner.getTabManager().checkBeforeOpen(norm,
					DataNormalization.class)) {
				DataNormalization norm2 = (DataNormalization) EncogWorkBench.getInstance()
						.getCurrentFile().find(norm);
				final DataNormalizationTab tab = new DataNormalizationTab(norm2);
				owner.openTab(tab);
			}

		}*/
		else {
			EncogWorkBench.displayError("Error",
					"Unknown object type.\nDo not know how to open.");
		}
	}

	public void performEditCopy() {
		final Frame frame = EncogWorkBench.getCurrentFocus();
		if (frame instanceof EncogCommonFrame) {
			final EncogCommonFrame ecf = (EncogCommonFrame) frame;
			ecf.copy();
		}

	}

	public void performEditCut() {
		final Frame frame = EncogWorkBench.getCurrentFocus();
		if (frame instanceof EncogCommonFrame) {
			final EncogCommonFrame ecf = (EncogCommonFrame) frame;
			ecf.cut();
		}
	}

	public void performEditPaste() {
		final Frame frame = EncogWorkBench.getCurrentFocus();
		if (frame instanceof EncogCommonFrame) {
			final EncogCommonFrame ecf = (EncogCommonFrame) frame;
			ecf.paste();
		}

	}

	public void performFileNew() {
		
		if (!checkSave()) {
			return;
		}

		CreateNewDocument dialog = new CreateNewDocument(EncogWorkBench.getInstance().getMainWindow());
		dialog.getParentDirectory().setValue(EncogWorkBench.getInstance().getEncogFolders().toString());
		dialog.getProjectFilename().setValue("MyEncogProject");
				
		if( dialog.process() )
		{
			File parent = new File(dialog.getParentDirectory().getValue());
			File project = new File(parent,dialog.getProjectFilename().getValue());
			Directory.deleteDirectory(project); // the user was warned!
			project.mkdir();
			File projectFile = new File(project,dialog.getProjectFilename().getValue()+".eg");
			EncogWorkBench.getInstance().close();
			EncogWorkBench.save(projectFile.toString());
		}
	}

	public void performFileOpen() {
		try {
			if (!checkSave()) {
				return;
			}

			final JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(EncogDocumentFrame.ENCOG_FILTER);
			fc.setCurrentDirectory(EncogWorkBench.getInstance().getEncogFolders());
			final int result = fc.showOpenDialog(owner);
			if (result == JFileChooser.APPROVE_OPTION) {
				EncogWorkBench.load(fc.getSelectedFile().getAbsolutePath());
			}
		}  
 
		catch (final Throwable e) {
			EncogWorkBench.displayError("Can't Open File or File Corrupt", e);
			e.printStackTrace();
			EncogWorkBench.getInstance().getMainWindow().endWait();
		}
	}

	public void performFileSave() {
		try {
			if (EncogWorkBench.getInstance().getCurrentFileName() == null) {
				performFileSaveAs();
			} else {
				EncogWorkBench.save();
			}
		} catch (final Throwable e) {
			EncogWorkBench.displayError("Can't Save File", e);
			EncogWorkBench.getInstance().getMainWindow().endWait();
		}
	}

	public void performFileSaveAs() {
		try {
			final JFileChooser fc = new JFileChooser();
			fc.setFileFilter(EncogDocumentFrame.ENCOG_FILTER);
			final int result = fc.showSaveDialog(owner);

			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				// no extension
				if (ExtensionFilter.getExtension(file) == null) {
					file = new File(file.getPath() + ".eg");
				}
				// wrong extension
				else if (ExtensionFilter.getExtension(file).compareTo("eg") != 0) {

					if (JOptionPane
							.showConfirmDialog(
									owner,
									"Encog files are usually stored with the .eg extension. \nDo you wish to save with the name you specified?",
									"Warning", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
						return;
					}
				}

				// file doesn't exist yet
				if (file.exists()) {
					final int response = JOptionPane.showConfirmDialog(null,
							"Overwrite existing file?", "Confirm Overwrite",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.CANCEL_OPTION) {
						return;
					}
				}

				EncogWorkBench.save(file.getAbsolutePath());
			}
		} catch (final Throwable e) {
			EncogWorkBench.displayError("Can't Save File", e);
		}

	}

	public void performGenerateCode() {
		final CodeGeneration code = new CodeGeneration();
		code.processCodeGeneration();

	}

	public void performNetworkQuery(DirectoryEntry item) {

		if (owner.getTabManager()
				.checkBeforeOpen(item, NetworkQueryFrame.class)) {
			BasicNetwork net = (BasicNetwork) EncogWorkBench.getInstance()
					.getCurrentFile().find(item);
			final NetworkQueryFrame frame = new NetworkQueryFrame(net);
			frame.setVisible(true);
		}

	}

	public static String generateNextID(String base) {
		int index = 1;
		String result = "";

		do {
			result = base + index;
			index++;
		} while (EncogWorkBench.getInstance().getCurrentFile().exists(result));

		return result;
	}

	public void performObjectsCreate() throws IOException {

		try {
			CreateObjectDialog dialog = new CreateObjectDialog(EncogWorkBench
					.getInstance().getMainWindow());

			dialog.setType(ObjectType.NeuralNetwork);

			if (!dialog.process())
				return;

			String name = dialog.getResourceName();
			
			switch (dialog.getType()) {
			case DataNormalization:
				final DataNormalization norm = new DataNormalization();
				EncogWorkBench.getInstance().getCurrentFile().add(name, norm);
				EncogWorkBench.getInstance().getMainWindow().redraw();
				break;
			case EncogScript:
				final EncogScript script = new EncogScript();
				script.setDescription("An Encog script");
				script.setSource("console.println(\'Hello World\')\n");
				EncogWorkBench.getInstance().getCurrentFile().add(name, script);
				EncogWorkBench.getInstance().getMainWindow().redraw();
				break;				
			case NeuralNetwork:
				CreateNeuralNetwork.process(name);
				break;
			case NEATPopulation:
				performCreatePopulation();
				break;
			case PropertyData:
				final PropertyData prop = new PropertyData();
				prop.setDescription("Some property data");
				EncogWorkBench.getInstance().getCurrentFile().add(name, prop);
				EncogWorkBench.getInstance().getMainWindow().redraw();
				break;
			case Text:
				final TextData text = new TextData();
				text.setDescription("A text file");
				text.setText("Insert text here.");
				EncogWorkBench.getInstance().getCurrentFile().add(name, text);
				EncogWorkBench.getInstance().getMainWindow().redraw();
				break;
			case TrainingData:
				performCreateTrainingData(name);
				break;
			}

		} catch (EncogError t) {
			EncogWorkBench.displayError("Error creating object", t);
			logger.error("Error creating object", t);
		}
	}

	private void performCreatePopulation() {
		PopulationDialog dialog = new PopulationDialog(owner);

		if (dialog.process()) {
			int populationSize = dialog.getPopulationSize().getValue();
			BasicPopulation pop = new BasicPopulation(populationSize);

			for (int i = 0; i < populationSize; i++) {
				pop.add(new NEATGenome(null, pop.assignGenomeID(), dialog
						.getInputNeurons().getValue(), dialog
						.getOutputNeurons().getValue()));
			}

			pop.setDescription("Population");
			EncogWorkBench.getInstance().getCurrentFile().add(
					generateNextID("population-"), pop);
			EncogWorkBench.getInstance().getMainWindow().redraw();
		}

	}

	public void performObjectsDelete(DirectoryEntry selected) {

		if (owner.getTabManager().find(selected) != null) {
			EncogWorkBench.displayError("Can't Delete Object",
					"This object can not be deleted while it is open.");
			return;
		}

		EncogWorkBench.getInstance().getCurrentFile().delete(selected);
		EncogWorkBench.getInstance().getMainWindow().redraw();
	}

	public void performBrowse() {
		BrowserFrame browse = new BrowserFrame();
		this.owner.openTab(browse, "Browser" + (browser++));
	}

	public void performHelpAbout() {
		// AboutEncog dialog = new AboutEncog();
		// dialog.process();
		EncogWorkBench.getInstance().getMainWindow().displayAboutTab();
	}

	boolean checkSave() {
		final String currentFileName = EncogWorkBench.getInstance()
				.getCurrentFileName();
		
		if( EncogWorkBench.getInstance().getCurrentFile()==null )
			return true;

		if (currentFileName != null
				|| EncogWorkBench.getInstance().getCurrentFile().getDirectory()
						.size() > 0) {
			final String f = currentFileName != null ? currentFileName
					: "Untitled";
			final int response = JOptionPane.showConfirmDialog(null, "Save "
					+ f + ", first?", "Save", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				performFileSave();
				return true;
			} else if (response == JOptionPane.NO_OPTION) {
				return true;
			} else {
				return false;
			}

		}

		return true;
	}

	public void performEditConfig() {
		// ObjectEditorFrame config = new
		// ObjectEditorFrame(EncogWorkBench.getInstance().getConfig());
		// config.setVisible(true);

		EncogConfigDialog dialog = new EncogConfigDialog(EncogWorkBench
				.getInstance().getMainWindow());

		EncogWorkBenchConfig config = EncogWorkBench.getInstance().getConfig();

		dialog.getUserID().setValue(config.getEncogCloudUserID());
		dialog.getPassword().setValue(config.getEncogCloudPassword());
		dialog.getDefaultError().setValue(config.getDefaultError());
		dialog.getNetwork().setValue(config.getEncogCloudNetwork());
		dialog.getAutoConnect().setValue(config.isAutoConnect());
		dialog.getThreadCount().setValue(config.getThreadCount());
		dialog.getUseOpenCL().setValue(config.isUseOpenCL());
		switch (config.getErrorCalculation()) {
		case RMS:
			((JComboBox) dialog.getErrorCalculation().getField())
					.setSelectedIndex(0);
			break;
		case MSE:
			((JComboBox) dialog.getErrorCalculation().getField())
					.setSelectedIndex(1);
			break;
		case ARCTAN:
			((JComboBox) dialog.getErrorCalculation().getField())
					.setSelectedIndex(2);
			break;
		}

		if (dialog.process()) {
			config.setEncogCloudUserID(dialog.getUserID().getValue());
			config.setEncogCloudPassword(dialog.getPassword().getValue());
			config.setDefaultError(dialog.getDefaultError().getValue());
			config.setAutoConnect(dialog.getAutoConnect().getValue());
			config.setEncogCloudNetwork(dialog.getNetwork().getValue());
			config.setThreadCount(dialog.getThreadCount().getValue());
			config.setUseOpenCL(dialog.getUseOpenCL().getValue());
			switch(((JComboBox) dialog.getErrorCalculation().getField()).getSelectedIndex())
			{
				case 0:
					config.setErrorCalculation(ErrorCalculationMode.RMS);
					break;
				case 1:
					config.setErrorCalculation(ErrorCalculationMode.MSE);
					break;
				case 2:
					config.setErrorCalculation(ErrorCalculationMode.ARCTAN);
					break;
			}
			EncogWorkBench.saveConfig();

			ErrorCalculation.setMode(EncogWorkBench.getInstance().getConfig()
					.getErrorCalculation());

			if (config.isUseOpenCL() && Encog.getInstance().getCL() == null) {
				EncogWorkBench.initCL();
				if (Encog.getInstance().getCL() != null) {
					EncogWorkBench
							.displayMessage("OpenCL",
									"Success, your graphics card(s) are now ready to help train neural networks.");
				}
			} else if (!EncogWorkBench.getInstance().getConfig().isUseOpenCL()
					&& Encog.getInstance().getCL() != null) {
				EncogWorkBench
						.displayMessage(
								"OpenCL",
								"Encog Workbench will stop using your GPU the next time\nthe workbench is restarted.");
			}

		}
	}

	public void performObjectsProperties(DirectoryEntry selected) {

		final EditEncogObjectProperties dialog = new EditEncogObjectProperties(
				owner);
		dialog.getNameField().setValue(selected.getName());
		dialog.getDescription().setValue(selected.getDescription());
		if (dialog.process()) {
			
			String error = ResourceNameValidate.validateResourceName(dialog.getNameField().getValue());
			
			if (error != null) {
				EncogWorkBench.displayError("Data Error",
						error);
				return;
			}

			EncogWorkBench.getInstance().getCurrentFile().updateProperties(
					selected.getName(), dialog.getNameField().getValue(),
					dialog.getDescription().getValue());
			EncogWorkBench.getInstance().getMainWindow().redraw();
		}
	}

	public void performFileRevert() {
		if (EncogWorkBench.askQuestion("Revert",
				"Would you like to revert to the last time you saved?")) {
			EncogWorkBench.load(EncogWorkBench.getInstance()
					.getCurrentFileName());
			EncogWorkBench.getInstance().getCurrentFile().buildDirectory();
			EncogWorkBench.getInstance().getMainWindow().redraw();
		}

	}

	public void performEvaluate() {
		try {
			EvaluateDialog dialog = new EvaluateDialog(EncogWorkBench
					.getInstance().getMainWindow());
			if (dialog.process()) {
				BasicNetwork network = dialog.getNetwork();
				NeuralDataSet training = dialog.getTrainingSet();

				double error = network.calculateError(training);
				EncogWorkBench.displayMessage("Error For this Network", ""
						+ Format.formatPercent(error));
			}
		} catch (Throwable t) {
			EncogWorkBench.displayError("Error Evaluating Network", t);
		}

	}

	public void performBenchmark() {
		if (EncogWorkBench
				.askQuestion(
						"Benchmark",
						"Would you like to benchmark Encog on this machine?\nThis process will take several minutes to complete.")) {
			BenchmarkDialog dialog = new BenchmarkDialog();
			dialog.setVisible(true);
		}

	}

	public void performCreateTrainingData(String name) throws IOException {
		CreateTrainingDataDialog dialog = new CreateTrainingDataDialog(
				EncogWorkBench.getInstance().getMainWindow());

		dialog.setType(TrainingDataType.Empty);	

		if (dialog.process()) {		
			performLinkTraining(dialog.getType(),name);
		}
	}
	
	public void performLinkTraining(TrainingDataType type, String name) throws IOException
	{
		switch (type) {
		case Empty:
			CreateTrainingData.linkEmpty(name);
			break;
		case ImportCSV:
			CreateTrainingData.linkCSV(name);
			break;
		case MarketWindow:
			CreateTrainingData.linkMarketWindow(name);
			break;
		case PredictWindow:
			CreateTrainingData.linkPredictWindow(name);
			break;
		case Random:
			CreateTrainingData.linkRandom(name);
			break;
		case XORTemp:
			CreateTrainingData.linkXORTemp(name);
			break;
		case XOR:
			CreateTrainingData.linkXOR(name);
			break;
		}
		
	}

	public void performCloudLogin() {
		CloudProcess.Login();

	}

	public void performQuit() {
		try {
			EncogWorkBench.saveConfig();
			if (EncogWorkBench.getInstance().getCloud() != null)
				EncogWorkBench.getInstance().getCloud().logout();
		} catch (Throwable t) {

		}
		System.exit(0);

	}
}
