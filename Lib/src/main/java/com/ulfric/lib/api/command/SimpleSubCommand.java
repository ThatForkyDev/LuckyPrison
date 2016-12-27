package com.ulfric.lib.api.command;

public abstract class SimpleSubCommand extends SubCommand implements Runnable, EnforceableCommand {

	protected SimpleSubCommand(Command command, String name, String... aliases)
	{
		super(command, name, aliases);
	}

	@Override
	public final boolean execute()
	{
		if (!this.enforce()) return true;

		this.run();

		return true;
	}

	@Override
	public boolean enforce()
	{
		return true;
	}

}
