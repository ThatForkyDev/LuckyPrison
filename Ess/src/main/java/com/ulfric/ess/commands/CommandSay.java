package com.ulfric.ess.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;

public class CommandSay extends SimpleCommand {

	public CommandSay()
	{
		this.withIndexUnusedArgs();

		this.withArgument(Argument.builder().withPath("start").withStrategy(ArgStrategy.STRING).withUsage("ess.say_blank").withRemovalExclusion());
	}


	@Override
	public void run()
	{
		String message = Strings.formatF(this.getUnusedArgs());

		for (Player player : Bukkit.getOnlinePlayers())
		{
			player.sendMessage(message.replace(Strings.PLAYER, player.getName()));
		}
	}

}