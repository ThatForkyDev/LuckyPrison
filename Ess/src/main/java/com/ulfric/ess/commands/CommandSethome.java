package com.ulfric.ess.commands;

import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.player.PermissionUtils;

public class CommandSethome extends SimpleCommand {

	public CommandSethome()
	{
		this.withEnforcePlayer();

		this.withArgument("name", ArgStrategy.STRING, "home");
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		List<String> homes = Hooks.DATA.getPlayerDataAsStringList(player.getUniqueId(), "homes");

		boolean multiplePerm = this.hasPermission(Permissions.MULTIPLE_HOMES);

		if (!homes.isEmpty() && !multiplePerm)
		{
			Locale.sendError(player, "ess.sethome_used_all");

			return;
		}

		String name = Optional.ofNullable((String) this.getObject("name")).orElse("home");

		boolean alreadyExists = false;

		String lowerCase = name.toLowerCase();

		for (String entry : homes)
		{
			if (entry == null || entry.isEmpty()) continue;

			if (!entry.toLowerCase().startsWith(lowerCase + ' ')) continue;

			alreadyExists = true;
		}

		if (this.hasObjects() && multiplePerm && !alreadyExists)
		{
			if (!CollectUtils.containsIgnoreCase(homes, name) && PermissionUtils.getMax(player, Permissions.MULTIPLE_HOMES) <= homes.size())
			{
				Locale.sendError(player, "ess.sethome_used_all");

				return;
			}
		}

		Location location = player.getLocation();

		String locationString = LocationUtils.toString(location);

		if (!homes.isEmpty())
		{
			boolean flag = true;
			for (int x = 0; x < homes.size(); x++)
			{
				String entry = homes.get(x);

				if (entry == null || entry.isEmpty()) continue;

				if (!entry.toLowerCase().startsWith(lowerCase + ' ')) continue;

				homes.set(x, Strings.format("{0} {1}", name, locationString));

				flag = false;

				break;
			}

			if (flag)
			{
				homes.add(Strings.format("{0} {1}", name, locationString));
			}
		}
		else
		{
			homes.add(Strings.format("{0} {1}", name, locationString));

			Hooks.DATA.setPlayerData(player.getUniqueId(), "homes", homes);
		}

		if (name.equals("home"))
		{
			Locale.sendSuccess(player, "ess.sethome");

			return;
		}

		Locale.sendSuccess(player, "ess.sethome_named", name);
	}

}