package com.ulfric.lib.command;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;

final class ModuleSubcommandDisable extends SimpleSubCommand {

	ModuleSubcommandDisable(Command command)
	{
		super(command, "disable", "disabl", "off");

		this.withArgument("module", ArgStrategy.MODULE, "system.module_not_found");

		this.withNode("lib.module.disable");
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		if (!module.isModuleEnabled())
		{
			Locale.sendError(this.getSender(), "system.module_already_disabled");

			return;
		}

		module.disable();

		Locale.sendSuccess(this.getSender(), "system.module_disabled", module.getName(), Booleans.fancify(!module.isModuleEnabled(), "OK", "ERR"));
	}

}
