package com.ulfric.cash.currency;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;

public class CommandSetbal extends SimpleCommand {

	public CommandSetbal()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);

		this.withArgument("price", PriceArg.INSTANCE, "cash.pay_invalid_price");
	}

	@Override
	public void run()
	{
		((Price) this.getObject("price")).setBal(((OfflinePlayer) this.getObject("player")).getUniqueId());
	}

}