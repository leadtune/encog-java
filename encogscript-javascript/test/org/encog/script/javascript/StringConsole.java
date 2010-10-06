package org.encog.script.javascript;

import org.encog.script.ConsoleInputOutput;

public class StringConsole implements ConsoleInputOutput {

	private StringBuilder result = new StringBuilder();
	
	@Override
	public String input(String prompt) {
		return null;
	}

	@Override
	public void print(String line) {
		result.append(line);
	}

	@Override
	public void printLine(String line) {
		result.append(line);
	}
	
	public void clear()
	{
		this.result.setLength(0);
	}
	
	public String toString()
	{
		return this.result.toString();	
	}
	
}
