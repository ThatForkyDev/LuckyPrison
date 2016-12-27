package com.ulfric.cash.currency;

import org.bukkit.entity.Player;

import com.ulfric.cash.currency.dollar.MoneyPrice;
import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;

public class CommandPay extends SimpleCommand {

	public CommandPay()
	{
		this.withArgument(Argument.REQUIRED_PLAYER);
		this.withArgument("price", PriceArg.INSTANCE, "cash.pay_invalid_price");
	}

	@Override
	public void run()
	{
		Player target = (Player) this.getObject("player");
		if (this.isPlayer() && this.getPlayer().equals(target))
		{
			Locale.sendError(this.getPlayer(), "cash.pay_self");

			return;
		}

		Price price = (Price) this.getObject("price");
		boolean money = price instanceof MoneyPrice;

		long longvalue = price.getAmount().longValue();

		if (longvalue <= 0 && !this.hasPermission("cash.overspend"))
		{
			if (money)
			{
				Locale.sendError(this.getSender(), "cash.pay_min_price", "$100");

				return;
			}

			Locale.sendError(this.getSender(), "cash.pay_min_price", "1T");

			return;
		}

		if (this.isPlayer() && !price.take(this.getUniqueId(), "Payment to " + target.getName()) && !this.hasPermission("cash.overspend"))
		{
			Locale.sendError(this.getSender(), "cash.pay_not_enough", money ? "cash" : "tokens");

			return;
		}

		price.give(target.getUniqueId(), "Payment from " + this.getName());

		String amount = price.toString();

		Locale.sendSuccess(this.getSender(), "cash.pay", amount, target.getName());

		if (!target.isOnline()) return;

		Locale.send(target.getPlayer(), "cash.pay_receive", amount, this.getName());

		return;
	}

}