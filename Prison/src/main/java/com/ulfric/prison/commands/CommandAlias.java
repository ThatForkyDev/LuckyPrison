package com.ulfric.prison.commands;

import com.ulfric.lib.api.command.SubCommandParent;

public class CommandAlias extends SubCommandParent {

	public CommandAlias()
	{
		this.withEnforcePlayer();

		this.withSubcommand(new SubCommandAliasadd(this));
		this.withSubcommand(new SubCommandAliasdel(this));
	}

}