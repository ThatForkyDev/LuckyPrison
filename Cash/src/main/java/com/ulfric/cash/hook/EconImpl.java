package com.ulfric.cash.hook;

import java.util.UUID;

import com.ulfric.cash.currency.dollar.Money;
import com.ulfric.cash.currency.token.Tokens;
import com.ulfric.lib.api.hook.EconHook.IEconHook;
import com.ulfric.lib.api.hook.EconHook.Price;

public enum EconImpl implements IEconHook {

	INSTANCE;

	@Override
	public boolean takeMoney(UUID uuid, double amount, String reason)
	{
		return Money.take(uuid, amount, reason);
	}

	@Override
	public void giveMoney(UUID uuid, double amount, String reason)
	{
		Money.give(uuid, amount, reason);
	}

	@Override
	public double getMoney(UUID uuid)
	{
		return Money.getBalance(uuid);
	}

	@Override
	public boolean takeTokens(UUID uuid, int amount)
	{
		return Tokens.take(uuid, amount);
	}

	@Override
	public void giveTokens(UUID uuid, int amount)
	{
		Tokens.give(uuid, amount);
	}

	@Override
	public int getTokens(UUID uuid)
	{
		return Tokens.getBalance(uuid);
	}

	@Override
	public Price price(String price)
	{
		com.ulfric.cash.currency.Price priceObj = com.ulfric.cash.currency.Price.of(price);

		if (priceObj == null) return null;

		return new PriceImpl(priceObj);
	}

	@Override
	public Price priceMoney(double price)
	{
		return new PriceImpl(com.ulfric.cash.currency.Price.money(price));
	}

	@Override
	public Price priceTokens(int price)
	{
		return new PriceImpl(com.ulfric.cash.currency.Price.token(price));
	}

}