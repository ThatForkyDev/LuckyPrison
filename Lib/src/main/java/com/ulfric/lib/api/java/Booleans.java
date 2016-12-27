package com.ulfric.lib.api.java;

import org.bukkit.command.CommandSender;

public final class Booleans {

	static IBooleans impl = IBooleans.EMPTY;

	private Booleans()
	{
	}

	public static boolean parseBoolean(String string)
	{
		return impl.parseBoolean(string);
	}

	public static String fancify(boolean value)
	{
		return impl.fancify(value);
	}

	public static String fancify(boolean value, CommandSender sender)
	{
		return impl.fancify(value, sender);
	}

	public static String fancify(boolean value, String good, String bad)
	{
		return impl.fancify(value, good, bad);
	}

	public static boolean isTrue(Object value)
	{
		return impl.isTrue(value);
	}

	public static boolean isFalse(Object value)
	{
		return impl.isFalse(value);
	}

	protected interface IBooleans {
		IBooleans EMPTY = new IBooleans() {
		};

		default boolean parseBoolean(String value)
		{
			return false;
		}

		default boolean isFalse(Object value)
		{
			return false;
		}

		default boolean isTrue(Object value)
		{
			return false;
		}

		default String fancify(boolean value)
		{
			return null;
		}

		default String fancify(boolean value, CommandSender sender)
		{
			return null;
		}

		default String fancify(boolean value, String good, String bad)
		{
			return null;
		}
	}

}
