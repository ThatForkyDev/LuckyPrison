package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;

public class CommandSudo extends PunishmentCommand {


	public CommandSudo()
	{
		super(Argument.REQUIRED_PLAYER);

		this.withIndexUnusedArgs();

		this.withArgument("start", ArgStrategy.STRING, "control.sudo_message_requited");
	}


	@Override
	public void execute()
	{
		((Player) this.getObject("player")).chat(Strings.format("{0} {1}", this.getObject("start"), this.getUnusedArgs()));
	}


}