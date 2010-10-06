package org.encog.script.javascript;


import org.encog.script.EncogScript;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestJavaScript extends TestCase {

	public void testHelloWorld()
	{
		StringConsole console = new StringConsole();
		EncogJavascriptEngine.init();
		EncogScript script = new EncogScript();
		script.setSource("console.print(\'Hello World\')\n");
		script.run(console);
		Assert.assertEquals(console.toString(),"Hello World");		
	}
	
	public void testLoop()
	{
		StringConsole console = new StringConsole();
		EncogJavascriptEngine.init();
		EncogScript script = new EncogScript();
		script.setSource("for(i=0;i<10;i++){console.print(i);}\n");
		script.run(console);
		Assert.assertEquals(console.toString(),"0.01.02.03.04.05.06.07.08.09.0");		
	}
}
