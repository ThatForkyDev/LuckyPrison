package com.ulfric.cash.hook;

import java.util.UUID;

import com.ulfric.cash.currency.token.TokenPrice;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.java.Assert;

public class PriceImpl implements Price {

	protected PriceImpl(com.ulfric.cash.currency.Price price)
	{
		Assert.notNull(price, "The price was null!");

		this.price = price;

		this.token = price instanceof TokenPrice;
	}

	private final com.ulfric.cash.currency.Price price;

	private final boolean token;
	@Override
	public boolean isToken()
	{
		return this.token;
	}

	@Override
	public Number getAmount()
	{
		return this.price.getAmount();
	}

	@Override
	public Number getRemaining(UUID uuid)
	{
		return this.price.getRemaining(uuid);
	}

	@Override
	public void give(UUID uuid, String reason)
	{
		this.price.give(uuid, reason);
	}

	@Override
	public boolean take(UUID uuid, String reason)
	{
		return this.price.take(uuid, reason);
	}

	@Override
	public void setBal(UUID uuid)
	{
		this.price.setBal(uuid);
	}

	@Override
	public String toString()
	{
		return this.price.toString();
	}

	@Override
	public String toString(boolean flag)
	{
		return this.price.toString(flag);
	}

	@Override
	public Price add(Price price)
	{
		if (!(price instanceof PriceImpl)) return this;

		return new PriceImpl(this.price.add(((PriceImpl) price).price));
	}

	@Override
	public Price subtract(Price price)
	{
		if (!(price instanceof PriceImpl)) return this;

		return new PriceImpl(this.price.subtract(((PriceImpl) price).price));
	}

}