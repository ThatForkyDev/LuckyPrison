package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;

public class CommandDelpet extends SimpleCommand {

	public CommandDelpet()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);
	}

	@Override
	public void run()
	{
		Hooks.PETS.removePet((Player) this.getObject("player"), false, true);
	}

}