package com.ulfric.tag.command;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.locale.Locale;

public class CommandCombattag extends SimpleCommand {

	public CommandCombattag()
	{
		this.withEnforcePlayer();

		this.withArgument(Argument.builder().withPath("player").withStrategy(ArgStrategy.PLAYER).withDefaultCallable(this::getPlayer).withNode("tag.view").withCap(1));
	}

	@Override
	public void run()
	{
		Player player = (Player) this.getObject("player");

		if (player.hasMetadata("_ulf_combattag"))
		{
			Locale.send(this.getPlayer(), "tag.warn");

			return;
		}

		Locale.send(this.getPlayer(), "tag.safe");
	}

}