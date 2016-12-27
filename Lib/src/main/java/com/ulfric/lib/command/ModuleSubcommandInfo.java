package com.ulfric.lib.command;

import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.IModule;
import com.ulfric.lib.api.module.Plugin;

import java.util.List;

final class ModuleSubcommandInfo extends SimpleSubCommand {

	ModuleSubcommandInfo(Command command)
	{
		super(command, "info", "show", "details", "detail");

		this.withArgument("module", ArgStrategy.MODULE, "system.module_not_found");
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		String name = module.getName();

		String desc = module.getModuleDescription();

		String version = module.getModuleVersion();

		String authors = module.getModuleAuthors();

		List<IModule> tree = module.getTree();

		String treeStr = "root";

		if (!CollectUtils.isEmpty(tree))
		{
			StringBuilder builder = new StringBuilder();

			for (IModule branch : tree)
			{
				builder.append(branch.getName());

				builder.append(" -> ");
			}

			builder.append("root");

			treeStr = builder.toString();
		}

		String enabled = Booleans.fancify(module.isModuleEnabled());

		boolean plugin = module instanceof Plugin;

		Locale.send(this.getSender(), "system.module_info", name, desc, version, authors, enabled, treeStr, plugin);
	}

}
