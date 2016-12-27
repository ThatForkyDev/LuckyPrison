package com.ulfric.ess.commands;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.teleport.TeleportUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandHome extends SimpleCommand {

	public CommandHome()
	{
		this.withEnforcePlayer();

		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer, "ess.home.others");

		this.withArgument("name", ArgStrategy.STRING);
	}

	@Override
	public void run()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");
		List<String> homes = Hooks.DATA.getPlayerDataAsStringList(target.getUniqueId(), "homes");

		if (homes.isEmpty())
		{
			Locale.sendError(this.getPlayer(), "ess.home_none");

			return;
		}

		String home = (String) this.getObject("name");

		if (home == null)
		{
			if (homes.size() > 1)
			{
				this.sendHomes(homes, this.getPlayer());

				return;
			}

			home = "home";
		}

		String lowerCase = home.toLowerCase();

		Location location = null;

		// See https://bitbucket.org/LuckyPrison/tracker/issues/341/uuids-in-the-home-list
		List<String> invalidHomes = Lists.newArrayList();

		for (String entry : homes)
		{
			if (entry == null || entry.isEmpty()) continue;

			if (entry.trim().split(" ").length != 2)
			{
				invalidHomes.add(entry);
			}

			if (!entry.toLowerCase().startsWith(lowerCase + ' ')) continue;

			location = LocationUtils.fromString(entry.substring(lowerCase.length(), entry.length()).trim());

			break;
		}
		invalidHomes.forEach(homes::remove);
		if (location != null)
		{
			TeleportUtils.teleport(this.getPlayer(), location, 3);

			return;
		}
		if (!this.hasObjects() && !homes.isEmpty())
		{
			this.sendHomes(homes, this.getPlayer());

			return;
		}

		Locale.sendError(this.getPlayer(), "ess.home_not_found", home);
	}

	private void sendHomes(Iterable<String> homes, Player player)
	{
		ComponentBuilder message = new ComponentBuilder(Locale.getMessage(player, "ess.homes"));

		boolean first = true;
		for (String string : homes)
		{
			if (StringUtils.isEmpty(string)) continue;

			String[] split = string.split(" ");

			if (!first)
			{
				message.append(", ").color(ChatColor.YELLOW);
			}
			else
			{
				first = false;
			}

			String home = split[0];

			message.append(split[0])
				   .color(ChatColor.GRAY)
				   .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										 TextComponent.fromLegacyText(Strings.format(Locale.getMessage(player, "ess.homes_click"), home))))
				   .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + home));

		}

		this.getSender().sendMessage(message.create());
	}

}
