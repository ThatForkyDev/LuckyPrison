package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandAbuse extends SimpleCommand {

	public CommandAbuse()
	{
		this.withEnforcePlayer();

		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		target.setVelocity(player.getLocation().getDirection().add(0, 0.1, 0).multiply(3));
	}

}