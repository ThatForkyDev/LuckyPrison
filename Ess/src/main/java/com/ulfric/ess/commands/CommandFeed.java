package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandFeed extends SimpleCommand {

	public CommandFeed()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.FEED_OTHERS);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		player.setFoodLevel(20);

		if (this.isPlayer() && player.equals(this.getPlayer()))
		{
			Locale.send(this.getPlayer(), "ess.feed");

			return;
		}

		Locale.sendSuccess(this.getSender(), "ess.feed_other", player.getName());
	}

}