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

package org.encog.engine.network.flat;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngine;
import org.encog.engine.EncogEngineError;
import org.encog.engine.EngineNeuralNetwork;
import org.encog.engine.data.BasicEngineData;
import org.encog.engine.data.EngineData;
import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.engine.util.EngineArray;
import org.encog.engine.util.ErrorCalculation;

/**
 * Implements a flat (vector based) neural network in the Encog Engine. This is
 * meant to be a very highly efficient feedforward, or simple recurrent, neural
 * network. It uses a minimum of objects and is designed with one principal in
 * mind-- SPEED. Readability, code reuse, object oriented programming are all
 * secondary in consideration.
 * 
 * Vector based neural networks are also very good for GPU processing. The flat
 * network classes will make use of the GPU if you have enabled GPU processing.
 * See the Encog class for more info.
 */
public class FlatNetwork implements EngineNeuralNetwork {

	/**
	 * The default bias activation.
	 */
	public static final double DEFAULT_BIAS_ACTIVATION = 1.0;

	/**
	 * The value that indicates that there is no bias activation.
	 */
	public static final double NO_BIAS_ACTIVATION = 0.0;

	/**
	 * The number of input neurons in this network.
	 */
	private int inputCount;

	/**
	 * The number of neurons in each of the layers.
	 */
	private int[] layerCounts;

	/**
	 * The number of context neurons in each layer. These context neurons will
	 * feed the next layer.
	 */
	private int[] layerContextCount;

	/**
	 * The number of neurons in each layer that are actually fed by neurons in
	 * the previous layer. Bias neurons, as well as context neurons, are not fed
	 * from the previous layer.
	 */
	private int[] layerFeedCounts;

	/**
	 * An index to where each layer begins (based on the number of neurons in
	 * each layer).
	 */
	private int[] layerIndex;

	/**
	 * The outputs from each of the neurons.
	 */
	private double[] layerOutput;

	/**
	 * The number of output neurons in this network.
	 */
	private int outputCount;

	/**
	 * The index to where the weights that are stored at for a given layer.
	 */
	private int[] weightIndex;

	/**
	 * The weights for a neural network.
	 */
	private double[] weights;

	/**
	 * The activation types.
	 */
	private ActivationFunction[] activationFunctions;

	/**
	 * The context target for each layer. This is how the backwards connections
	 * are formed for the recurrent neural network. Each layer either has a
	 * zero, which means no context target, or a layer number that indicates the
	 * target layer.
	 */
	private int[] contextTargetOffset;

	/**
	 * The size of each of the context targets. If a layer's contextTargetOffset
	 * is zero, its contextTargetSize should also be zero. The contextTargetSize
	 * should always match the feed count of the targeted context layer.
	 */
	private int[] contextTargetSize;

	/**
	 * The bias activation for each layer. This is usually either 1, for a bias,
	 * or zero for no bias.
	 */
	private double[] biasActivation;

	/**
	 * The layer that training should begin on.
	 */
	private int beginTraining;

	/**
	 * The layer that training should end on.
	 */
	private int endTraining;

	/**
	 * Does this network have some connections disabled.
	 */
	private boolean isLimited;

	/**
	 * The limit, under which, all a cconnection is not considered to exist.
	 */
	private double connectionLimit;

	/**
	 * Default constructor.
	 */
	public FlatNetwork() {

	}

	/**
	 * Create a flat network from an array of layers.
	 * 
	 * @param layers
	 *            The layers.
	 */
	public FlatNetwork(final FlatLayer[] layers) {
		init(layers);
	}

