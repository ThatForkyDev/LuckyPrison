package com.ulfric.lib.command;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.IModule;
import com.ulfric.lib.api.module.Modules;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class ModuleSubcommandList extends SimpleSubCommand {

	ModuleSubcommandList(Command command)
	{
		super(command, "list", "modules", "lis", "l", "view");

		this.withArgument("module", ArgStrategy.MODULE);
		this.withArgument("enabled", new ExactArg("enabled"));
		this.withArgument("disabled", new ExactArg("disabled"));
	}

	@Override
	public void run()
	{
		IModule module = (IModule) this.getObject("module");

		List<IModule> modules;

		if (module == null)
		{
			modules = Lists.newArrayListWithExpectedSize(Modules.getModules().size());

			for (Map.Entry<String, IModule> entry : Modules.getModules())
			{
				modules.add(entry.getValue());
			}
		}
		else
		{
			modules = module.getChildren();
		}

		if (this.hasObject("enabled"))
		{
			Iterator<IModule> iter = modules.iterator();

			while (iter.hasNext())
			{
				if (iter.next().isModuleEnabled()) continue;

				iter.remove();
			}
		}

		else if (this.hasObject("disabled"))
		{
			Iterator<IModule> iter = modules.iterator();

			while (iter.hasNext())
			{
				if (!iter.next().isModuleEnabled()) continue;

				iter.remove();
			}
		}

		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.WHITE);
		builder.append(Strings.format("Modules ({0}): ", modules.size()));

		for (IModule lmodule : modules)
		{
			builder.append(lmodule.isModuleEnabled() ? ChatColor.GREEN : ChatColor.RED);

			builder.append(lmodule.getName());

			builder.append(ChatColor.WHITE);

			builder.append(", ");
		}

		String str = builder.toString();

		this.getSender().sendMessage(str.substring(0, str.length() - 4));
	}

}
