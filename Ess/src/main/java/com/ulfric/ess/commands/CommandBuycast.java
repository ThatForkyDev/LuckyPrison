package com.ulfric.ess.commands;

import java.util.Optional;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandBuycast extends SimpleCommand {

	public CommandBuycast()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);

		this.withIndexUnusedArgs();
	}

	@Override
	public void run()
	{
		Locale.sendMass("ess.buycast",
				((OfflinePlayer) this.getObject("player")).getName(),
				Optional.ofNullable(this.getUnusedArgs()).orElse("unknown"));
	}

}