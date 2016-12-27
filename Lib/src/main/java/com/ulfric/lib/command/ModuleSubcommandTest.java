package com.ulfric.lib.command;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;

final class ModuleSubcommandTest extends SimpleSubCommand {

	ModuleSubcommandTest(Command command)
	{
		super(command, "test", "run");

		this.withArgument("module", ArgStrategy.MODULE, "system.module_not_found");

		this.withNode("lib.module.test");
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		if (!module.isModuleEnabled())
		{
			Locale.sendError(this.getSender(), "system.module_is_disabled");

			return;
		}

		module.test(this.getSender());

		Locale.sendSuccess(this.getSender(), "system.module_tested", module.getName(), module.getTotalSubModules(), Booleans.fancify(module.isModuleEnabled(), "enabled", "disabled"));
	}

}
