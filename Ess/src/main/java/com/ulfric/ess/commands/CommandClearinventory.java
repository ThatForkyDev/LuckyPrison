package com.ulfric.ess.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.inventory.InventoryUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandClearinventory extends SimpleCommand {


	public CommandClearinventory()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "ess.clearinv.others");
	}


	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");

		InventoryUtils.clear(target.getInventory());

		Locale.sendSuccess(this.getSender(), "ess.clearinv", this.isPlayer() && target.equals(this.getPlayer()) ? Locale.getMessage(this.getSender(), "system.your") : target.getName() + "'s");
	}


}