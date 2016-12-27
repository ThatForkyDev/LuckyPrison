package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.teleport.TeleportUtils;

public class CommandTeleporthere extends SimpleCommand {

	public CommandTeleporthere()
	{
		this.withEnforcePlayer();

		this.withArgument(Argument.REQUIRED_PLAYER);
	}


	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		if (target.equals(this.getPlayer()))
		{
			Locale.sendError(this.getPlayer(), "ess.teleport_not_found");

			return;
		}

		TeleportUtils.teleport(target, this.getLocation());

		return;
	}

}