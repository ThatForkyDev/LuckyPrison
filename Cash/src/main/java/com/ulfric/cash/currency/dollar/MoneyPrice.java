package com.ulfric.cash.currency.dollar;

import java.util.UUID;

import com.ulfric.cash.currency.Price;
import com.ulfric.lib.api.java.StringUtils;

public class MoneyPrice extends Price {

	public MoneyPrice(double amount)
	{
		this.amount = amount;
	}

	private final double amount;
	@Override
	public Double getAmount() { return this.amount; }

	@Override
	public Double getRemaining(UUID uuid)
	{
		double balance = Money.getBalance(uuid);

		if (balance > this.amount) return 0D;

		return this.amount - balance;
	}

	@Override
	public void give(UUID uuid)
	{
		Money.give(uuid, this.amount);
	}

	@Override
	public void give(UUID uuid, String reason)
	{
		Money.give(uuid, this.amount, reason);
	}

	@Override
	public boolean take(UUID uuid, String reason)
	{
		return Money.take(uuid, this.amount, reason);
	}

	@Override
	public void setBal(UUID uuid)
	{
		Money.set(uuid, this.amount);
	}

	@Override
	public String toString()
	{
		return this.toString(false);
	}

	@Override
	public String toString(boolean small)
	{
		return StringUtils.formatMoneyFully(this.amount, small);
	}

	@Override
	public MoneyPrice add(Price price)
	{
		if (!(price instanceof MoneyPrice)) return this;

		return new MoneyPrice(this.amount + ((MoneyPrice) price).getAmount());
	}

	@Override
	public MoneyPrice subtract(Price price)
	{
		if (!(price instanceof MoneyPrice)) return this;

		return new MoneyPrice(this.amount - ((MoneyPrice) price).getAmount());
	}

}