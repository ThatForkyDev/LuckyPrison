package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandCloneinv extends SimpleCommand {

	public CommandCloneinv()
	{
		this.withEnforcePlayer();

		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		player.getInventory().setContents(target.getInventory().getContents());

		Locale.sendSuccess(player, "control.cloneinv", target.getName());
	}

}