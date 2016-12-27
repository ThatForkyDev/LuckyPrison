package com.ulfric.control.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandSeen extends SimpleCommand {

	public CommandSeen()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		if (!PlayerUtils.hasPlayed(player))
		{
			Locale.sendError(this.getSender(), "control.seen_not_found", player.getName());

			return;
		}

		UUID uuid = player.getUniqueId();

		if (player.isOnline())
		{
			Locale.send(this.getSender(), "control.seen_online", player.getName());

			return;
		}

		Locale.send(this.getSender(), "control.seen_offline", player.getName(), Hooks.DATA.getPlayerDataAsString(uuid, "data.join.date.last"));

		if (!this.hasPermission("control.seen.location")) return;

		Locale.send(this.getSender(), "control.seen_location", Hooks.DATA.getPlayerDataAsString(uuid, "data.lastlocation").replace(",", ", "));
	}

}