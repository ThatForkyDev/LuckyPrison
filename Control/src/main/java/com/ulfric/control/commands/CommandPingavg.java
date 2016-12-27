package com.ulfric.control.commands;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandPingavg extends SimpleCommand {

	@Override
	public void run()
	{
		Collection<? extends Player> players = Bukkit.getOnlinePlayers();

		Locale.send(this.getSender(), "control.pingavg", players.size(), players.stream().mapToInt(PlayerUtils::getPing).average().orElse(0));
	}

}