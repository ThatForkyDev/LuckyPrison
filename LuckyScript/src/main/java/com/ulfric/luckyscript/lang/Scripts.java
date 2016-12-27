package com.ulfric.luckyscript.lang;

public class Scripts {

	protected static IScripts impl = IScripts.EMPTY;

	public static Script getScript(String name)
	{
		return Scripts.impl.getScript(name);
	}

	protected interface IScripts
	{
		IScripts EMPTY = new IScripts() { };
	
		default Script getScript(String name) { return null; }
	}

}