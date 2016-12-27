package com.ulfric.control.commands;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.locale.Locale;

public class CommandSpectate extends SimpleCommand {

	public CommandSpectate()
	{
		this.withEnforcePlayer();

		this.withArgument("player", ArgStrategy.PLAYER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		if (target == null || player.getUniqueId().equals(target.getUniqueId()))
		{
			Locale.sendSuccess(player, "control.spectate_over");

			player.setGameMode(Optional.ofNullable((GameMode) Metadata.getAndRemove(player, "_ulf_gamemode")).orElseGet(Bukkit::getDefaultGameMode));

			return;
		}

		Metadata.apply(player, "_ulf_gamemode", player.getGameMode());
		player.setGameMode(GameMode.SPECTATOR);
		player.setSpectatorTarget(target);

		Locale.sendSuccess(player, "control.spectate", target.getName());
	}

}