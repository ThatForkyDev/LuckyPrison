package com.ulfric.lib.command;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;

final class ModuleSubcommandEnable extends SimpleSubCommand {

	ModuleSubcommandEnable(Command command)
	{
		super(command, "enable", "enabl", "on");

		this.withArgument("module", ArgStrategy.MODULE, "system.module_not_found");

		this.withArgument("sub", new ExactArg("sub"));

		this.withNode("lib.module.enable");
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		if (module.isModuleEnabled())
		{
			if (!this.hasObject("sub"))
			{
				Locale.sendError(this.getSender(), "module_already_enabled");

				return;
			}

			module.enableSubModules();

			Locale.sendSuccess(this.getSender(), "system.module_enabled_sub", module.getName());

			return;
		}

		module.enable();

		if (!this.hasObject("sub"))
		{
			Locale.sendSuccess(this.getSender(), "system.module_enabled", module.getName(), Booleans.fancify(module.isModuleEnabled(), "OK", "ERR"));

			return;
		}

		module.enableSubModules();

		Locale.sendSuccess(this.getSender(), "system.module_enabled_wsub", module.getName(), Booleans.fancify(module.isModuleEnabled(), "OK", "ERR"));
	}

}
