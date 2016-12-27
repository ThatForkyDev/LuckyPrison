package com.ulfric.ess.commands;

import org.bukkit.Location;
import org.bukkit.World;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.teleport.TeleportUtils;

public class CommandTeleportpos extends SimpleCommand {


	public CommandTeleportpos()
	{
		this.withEnforcePlayer();

		this.withArgument("x", ArgStrategy.DOUBLE, "ess.tppos_no_x");
		this.withArgument("y", ArgStrategy.DOUBLE, "ess.tppos_no_y");
		this.withArgument("z", ArgStrategy.DOUBLE, "ess.tppos_no_z");
		this.withArgument("world", ArgStrategy.WORLD, () -> this.getLocation().getWorld());
	}


	@Override
	public void run()
	{
		TeleportUtils.teleport(this.getPlayer(), new Location((World) this.getObject("world"), (double) this.getObject("x"), (double) this.getObject("y"), (double) this.getObject("z")));
	}


}