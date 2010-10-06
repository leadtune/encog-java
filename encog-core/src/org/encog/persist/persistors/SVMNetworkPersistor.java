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

package org.encog.persist.persistors;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import org.encog.engine.util.EngineArray;
import org.encog.mathutil.libsvm.svm;
import org.encog.mathutil.libsvm.svm_model;
import org.encog.mathutil.libsvm.svm_node;
import org.encog.mathutil.libsvm.svm_parameter;
import org.encog.mathutil.rbf.RadialBasisFunction;
import org.encog.neural.networks.layers.RadialBasisFunctionLayer;
import org.encog.neural.networks.svm.SVMNetwork;
import org.encog.parse.tags.read.ReadXML;
import org.encog.parse.tags.write.WriteXML;
import org.encog.persist.EncogPersistedCollection;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;

/**
 * Persist a SVM network.
 */
public class SVMNetworkPersistor implements Persistor {

	/**
	 * Constants for the SVM types.
	 */
	public static final String svm_type_table[] = { "c_svc", "nu_svc",
			"one_class", "epsilon_svr", "nu_svr", };

	/**
	 * Constants for the kernel types.
	 */
	public static final String kernel_type_table[] = { "linear", "polynomial",
			"rbf", "sigmoid", "precomputed" };

	/**
	 * The input tag.
	 */
	public static final String TAG_INPUT = "input";

	/**
	 * The output tag.
	 */
	public static final String TAG_OUTPUT = "output";

	/**
	 * The models tag.
	 */
	public static final String TAG_MODELS = "models";

	/**
	 * The data tag.
	 */
	public static final String TAG_DATA = "Data";
	
	/**
	 * The row tag.
	 */
	public static final String TAG_ROW = "Row";
	
	/**
	 * The model tag.
	 */
	public static final String TAG_MODEL = "Model";
	
	/**
	 * The type of SVM this is.
	 */
	public final static String TAG_TYPE_SVM = "typeSVM";
	
	/**
	 * The type of kernel to use.
	 */
	public final static String TAG_TYPE_KERNEL = "typeKernel";
	
	/**
	 * The degree to use.
	 */
	public final static String TAG_DEGREE = "degree";
	
	/**
	 * The gamma to use.
	 */
	public final static String TAG_GAMMA = "gamma";
	
	/**
	 * The coefficient.
	 */
	public final static String TAG_COEF0 = "coef0";
	
	/**
	 * The number of classes.
	 */
	public final static String TAG_NUMCLASS = "numClass";
	
	/**
	 * The total number of cases.
	 */
	public final static String TAG_TOTALSV = "totalSV";
	
	/**
	 * The rho to use.
	 */
	public final static String TAG_RHO = "rho";
	
	/**
	 * The labels.
	 */
	public final static String TAG_LABEL = "label";
	
	/**
	 * The A-probability.
	 */
	public final static String TAG_PROB_A = "probA";

	/**
	 * The B-probability.
	 */
	public final static String TAG_PROB_B = "probB";
	
	/**
	 * The number of support vectors.
	 */
	public final static String TAG_NSV = "nSV";

