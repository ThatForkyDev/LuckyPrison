package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.teleport.TeleportUtils;

public class CommandTeleport extends SimpleCommand {

	public CommandTeleport()
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
			Locale.sendError(this.getPlayer(), "ess.teleport_err");

			return;
		}

		TeleportUtils.teleport(this.getPlayer(), target.getLocation());
	}

}