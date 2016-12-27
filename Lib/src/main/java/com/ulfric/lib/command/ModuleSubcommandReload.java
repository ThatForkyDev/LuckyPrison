package com.ulfric.lib.command;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;

final class ModuleSubcommandReload extends SimpleSubCommand {

	ModuleSubcommandReload(Command command)
	{
		super(command, "reload", "rel");

		this.withArgument("module", ArgStrategy.MODULE, "system.module_not_found");

		this.withNode("lib.module.reload");
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		if (module.isModuleEnabled())
		{
			module.disable();
		}

		module.enable();

		Locale.sendSuccess(this.getSender(), "system.module_reloaded", module.getName(), module.getTotalSubModules(), Booleans.fancify(module.isModuleEnabled(), "enabled", "disabled"));
	}

}
