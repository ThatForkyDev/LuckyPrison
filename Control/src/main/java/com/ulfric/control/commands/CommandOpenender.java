package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandOpenender extends SimpleCommand {

	public CommandOpenender()
	{
		this.withEnforcePlayer();

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "control.openender.other");
	}

	@Override
	public void run()
	{
		this.getPlayer().openInventory(((Player) this.getObject("player")).getEnderChest());
	}

}