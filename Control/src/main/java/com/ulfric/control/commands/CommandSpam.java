package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.server.Commands;

public class CommandSpam extends SimpleCommand {


	public CommandSpam()
	{
		this.withIndexUnusedArgs();

		this.withArgument("int", ArgStrategy.INTEGER, 1);

		this.withArgument("start", ArgStrategy.STRING, "spam_required");
	}


	@Override
	public void run()
	{
		int times = (int) this.getObject("int");

		String message = Strings.format("{0} {1}", this.getObject("start"), this.getUnusedArgs());

		if (!this.isPlayer())
		{
			for (int x = 0; x < times; x++)
			{
				Commands.dispatch(message);
			}

			return;
		}

		Player player = this.getPlayer();

		for (int x = 0; x < times; x++)
		{
			player.chat(message);
		}
	}


}