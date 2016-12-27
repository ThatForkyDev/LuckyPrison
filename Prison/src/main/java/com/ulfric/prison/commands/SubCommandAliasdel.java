package com.ulfric.prison.commands;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Command;
import com.ulfric.lib.api.command.SimpleSubCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;

class SubCommandAliasdel extends SimpleSubCommand {


	public SubCommandAliasdel(Command command)
	{
		super(command, "delete", "del", "remove");

		this.withEnforcePlayer();

		this.withArgument("alias", ArgStrategy.STRING, "prison.alias_need_cmd");
	}

	@Override
	public void run()
	{
		String alias = (String) this.getObject("alias");

		Player player = this.getPlayer();

		List<String> aliases = Hooks.DATA.getPlayerDataAsStringList(this.getUniqueId(), "prison.aliases");

		if (aliases.isEmpty())
		{
			Locale.send(player, "prison.alias_none");

			return;
		}

		Iterator<String> iterator = aliases.iterator();

		while (iterator.hasNext())
		{
			String found = StringUtils.findOption(iterator.next(), "cmd");

			if (found == null) continue;

			if (!found.equals(alias)) continue;

			iterator.remove();

			Locale.send(player, "prison.alias_del");

			return;
		}

		Locale.send(player, "prison.alias_not_found");
	}

}
