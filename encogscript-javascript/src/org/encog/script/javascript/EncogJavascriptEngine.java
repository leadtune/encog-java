package org.encog.script.javascript;

import java.lang.reflect.InvocationTargetException;

import org.encog.persist.EncogMemoryCollection;
import org.encog.script.ConsoleInputOutput;
import org.encog.script.EncogScript;
import org.encog.script.EncogScriptEngine;
import org.encog.script.EncogScriptEngineFactory;
import org.encog.script.EncogScriptError;
import org.encog.script.EncogScriptRuntimeError;
import org.encog.script.javascript.objects.JSEncogCollection;
import org.encog.script.javascript.objects.JSEncogConsole;
import org.encog.script.javascript.objects.JSNeuralNetwork;
import org.encog.script.javascript.objects.JSTrainer;
import org.encog.script.javascript.objects.JSTrainingData;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class EncogJavascriptEngine implements EncogScriptEngine {

	private ConsoleInputOutput externalConsole;
	private EncogMemoryCollection currentCollection;
	
	private JSEncogConsole console;
	private JSEncogCollection current;
	
	public EncogJavascriptEngine(EncogMemoryCollection collection)
	{
		this.currentCollection = collection;
	}
	
	public static void init()
	{
		EncogScriptEngineFactory.getInstance().registerIndividualEngineFactory(new EncogJavascriptEngineFactory());
	}
	
	public EncogJavascriptEngine() {
		this(null);
	}

	public void setConsole(ConsoleInputOutput console)
	{
		this.externalConsole = console;
	}
	
	public void run(EncogScript script)
	{
		Context cx = Context.enter();
        try {
        	
        	Scriptable scope = cx.initStandardObjects();
        	
        	
        	ScriptableObject.defineClass(scope, JSTrainingData.class);
        	ScriptableObject.defineClass(scope, JSNeuralNetwork.class);
        	ScriptableObject.defineClass(scope, JSTrainer.class);
        	ScriptableObject.defineClass(scope, JSEncogCollection.class);
        	//ScriptableObject.defineClass(scope, EncogJavascriptConsole.class);
        	        	
        	this.console = new JSEncogConsole();
        	this.console.setConsole(this.externalConsole);
        	Object con2 = Context.javaToJS(this.console, scope);
        	ScriptableObject.putProperty(scope, "console", con2);
        	
        	this.current = (JSEncogCollection)cx.newObject(scope,"EncogCollection");
        	this.current.setCollection(this.currentCollection);
        	ScriptableObject.putProperty(scope, "current",this.current);
  
        	cx.evaluateString(scope, script.getSource(), script.getName(), 1, null);
  
        } catch(EvaluatorException e) {
        	throw new EncogScriptRuntimeError(e);
        } catch(EcmaError e) {
        	throw new EncogScriptRuntimeError(e);
        } catch (IllegalAccessException e) {
			throw new EncogScriptError(e);
		} catch (InstantiationException e) {
			throw new EncogScriptError(e);
		} catch (InvocationTargetException e) {
			throw new EncogScriptError(e);
		} 
        finally {
        	Context.exit();
        }
	}
}
