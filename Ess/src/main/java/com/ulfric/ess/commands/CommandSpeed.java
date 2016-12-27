package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandSpeed extends SimpleCommand {

	public CommandSpeed()
	{
		this.withEnforcePlayer();

		this.withArgument("speed", ArgStrategy.FLOAT, 1F);

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.SPEED_OTHERS);
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		float speed = Math.abs((float) this.getObject("speed")) / 10;

		if (speed <= 0)
		{
			Locale.sendError(this.getSender(), "ess.speed_small_err");

			return;
		}
		else if (speed > 1)
		{
			Locale.sendError(this.getSender(), "ess.speed_big_err");

			return;
		}

		if (player.isFlying())
		{
			Locale.sendSuccess(this.getSender(), "ess.speed_fly", StringUtils.formatDecimal(speed*10));

			player.setFlySpeed(speed);

			return;
		}

		Locale.sendSuccess(this.getSender(), "ess.speed_walk", StringUtils.formatDecimal(speed*10));

		player.setWalkSpeed(speed);
	}

}