package com.ulfric.lib.api.command;

public abstract class EnforcedCommand extends Command implements EnforceableCommand, ExecutableCommand {


	@Override
	public boolean dispatch()
	{
		return this.enforce() && this.execute();
	}


}