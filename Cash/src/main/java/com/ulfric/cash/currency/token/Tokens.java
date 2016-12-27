package com.ulfric.cash.currency.token;

import java.util.UUID;

public class Tokens {

	protected static ITokens impl = ITokens.EMPTY;

	public static int getBalance(UUID uuid)
	{
		return Tokens.impl.getBalance(uuid);
	}

	public static boolean take(UUID uuid, int amount)
	{
		return Tokens.impl.take(uuid, amount);
	}

	public static void give(UUID uuid, int amount)
	{
		Tokens.impl.give(uuid, amount);
	}

	public static void set(UUID uuid, int amount)
	{
		Tokens.impl.set(uuid, amount);
	}

	protected interface ITokens
	{
		ITokens EMPTY = new ITokens() { };

		default int getBalance(UUID uuid) { return 0; }

		default void set(UUID uuid,int amount) { }

		default boolean take(UUID uuid, int amount) { return false; }

		default void give(UUID uuid, int amount) { }
	}

}