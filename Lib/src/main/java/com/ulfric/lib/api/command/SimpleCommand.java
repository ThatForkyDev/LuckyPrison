package com.ulfric.lib.api.command;

public abstract class SimpleCommand extends Command implements Runnable {


	@Override
	public boolean dispatch()
	{
		this.run();

		return true;
	}


}