	/**
	 * Construct a flat neural network.
	 * 
	 * @param input
	 *            Neurons in the input layer.
	 * @param hidden1
	 *            Neurons in the first hidden layer. Zero for no first hidden
	 *            layer.
	 * @param hidden2
	 *            Neurons in the second hidden layer. Zero for no second hidden
	 *            layer.
	 * @param output
	 *            Neurons in the output layer.
	 * @param tanh
	 *            True if this is a tanh activation, false for sigmoid.
	 */
	public FlatNetwork(final int input, final int hidden1, final int hidden2,
			final int output, final boolean tanh) {
		final double[] params = new double[1];
		FlatLayer[] layers;
		final ActivationFunction act = tanh ? new ActivationTANH()
				: new ActivationSigmoid();
		params[0] = 1; // slope

		if ((hidden1 == 0) && (hidden2 == 0)) {
			layers = new FlatLayer[2];
			layers[0] = new FlatLayer(act, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[1] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION, params);
		} else if ((hidden1 == 0) || (hidden2 == 0)) {
			final int count = Math.max(hidden1, hidden2);
			layers = new FlatLayer[3];
			layers[0] = new FlatLayer(act, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[1] = new FlatLayer(act, count,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[2] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION, params);
		} else {
			layers = new FlatLayer[4];
			layers[0] = new FlatLayer(act, input,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[1] = new FlatLayer(act, hidden1,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[2] = new FlatLayer(act, hidden2,
					FlatNetwork.DEFAULT_BIAS_ACTIVATION, params);
			layers[3] = new FlatLayer(act, output,
					FlatNetwork.NO_BIAS_ACTIVATION, params);
		}

		this.isLimited = false;
		this.connectionLimit = 0.0;

		init(layers);
	}

	/**
	 * Calculate the error for this neural network. The error is calculated
	 * using root-mean-square(RMS).
	 * 
	 * @param data
	 *            The training set.
	 * @return The error percentage.
	 */
	public double calculateError(final EngineIndexableSet data) {
		final ErrorCalculation errorCalculation = new ErrorCalculation();

		final double[] actual = new double[this.outputCount];
		final EngineData pair = BasicEngineData.createPair(data.getInputSize(),
				data.getIdealSize());

		for (int i = 0; i < data.getRecordCount(); i++) {
			data.getRecord(i, pair);
			compute(pair.getInputArray(), actual);
			errorCalculation.updateError(actual, pair.getIdealArray());
		}
		return errorCalculation.calculate();
	}

	/**
	 * Clear any context neurons.
	 */
	public void clearContext() {
		int index = 0;

		for (int i = 0; i < this.layerIndex.length; i++) {

			final boolean hasBias = (this.layerContextCount[i] + this.layerFeedCounts[i]) != this.layerCounts[i];

			// fill in regular neurons
			for (int j = 0; j < this.layerFeedCounts[i]; j++) {
				this.layerOutput[index++] = 0;
			}

			// fill in the bias
			if (hasBias) {
				this.layerOutput[index++] = this.biasActivation[i];
			}

			// fill in context
			for (int j = 0; j < this.layerContextCount[i]; j++) {
				this.layerOutput[index++] = 0;
			}
		}
	}

	/**
	 * Clone the network.
	 * 
	 * @return A clone of the network.
	 */
	@Override
	public FlatNetwork clone() {
		final FlatNetwork result = new FlatNetwork();
		cloneFlatNetwork(result);
		return result;
	}

	public void cloneFlatNetwork(FlatNetwork result) {
		result.inputCount = this.inputCount;
		result.layerCounts = EngineArray.arrayCopy(this.layerCounts);
		result.layerIndex = EngineArray.arrayCopy(this.layerIndex);
		result.layerOutput = EngineArray.arrayCopy(this.layerOutput);
		result.layerFeedCounts = EngineArray.arrayCopy(this.layerFeedCounts);
		result.contextTargetOffset = EngineArray
				.arrayCopy(this.contextTargetOffset);
		result.contextTargetSize = EngineArray
				.arrayCopy(this.contextTargetSize);
		result.layerContextCount = EngineArray
				.arrayCopy(this.layerContextCount);
		result.biasActivation = EngineArray.arrayCopy(this.biasActivation);
		result.outputCount = this.outputCount;
		result.weightIndex = this.weightIndex;
		result.weights = this.weights;

		result.activationFunctions = new ActivationFunction[this.activationFunctions.length];
		for (int i = 0; i < result.activationFunctions.length; i++) {
			result.activationFunctions[i] = this.activationFunctions[i].clone();
		}

		result.beginTraining = this.beginTraining;
		result.endTraining = this.endTraining;
	}

	/**
	 * Calculate the output for the given input.
	 * 
	 * @param input
	 *            The input.
	 * @param output
	 *            Output will be placed here.
	 */
	public void compute(final double[] input, final double[] output) {
		final int sourceIndex = this.layerOutput.length
				- this.layerCounts[this.layerCounts.length - 1];

		EngineArray.arrayCopy(input, 0, this.layerOutput, sourceIndex,
				this.inputCount);

		for (int i = this.layerIndex.length - 1; i > 0; i--) {
			computeLayer(i);
		}

		EngineArray.arrayCopy(this.layerOutput, 0, output, 0, this.outputCount);
	}

	/**
	 * Calculate a layer.
	 * 
	 * @param currentLayer
	 *            The layer to calculate.
	 */
	protected void computeLayer(final int currentLayer) {

		final int inputIndex = this.layerIndex[currentLayer];
		final int outputIndex = this.layerIndex[currentLayer - 1];
		final int inputSize = this.layerCounts[currentLayer];
		final int outputSize = this.layerFeedCounts[currentLayer - 1];

		int index = this.weightIndex[currentLayer - 1];

		final int limitX = outputIndex + outputSize;
		final int limitY = inputIndex + inputSize;

		// weight values
		for (int x = outputIndex; x < limitX; x++) {
			double sum = 0;
			for (int y = inputIndex; y < limitY; y++) {
				sum += this.weights[index++] * this.layerOutput[y];
			}
			this.layerOutput[x] = sum;
		}

		this.activationFunctions[currentLayer-1].activationFunction(this.layerOutput,outputIndex, outputSize);

		// update context values
		final int offset = this.contextTargetOffset[currentLayer];

		for (int x = 0; x < this.contextTargetSize[currentLayer]; x++) {
			this.layerOutput[offset + x] = this.layerOutput[outputIndex + x];
		}
	}

	/**
	 * Decode the specified data into the weights of the neural network. This
	 * method performs the opposite of encodeNetwork.
	 * 
	 * @param data
	 *            The data to be decoded.
	 */
	@Override
	public void decodeNetwork(final double[] data) {
		if (data.length != this.weights.length) {
			throw new EncogEngineError(
					"Incompatable weight sizes, can't assign length="
							+ data.length + " to length=" + data.length);
		}
		this.weights = data;

	}

	/**
	 * Encode the neural network to an array of doubles. This includes the
	 * network weights. To read this into a neural network, use the
	 * decodeNetwork method.
	 * 
	 * @return The encoded network.
	 */
	@Override
	public double[] encodeNetwork() {
		return this.weights;
	}

	/**
	 * @return The offset of the context target for each layer.
	 */
	public int[] getContextTargetOffset() {
		return this.contextTargetOffset;
	}

	/**
	 * @return The context target size for each layer. Zero if the layer does
	 *         not feed a context layer.
	 */
	public int[] getContextTargetSize() {
		return this.contextTargetSize;
	}

	/**
	 * @return The length of the array the network would encode to.
	 */
	@Override
	public int getEncodeLength() {
		return this.weights.length;
	}

	/**
	 * @return The number of input neurons.
	 */
	public int getInputCount() {
		return this.inputCount;
	}

	/**
	 * @return The number of neurons in each layer.
	 */
	public int[] getLayerCounts() {
		return this.layerCounts;
	}

	/**
	 * @return The number of neurons in each layer that are fed by the previous
	 *         layer.
	 */
	public int[] getLayerFeedCounts() {
		return this.layerFeedCounts;
	}

	/**
	 * @return Indexes into the weights for the start of each layer.
	 */
	public int[] getLayerIndex() {
		return this.layerIndex;
	}

	/**
	 * @return The output for each layer.
	 */
	public double[] getLayerOutput() {
		return this.layerOutput;
	}

	/**
	 * @return The neuron count.
	 */
	public int getNeuronCount() {
		int result = 0;
		for (final int element : this.layerCounts) {
			result += element;
		}
		return result;
	}

	/**
	 * @return The number of output neurons.
	 */
	public int getOutputCount() {
		return this.outputCount;
	}

	/**
	 * @return The index of each layer in the weight and threshold array.
	 */
	public int[] getWeightIndex() {
		return this.weightIndex;
	}

	/**
	 * @return The index of each layer in the weight and threshold array.
	 */
	public double[] getWeights() {
		return this.weights;
	}

	/**
	 * Neural networks with only one type of activation function offer certain
	 * optimization options. This method determines if only a single activation
	 * function is used.
	 * 
	 * @return The number of the single activation function, or -1 if there are
	 *         no activation functions or more than one type of activation
	 *         function.
	 */
	public Class<?> hasSameActivationFunction() {
		final List<Class> map = new ArrayList<Class>();

		for (final ActivationFunction activation : this.activationFunctions) {
			if (!map.contains(activation.getClass())) {
				map.add(activation.getClass());
			}
		}

		if (map.size() != 1) {
			return null;
		} else {
			return map.get(0);
		}
	}

	/**
	 * Construct a flat network.
	 * 
	 * @param layers
	 *            The layers of the network to create.
	 */
	public void init(final FlatLayer[] layers) {

		final int layerCount = layers.length;

		this.inputCount = layers[0].getCount();
		this.outputCount = layers[layerCount - 1].getCount();

		this.layerCounts = new int[layerCount];
		this.layerContextCount = new int[layerCount];
		this.weightIndex = new int[layerCount];
		this.layerIndex = new int[layerCount];
		this.activationFunctions = new ActivationFunction[layerCount];
		this.layerFeedCounts = new int[layerCount];
		this.contextTargetOffset = new int[layerCount];
		this.contextTargetSize = new int[layerCount];
		this.biasActivation = new double[layerCount];

		int index = 0;
		int neuronCount = 0;
		int weightCount = 0;

		for (int i = layers.length - 1; i >= 0; i--) {

			final FlatLayer layer = layers[i];
			FlatLayer nextLayer = null;

			if (i > 0) {
				nextLayer = layers[i - 1];
			}

			this.biasActivation[index] = layer.getBiasActivation();
			this.layerCounts[index] = layer.getTotalCount();
			this.layerFeedCounts[index] = layer.getCount();
			this.layerContextCount[index] = layer.getContectCount();
			this.activationFunctions[index] = layer.getActivation();

			neuronCount += layer.getTotalCount();

			if (nextLayer != null) {
				weightCount += layer.getCount() * nextLayer.getTotalCount();
			}

			if (index == 0) {
				this.weightIndex[index] = 0;
				this.layerIndex[index] = 0;
			} else {
				this.weightIndex[index] = this.weightIndex[index - 1]
						+ (this.layerCounts[index] * this.layerFeedCounts[index - 1]);
				this.layerIndex[index] = this.layerIndex[index - 1]
						+ this.layerCounts[index - 1];
			}

			int neuronIndex = 0;
			for (int j = layers.length - 1; j >= 0; j--) {
				if (layers[j].getContextFedBy() == layer) {
					this.contextTargetSize[i] = layers[j].getContectCount();
					this.contextTargetOffset[i] = neuronIndex
							+ layers[j].getTotalCount()
							- layers[j].getContectCount();
				}
				neuronIndex += layers[j].getTotalCount();
			}

			index++;
		}

		this.beginTraining = 0;
		this.endTraining = this.layerCounts.length - 1;

		this.weights = new double[weightCount];
		this.layerOutput = new double[neuronCount];

		clearContext();
	}

	/**
	 * Perform a simple randomization of the weights of the neural network
	 * between -1 and 1.
	 */
	public void randomize() {
		randomize(1, -1);
	}

	/**
	 * Perform a simple randomization of the weights of the neural network
	 * between the specified hi and lo.
	 * 
	 * @param hi
	 *            The network high.
	 * @param lo
	 *            The network low.
	 */
	public void randomize(final double hi, final double lo) {
		for (int i = 0; i < this.weights.length; i++) {
			this.weights[i] = (Math.random() * (hi - lo)) + lo;
		}
	}

	/**
	 * @return the beginTraining
	 */
	public int getBeginTraining() {
		return beginTraining;
	}

	/**
	 * @param beginTraining
	 *            the beginTraining to set
	 */
	public void setBeginTraining(int beginTraining) {
		this.beginTraining = beginTraining;
	}

	/**
	 * @return the endTraining
	 */
	public int getEndTraining() {
		return endTraining;
	}

	/**
	 * @param endTraining
	 *            the endTraining to set
	 */
	public void setEndTraining(int endTraining) {
		this.endTraining = endTraining;
	}

	/**
	 * @return the connectionLimit
	 */
	public double getConnectionLimit() {
		return connectionLimit;
	}

	/**
	 * @param connectionLimit
	 *            the connectionLimit to set
	 */
	public void setConnectionLimit(double connectionLimit) {
		this.connectionLimit = connectionLimit;
		if (this.connectionLimit > EncogEngine.DEFAULT_ZERO_TOLERANCE)
			this.isLimited = true;
	}

	/**
	 * @return the isLimited
	 */
	public boolean isLimited() {
		return isLimited;
	}

	public void clearConnectionLimit() {
		this.connectionLimit = 0.0;
		this.isLimited = false;
	}

	/**
	 * Determine if the hidden or output layers have a slope other than 1.0.
	 * Only counts sigmoid, linear and tanh. Other activation types will cause
	 * this method to return false.
	 * 
	 * @return True if there is a slope other than 1.0, false otherwise.
	 */
	public boolean anySlopeNotOne() {
		for (ActivationFunction activation : this.activationFunctions) {
			if (!(activation instanceof ActivationLinear)
					&& !(activation instanceof ActivationSigmoid)
					&& !(activation instanceof ActivationTANH)) {
				return true;
			}

			if (Math.abs(activation.getParams()[0] - 1.0) > EncogEngine.DEFAULT_ZERO_TOLERANCE) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return The activation functions.
	 */
	public ActivationFunction[] getActivationFunctions() {
		return activationFunctions;
	}
	
	

}
