package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;

public class CommandEnderchest extends SimpleCommand
{

	public CommandEnderchest()
	{
		this.withEnforcePlayer();

		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "ess.enderchest.others");
	}

	@Override
	public void run()
	{
		if (this.hasObject("player")) {
			getPlayer().closeInventory();
			getPlayer().openInventory(((Player) getObject("player")).getEnderChest());
		} else {
			getPlayer().closeInventory();
			getPlayer().openInventory(getPlayer().getEnderChest());
		}
	}

}
