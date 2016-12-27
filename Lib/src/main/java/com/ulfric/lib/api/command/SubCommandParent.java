package com.ulfric.lib.api.command;

import com.ulfric.lib.api.server.Commands;

public abstract class SubCommandParent extends Command {

	@Override
	public final boolean dispatch()
	{
		Commands.dispatch(this.getSender(), "help " + this.getLabel());

		return true;
	}

}