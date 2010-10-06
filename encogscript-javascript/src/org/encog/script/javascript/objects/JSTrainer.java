package org.encog.script.javascript.objects;

import org.encog.engine.util.Format;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;

public class JSTrainer extends ScriptableObject {
	
	private Train train;
	private JSNeuralNetwork network;
	private JSTrainingData data;

	@Override
	public String getClassName() {
		return "Trainer";
	}
	
	public void jsFunction_create(JSNeuralNetwork network, JSTrainingData data)
	{
		this.network = network;
		this.data = data;
		train = new ResilientPropagation(network.getNetwork(),data.getData());
	}
	
	public double jsGet_error()
	{
		return train.getError();
	}
	
	public void jsFunction_iteration()
	{
		train.iteration();
	}
	
	public void jsFunction_trainToError(double error) {
		int epoch = 1;

		Object obj = ScriptableObject.getProperty(this.getParentScope(),
				"console");
		JSEncogConsole console = (JSEncogConsole) Context.jsToJava(obj,
				JSEncogConsole.class);
		console.println("Beginning training...");

		long lastUpdate = 0;

		do {
			train.iteration();

			long sinceLastUpdate = System.currentTimeMillis() - lastUpdate;

			if (sinceLastUpdate > 1000) {
				console.println("Iteration #" + Format.formatInteger(epoch)
						+ " Error:" + Format.formatPercent(train.getError())
						+ " Target Error: " + Format.formatPercent(error));
				lastUpdate = System.currentTimeMillis();
			}
			epoch++;
		} while ((train.getError() > error) && !train.isTrainingDone());
		
		console.println("Iteration #" + Format.formatInteger(epoch)
				+ " Error:" + Format.formatPercent(train.getError())
				+ " Target Error: " + Format.formatPercent(error));
		
		
		train.finishTraining();
	}	
	
}
