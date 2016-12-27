package com.ulfric.luckyscript.lang.except;

@SuppressWarnings("serial")
public class ScriptParseException extends IllegalArgumentException {


	public ScriptParseException(String message)
	{
		super(message);
	}

	public ScriptParseException(String message, Throwable throwable)
	{
		super(message, throwable);
	}


}