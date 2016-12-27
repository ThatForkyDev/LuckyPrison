package com.ulfric.cash.currency.token;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;

public class CommandTokens extends SimpleCommand {

	public CommandTokens()
	{
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		Locale.send(this.getSender(), "cash.tokens", (this.isPlayer() && !player.equals(this.getPlayer())) ? player.getName() + "'s" : Locale.getMessage(this.getSender(), "system.your"), StringUtils.formatNumber(Tokens.getBalance(player.getUniqueId())));
	}

}