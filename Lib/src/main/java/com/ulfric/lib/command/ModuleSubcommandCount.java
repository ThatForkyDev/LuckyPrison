package com.ulfric.lib.command;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;
import com.ulfric.lib.api.module.Modules;

final class ModuleSubcommandCount extends SimpleSubCommand {

	ModuleSubcommandCount(Command command)
	{
		super(command, "count", "number", "cnt");

		this.withArgument("module", ArgStrategy.MODULE);
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		if (module == null)
		{
			Locale.send(this.getSender(), "system.module_count", Modules.getModules().size(), "root");

			return;
		}

		Locale.send(this.getSender(), "system.module_count", module.getTotalSubModules(), module.getName());
	}

}
