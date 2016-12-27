package com.ulfric.cash.currency.dollar;

import org.bukkit.OfflinePlayer;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandBalance extends SimpleCommand {

	public CommandBalance()
	{
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		OfflinePlayer target = (OfflinePlayer) this.getObject("player");
		if (target == null) { // Occurs if incorrect user supplied by console
			Locale.sendError(this.getSender(), "cash.balance_not_found");
			return;
		}
		double balance = Money.getBalance(target.getUniqueId());
		String fbalance = StringUtils.formatNumber(balance);
		String value = Strings.formatF("&a${0} {1}", fbalance, (balance > 999 ? Strings.format("&6({0}{1})", fbalance.split(",")[0], StringUtils.formatShortWordNumber(balance)) : Strings.BLANK));

		if (this.isPlayer() && target.getUniqueId().equals(this.getUniqueId()))
		{
			Locale.send(this.getPlayer(), "cash.balance", Locale.getMessage(this.getPlayer(), "system.your"), value);

			return;
		}

		Locale.send(this.getSender(), "cash.balance", target.getName() + "'s", value);
	}

}