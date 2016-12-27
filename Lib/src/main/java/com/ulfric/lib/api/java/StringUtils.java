package com.ulfric.lib.api.java;

import java.util.Collection;

public final class StringUtils {

	static IStringUtils impl = IStringUtils.EMPTY;

	private StringUtils()
	{
	}

	public static String acronym(String string)
	{
		return impl.acronym(string);
	}

	public static String mergeNicely(Collection<? extends CharSequence> collection)
	{
		return impl.mergeNicely(collection);
	}

	public static String mergeNamedNicely(Collection<? extends Named> collection)
	{
		return impl.mergeNamedNicely(collection);
	}

	public static String prepend(String message, String prefix)
	{
		return impl.prepend(message, prefix);
	}

	public static String findOption(String whole, String split)
	{
		return impl.findOption(whole, split);
	}

	public static String findOption(String whole, String split, String defaultValue)
	{
		return impl.findOption(whole, split, defaultValue);
	}

	public static String makeNumeric(String string)
	{
		return impl.makeNumeric(string);
	}

	public static String formatMoneyFully(double amount)
	{
		return impl.formatMoneyFully(amount);
	}

	public static String formatMoneyFully(double amount, boolean tiny)
	{
		return impl.formatMoneyFully(amount, tiny);
	}

	public static String formatMoney(double amount)
	{
		return impl.formatMoney(amount);
	}

	public static String formatDecimal(Number amount)
	{
		return impl.formatDecimal(amount);
	}

	public static String formatNumber(Number amount)
	{
		return impl.formatNumber(amount);
	}

	public static String formatShortWordNumber(Number amount)
	{
		return impl.formatShortWordNumber(amount);
	}

	public static String formatShortWordNumber(Number total, boolean tiny)
	{
		return impl.formatShortWordNumber(total, tiny);
	}

	public static String formatIP(String address)
	{
		return impl.formatIP(address);
	}

	public static boolean isSimilar(String str1, String str2)
	{
		return impl.isSimilar(str1, str2);
	}

	public static int encodedDiff(String str1, String str2)
	{
		return impl.encodedDiff(str1, str2);
	}

	public static int diff(String str1, String str2)
	{
		return impl.diff(str1, str2);
	}

	public static String merge(Iterable<String> iterable, char seperator)
	{
		return impl.merge(iterable, seperator);
	}

	public static String trimStart(String string, char... characters) {
		return impl.trimStart(string, characters);
	}

	protected interface IStringUtils {
		IStringUtils EMPTY = new IStringUtils() {
		};

		default String acronym(String string)
		{
			return string;
		}

		default String prepend(String message, String prefix)
		{
			return message;
		}

		default boolean isSimilar(String str1, String str2)
		{
			return false;
		}

		default int diff(String str1, String str2)
		{
			return 0;
		}

		default int encodedDiff(String str1, String str2)
		{
			return 0;
		}

		default String merge(Iterable<? extends CharSequence> iterable, char seperator)
		{
			return null;
		}

		default String mergeNicely(Collection<? extends CharSequence> collection)
		{
			return null;
		}

		default String mergeNamedNicely(Collection<? extends Named> collection)
		{
			return null;
		}

		default String findOption(String whole, String split)
		{
			return null;
		}

		default String findOption(String whole, String split, String defaultValue)
		{
			return null;
		}

		default String makeNumeric(String string)
		{
			return string;
		}

		default String formatMoneyFully(double amount)
		{
			return null;
		}

		default String formatMoneyFully(double amount, boolean tiny)
		{
			return null;
		}

		default String formatMoney(double amount)
		{
			return null;
		}

		default String formatDecimal(Number amount)
		{
			return null;
		}

		default String formatNumber(Number amount)
		{
			return null;
		}

		default String formatShortWordNumber(Number amount)
		{
			return null;
		}

		default String formatShortWordNumber(Number total, boolean tiny)
		{
			return null;
		}

		default String formatIP(String address)
		{
			return null;
		}

		default String trimStart(String string, char... characters)
		{
			return null;
		}
	}

}
