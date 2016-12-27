package com.ulfric.lib.api.command;

public abstract class EnforcedSubCommand extends SubCommand implements EnforceableCommand {

	protected EnforcedSubCommand(Command command, String name)
	{
		super(command, name);
	}

	@Override
	public final boolean dispatch()
	{
		return !this.enforce() || !this.canUse() || this.execute();

	}

}
