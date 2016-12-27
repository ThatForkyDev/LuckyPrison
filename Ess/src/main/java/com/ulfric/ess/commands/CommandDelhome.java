package com.ulfric.ess.commands;

import java.util.List;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;

public class CommandDelhome extends SimpleCommand {

	public CommandDelhome()
	{
		this.withEnforcePlayer();

		this.withArgument("name", ArgStrategy.STRING, "home");
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		List<String> homes = Hooks.DATA.getPlayerDataAsStringList(player.getUniqueId(), "homes");

		String name = (String) this.getObject("name");
		String lowerCase = name.toLowerCase();
		String foundHome = null;

		for (String entry : homes)
		{
			if (entry == null || entry.isEmpty()) continue;

			if (!entry.toLowerCase().startsWith(lowerCase + ' ')) continue;

			foundHome = entry;
			break;
		}

		if (foundHome == null)
		{
			Locale.sendError(this.getPlayer(), "ess.home_not_found", name);
			return;
		}

		homes.remove(foundHome);
		Hooks.DATA.setPlayerData(player.getUniqueId(), "homes", homes);
		Locale.sendSuccess(player, "ess.delhome_named", name);
	}

}