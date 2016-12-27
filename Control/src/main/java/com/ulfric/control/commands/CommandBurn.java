package com.ulfric.control.commands;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.lib.api.time.Timestamp;

public class CommandBurn extends SimpleCommand {


	public CommandBurn()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);

		this.withArgument("time", ArgStrategy.FUTURE, "control.time_needed");
	}


	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		Timestamp time = (Timestamp) this.getObject("time");

		long ticks = Ticks.fromMilliseconds(time.timeTill());

		int total = (int) (player.getFireTicks() + Ticks.fromMilliseconds(time.timeTill()));

		player.setFireTicks(total);

		Locale.sendSuccess(this.getSender(), "control.burn", player.getName(), ticks, total);
	}


}