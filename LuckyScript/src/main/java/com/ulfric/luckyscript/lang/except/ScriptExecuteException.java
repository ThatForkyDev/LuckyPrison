package com.ulfric.luckyscript.lang.except;

@SuppressWarnings("serial")
public class ScriptExecuteException extends IllegalArgumentException {


	public ScriptExecuteException(String message, Throwable throwable)
	{
		super(message, throwable);
	}


}