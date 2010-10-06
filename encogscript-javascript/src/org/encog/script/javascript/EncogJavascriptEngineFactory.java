package org.encog.script.javascript;

import org.encog.persist.EncogMemoryCollection;
import org.encog.script.EncogScriptEngine;
import org.encog.script.IndividualEngineFactory;

public class EncogJavascriptEngineFactory implements IndividualEngineFactory {

	@Override
	public EncogScriptEngine create() {
		return new EncogJavascriptEngine();
	}

	@Override
	public EncogScriptEngine create(EncogMemoryCollection c) {
		return new EncogJavascriptEngine(c);
	}

	@Override
	public String getName() {
		return "javascript";
	}

}
