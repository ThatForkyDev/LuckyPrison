package com.ulfric.ess.commands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.teleport.TeleportUtils;

public class CommandTop extends SimpleCommand {

	public CommandTop()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		Location location = player.getLocation().clone();
		location.setY(location.getWorld().getHighestBlockYAt(location) + 1);

		TeleportUtils.teleport(player, location, 5);
	}

}