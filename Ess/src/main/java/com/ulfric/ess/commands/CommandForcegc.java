package com.ulfric.ess.commands;

import com.ulfric.lib.api.command.SimpleCommand;

public class CommandForcegc extends SimpleCommand {

	@Override
	public void run()
	{
		System.gc();
	}

}