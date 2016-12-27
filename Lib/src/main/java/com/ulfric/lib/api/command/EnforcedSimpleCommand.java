package com.ulfric.lib.api.command;

public abstract class EnforcedSimpleCommand extends SimpleCommand implements EnforceableCommand {

	@Override
	public final boolean dispatch()
	{
		if (!this.enforce()) return true;

		this.run();

		return true;
	}

}