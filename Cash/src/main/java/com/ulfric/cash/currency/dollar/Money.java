package com.ulfric.cash.currency.dollar;

import java.util.UUID;

public class Money {

	protected static IMoney impl = IMoney.EMPTY;

	public static double getBalance(UUID uuid)
	{
		return Money.impl.getBalance(uuid);
	}

	public static boolean take(UUID uuid, double amount)
	{
		return Money.impl.take(uuid, amount);
	}

	public static boolean take(UUID uuid, double amount, String reason)
	{
		return Money.impl.take(uuid, amount, reason);
	}

	public static void give(UUID uuid, double amount)
	{
		Money.impl.give(uuid, amount);
	}

	public static void give(UUID uuid, double amount, String reason)
	{
		Money.impl.give(uuid, amount, reason);
	}

	public static void set(UUID uuid, double amount)
	{
		Money.impl.set(uuid, amount);
	}

	protected interface IMoney
	{
		IMoney EMPTY = new IMoney() { };

		default double getBalance(UUID uuid) { return 0D; }

		default boolean take(UUID uuid, double amount) { return false; }

		default boolean take(UUID uuid, double amount, String reason) { return false; }

		default void give(UUID uuid, double amount) { }

		default void give(UUID uuid, double amount, String reason) { }

		default void set(UUID uuid, double amount) { }
	}

}