	/**
	 * Load the SVM network.
	 * @param in Where to read it from.
	 * @return The loaded object.
	 */
	@Override
	public EncogPersistedObject load(ReadXML in) {
		SVMNetwork result = null;
		int input = -1, output = -1;

		final String name = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_NAME);
		final String description = in.getTag().getAttributes().get(
				EncogPersistedCollection.ATTRIBUTE_DESCRIPTION);

		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_INPUT, true)) {
				input = Integer.parseInt(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_OUTPUT, true)) {
				output = Integer.parseInt(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_MODELS, true)) {
				result = new SVMNetwork(input, output, false);
				handleModels(in, result);
			} else if (in.is(EncogPersistedCollection.TYPE_SVM, false)) {
				break;
			}
		}

		result.setName(name);
		result.setDescription(description);
		return result;
	}

	/**
	 * Load the models.
	 * @param in Where to read the models from.
	 * @param network Where the models are read into.
	 */
	private void handleModels(ReadXML in, SVMNetwork network) {

		int index = 0;
		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_MODEL, true)) {
				svm_parameter param = new svm_parameter();
				svm_model model = new svm_model();
				model.param = param;
				network.getModels()[index] = model;
				handleModel(in, network.getModels()[index]);
				index++;
			} else if (in.is(SVMNetworkPersistor.TAG_MODELS, false)) {
				break;
			}
		}

	}

	/**
	 * Handle a model.
	 * @param in Where to read the model from.
	 * @param model Where to load the model into.
	 */
	private void handleModel(ReadXML in, svm_model model) {
		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_TYPE_SVM, true)) {
				int i = EngineArray.findStringInArray(
						SVMNetworkPersistor.svm_type_table, in.readTextToTag());
				model.param.svm_type = i;
			} else if (in.is(SVMNetworkPersistor.TAG_DEGREE, true)) {
				model.param.degree = Integer.parseInt(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_GAMMA, true)) {
				model.param.gamma = Double.parseDouble(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_COEF0, true)) {
				model.param.coef0 = Double.parseDouble(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_NUMCLASS, true)) {
				model.nr_class = Integer.parseInt(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_TOTALSV, true)) {
				model.l = Integer.parseInt(in.readTextToTag());
			} else if (in.is(SVMNetworkPersistor.TAG_RHO, true)) {
				int n = model.nr_class * (model.nr_class - 1) / 2;
				model.rho = new double[n];
				StringTokenizer st = new StringTokenizer(in.readTextToTag());
				for (int i = 0; i < n; i++)
					model.rho[i] = Double.parseDouble(st.nextToken());
			} else if (in.is(SVMNetworkPersistor.TAG_LABEL, true)) {
				int n = model.nr_class;
				model.label = new int[n];
				StringTokenizer st = new StringTokenizer(in.readTextToTag());
				for (int i = 0; i < n; i++)
					model.label[i] = Integer.parseInt(st.nextToken());
			} else if (in.is(SVMNetworkPersistor.TAG_PROB_A, true)) {
				int n = model.nr_class * (model.nr_class - 1) / 2;
				model.probA = new double[n];
				StringTokenizer st = new StringTokenizer(in.readTextToTag());
				for (int i = 0; i < n; i++)
					model.probA[i] = Double.parseDouble(st.nextToken());
			} else if (in.is(SVMNetworkPersistor.TAG_PROB_B, true)) {
				int n = model.nr_class * (model.nr_class - 1) / 2;
				model.probB = new double[n];
				StringTokenizer st = new StringTokenizer(in.readTextToTag());
				for (int i = 0; i < n; i++)
					model.probB[i] = Double.parseDouble(st.nextToken());
			} else if (in.is(SVMNetworkPersistor.TAG_NSV, true)) {
				int n = model.nr_class;
				model.nSV = new int[n];
				StringTokenizer st = new StringTokenizer(in.readTextToTag());
				for (int i = 0; i < n; i++)
					model.nSV[i] = Integer.parseInt(st.nextToken());
			} else if (in.is(SVMNetworkPersistor.TAG_TYPE_KERNEL, true)) {
				int i = EngineArray.findStringInArray(
						SVMNetworkPersistor.kernel_type_table, in
								.readTextToTag());
				model.param.kernel_type = i;
			} else if (in.is(SVMNetworkPersistor.TAG_DATA, true)) {
				handleData(in, model);
			} else if (in.is(SVMNetworkPersistor.TAG_MODEL, false)) {
				break;
			}
		}
	}

	/**
	 * Load the data from a model.
	 * @param in Where to read the data from.
	 * @param model The model to load data into.
	 */
	private void handleData(ReadXML in, svm_model model) {
		int i = 0;
		int m = model.nr_class - 1;
		int l = model.l;

		model.sv_coef = new double[m][l];
		model.SV = new svm_node[l][];

		while (in.readToTag()) {
			if (in.is(SVMNetworkPersistor.TAG_ROW, true)) {
				String line = in.readTextToTag();

				StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

				for (int k = 0; k < m; k++)
					model.sv_coef[k][i] = Double.parseDouble(st.nextToken());
				int n = st.countTokens() / 2;
				model.SV[i] = new svm_node[n];
				for (int j = 0; j < n; j++) {
					model.SV[i][j] = new svm_node();
					model.SV[i][j].index = Integer.parseInt(st.nextToken());
					model.SV[i][j].value = Double.parseDouble(st.nextToken());
				}
				i++;
			} else if (in.is(SVMNetworkPersistor.TAG_DATA, false)) {
				break;
			}
		}
	}

	/**
	 * Save a model.
	 * @param out Where to save a model to.
	 * @param model The model to save to.
	 */
	public static void saveModel(WriteXML out, svm_model model) {
		if (model != null) {
			out.beginTag(SVMNetworkPersistor.TAG_MODEL);

			svm_parameter param = model.param;

			out.addProperty(SVMNetworkPersistor.TAG_TYPE_SVM,
					svm_type_table[param.svm_type]);
			out.addProperty(SVMNetworkPersistor.TAG_TYPE_KERNEL,
					kernel_type_table[param.kernel_type]);

			if (param.kernel_type == svm_parameter.POLY) {
				out.addProperty(SVMNetworkPersistor.TAG_DEGREE, param.degree);
			}

			if (param.kernel_type == svm_parameter.POLY
					|| param.kernel_type == svm_parameter.RBF
					|| param.kernel_type == svm_parameter.SIGMOID) {
				out.addProperty(SVMNetworkPersistor.TAG_GAMMA, param.gamma);
			}

			if (param.kernel_type == svm_parameter.POLY
					|| param.kernel_type == svm_parameter.SIGMOID) {
				out.addProperty(SVMNetworkPersistor.TAG_COEF0, param.coef0);
			}

			int nr_class = model.nr_class;
			int l = model.l;

			out.addProperty(SVMNetworkPersistor.TAG_NUMCLASS, nr_class);
			out.addProperty(SVMNetworkPersistor.TAG_TOTALSV, l);

			out.addProperty(SVMNetworkPersistor.TAG_RHO, model.rho, nr_class
					* (nr_class - 1) / 2);
			out.addProperty(SVMNetworkPersistor.TAG_LABEL, model.label,
					nr_class);
			out.addProperty(SVMNetworkPersistor.TAG_PROB_A, model.probA,
					nr_class * (nr_class - 1) / 2);
			out.addProperty(SVMNetworkPersistor.TAG_PROB_B, model.probB,
					nr_class * (nr_class - 1) / 2);
			out.addProperty(SVMNetworkPersistor.TAG_NSV, model.nSV, nr_class);

			out.beginTag(SVMNetworkPersistor.TAG_DATA);

			double[][] sv_coef = model.sv_coef;
			svm_node[][] SV = model.SV;

			StringBuilder line = new StringBuilder();
			for (int i = 0; i < l; i++) {
				line.setLength(0);
				for (int j = 0; j < nr_class - 1; j++)
					line.append(sv_coef[j][i] + " ");

				svm_node[] p = SV[i];
				if (param.kernel_type == svm_parameter.PRECOMPUTED)
					line.append("0:" + (int) (p[0].value));
				else
					for (int j = 0; j < p.length; j++)
						line.append(p[j].index + ":" + p[j].value + " ");
				out.addProperty(SVMNetworkPersistor.TAG_ROW, line.toString());
			}

			out.endTag();
			out.endTag();

		}
	}

	/**
	 * Save a SVMNetwork.
	 * @param obj The object to save.
	 * @param out Where to save it to.
	 */
	@Override
	public void save(EncogPersistedObject obj, WriteXML out) {
		PersistorUtil.beginEncogObject(EncogPersistedCollection.TYPE_SVM, out,
				obj, true);
		final SVMNetwork net = (SVMNetwork) obj;

		out.addProperty(SVMNetworkPersistor.TAG_INPUT, net.getInputCount());
		out.addProperty(SVMNetworkPersistor.TAG_OUTPUT, net.getOutputCount());
		out.beginTag(SVMNetworkPersistor.TAG_MODELS);
		for (int i = 0; i < net.getModels().length; i++) {
			saveModel(out, net.getModels()[i]);
		}
		out.endTag();
		out.endTag();
	}

}
