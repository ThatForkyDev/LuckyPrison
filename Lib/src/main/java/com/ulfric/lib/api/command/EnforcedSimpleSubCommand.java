package com.ulfric.lib.api.command;

public abstract class EnforcedSimpleSubCommand extends SimpleSubCommand {

	protected EnforcedSimpleSubCommand(Command command, String name, String... aliases)
	{
		super(command, name, aliases);
	}

	@Override
	public final boolean dispatch()
	{
		return !this.enforce() || !this.canUse() || this.execute();

	}

}
