package com.ulfric.cash.currency;

import java.util.UUID;

public abstract class Price {

	protected static IPrice impl = IPrice.EMPTY;

	public static Price money(Number price)
	{
		return Price.impl.money(price);
	}

	public static Price token(Number price)
	{
		return Price.impl.token(price);
	}

	public static Price of(String price)
	{
		return Price.impl.of(price);
	}

	protected interface IPrice
	{
		IPrice EMPTY = new IPrice() { };

		default Price money(Number price) { return null; }

		default Price token(Number price) { return null; }

		default Price of(String price) { return null; }
	}

	public abstract Number getAmount();

	public abstract Number getRemaining(UUID uuid);

	public abstract void give(UUID uuid);

	public abstract void give(UUID uuid, String reason);

	public abstract boolean take(UUID uuid, String reason);

	public abstract void setBal(UUID uuid);

	public abstract String toString(boolean small);

	public abstract Price add(Price price);

	public abstract Price subtract(Price price);

}