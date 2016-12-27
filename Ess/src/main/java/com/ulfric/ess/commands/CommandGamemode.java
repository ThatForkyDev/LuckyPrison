package com.ulfric.ess.commands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandGamemode extends SimpleCommand {

	public CommandGamemode()
	{
		this.withArgument("mode", ArgStrategy.GAMEMODE, "ess.gamemode_not_found");

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.GAMEMODE_OTHERS);
	}

	@Override
	public void run()
	{
		GameMode mode = (GameMode) this.getObject("mode");

		Player player = (Player) this.getObject("player");

		if (player.getGameMode().equals(mode))
		{
			Locale.sendError(this.getSender(), "ess.gamemode_already", player.getName().equals(this.getName()) ? Locale.getMessage(this.getSender(), "system.your") : player.getName() + "'s", StringUtils.capitalize(mode.name().toLowerCase()));

			return;
		}

		Locale.sendSuccess(this.getSender(), "ess.gamemode", player.getName().equals(this.getName()) ? Locale.getMessage(this.getSender(), "system.your") : player.getName() + "'s", StringUtils.capitalize(mode.name().toLowerCase()));

		player.setGameMode(mode);
	}

}