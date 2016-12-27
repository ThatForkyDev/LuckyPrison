package com.ulfric.prison.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandCraft extends SimpleCommand {


	public CommandCraft()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "prison.workbench.others");
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");
		target.openWorkbench(target.getLocation(), true);
	}


}