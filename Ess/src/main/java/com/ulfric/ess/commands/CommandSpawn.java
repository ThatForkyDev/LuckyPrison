package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.ess.modules.ModuleSpawnpoint;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.teleport.TeleportUtils;

public class CommandSpawn extends SimpleCommand {

	public CommandSpawn()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.WARP_OTHERS);
	}

	@Override
	public void run()
	{
		TeleportUtils.teleport((Player) this.getObject("player"), ModuleSpawnpoint.get().getSpawn().getLocation(), 5);
	}

}