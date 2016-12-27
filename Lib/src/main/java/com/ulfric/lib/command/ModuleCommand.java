package com.ulfric.lib.command;

import com.ulfric.lib.api.command.SubCommandParent;

public final class ModuleCommand extends SubCommandParent {

	public ModuleCommand()
	{
		this.withSubcommand(new ModuleSubcommandList(this));
		this.withSubcommand(new ModuleSubcommandInfo(this));
		this.withSubcommand(new ModuleSubcommandEnable(this));
		this.withSubcommand(new ModuleSubcommandDisable(this));
		this.withSubcommand(new ModuleSubcommandReload(this));
		this.withSubcommand(new ModuleSubcommandTest(this));
		this.withSubcommand(new ModuleSubcommandCount(this));
	}

}
