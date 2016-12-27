package com.ulfric.prison.commands;

import com.ulfric.lib.api.command.SubCommandParent;

public class CommandMinebuddy extends SubCommandParent {

	public CommandMinebuddy()
	{
		this.withSubcommand(new SubCommandMbinfo(this));
		this.withSubcommand(new SubCommandMbleave(this));
		this.withSubcommand(new SubCommandMbinvite(this));
		this.withSubcommand(new SubCommandMbdecline(this));
		this.withSubcommand(new SubCommandMbaccept(this));
	}

}