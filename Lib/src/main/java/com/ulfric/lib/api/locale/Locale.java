package com.ulfric.lib.api.locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Pattern;

public final class Locale {

	static ILocale impl = ILocale.EMPTY;

	private Locale()
	{
	}

	public static String getLanguage(Player player)
	{
		return impl.getLanguage(player);
	}

	public static String getMessage(String message)
	{
		return impl.getMessage(message);
	}

	public static String getMessage(CommandSender sender, String message)
	{
		return impl.getMessage(sender, message);
	}

	public static String getMessage(Player player, String message)
	{
		return impl.getMessage(player, message);
	}

	public static void sendMass(String message, Object... objects)
	{
		impl.sendMass(message, objects);
	}

	public static void sendMassMeta(String meta, String message, Object... objects)
	{
		impl.sendMassMeta(meta, message, objects);
	}

	public static void sendMassPerm(String permission, String message, Object... objects)
	{
		impl.sendMassPerm(permission, message, objects);
	}

	public static void sendMassPerm(String permission, boolean has, String message, Object... objects)
	{
		impl.sendMassPerm(permission, has, message, objects);
	}

	public static void send(CommandSender sender, String message, Object... objects)
	{
		impl.send(sender, message, objects);
	}

	public static void send(Player player, String message, Object... objects)
	{
		impl.send(player, message, objects);
	}

	public static void sendAction(Player player, String message, Object... objects)
	{
		impl.sendAction(player, message, objects);
	}

	public static void sendWarning(CommandSender sender, String message, Object... objects)
	{
		impl.sendWarning(sender, message, objects);
	}

	public static void sendWarning(Player player, String message, Object... objects)
	{
		impl.sendWarning(player, message, objects);
	}

	public static void sendError(CommandSender sender, String message, Object... objects)
	{
		impl.sendError(sender, message, objects);
	}

	public static void sendError(Player player, String message, Object... objects)
	{
		impl.sendError(player, message, objects);
	}

	public static String asError(CommandSender sender, String message, Object... objects)
	{
		return impl.asError(sender, message, objects);
	}

	public static String asError(Player player, String message, Object... objects)
	{
		return impl.asError(player, message, objects);
	}

	public static void sendSuccess(CommandSender sender, String message, Object... objects)
	{
		impl.sendSuccess(sender, message, objects);
	}

	public static void sendSuccess(Player player, String message, Object... objects)
	{
		impl.sendSuccess(player, message, objects);
	}

	public static String asSuccess(CommandSender sender, String message, Object... objects)
	{
		return impl.asSuccess(sender, message, objects);
	}

	public static String asSuccess(Player player, String message, Object... objects)
	{
		return impl.asSuccess(player, message, objects);
	}

	public static void sendSpecial(CommandSender sender, String special, String message, Object... objects)
	{
		impl.sendSpecial(sender, special, message, objects);
	}

	public static void sendSpecial(Player player, String special, String message, Object... objects)
	{
		impl.sendSpecial(player, special, message, objects);
	}

	public static void sendTimelock(Player player, String message, long timelock)
	{
		impl.sendTimelock(player, message, timelock);
	}

	public static Pattern pattern()
	{
		return impl.pattern();
	}

	public static String englishUS()
	{
		return impl.englishUS();
	}

	protected interface ILocale {
		ILocale EMPTY = new ILocale() {
		};

		default String englishUS()
		{
			return null;
		}

		default void sendAction(Player player, String message, Object... objects)
		{
		}

		default String getLanguage(Player player)
		{
			return null;
		}

		default Map<String, String> getLocale(Player player)
		{
			return null;
		}

		default Map<String, String> getLocale(String locale)
		{
			return null;
		}

		default String getMessage(String message)
		{
			return null;
		}

		default String getMessage(CommandSender sender, String message)
		{
			return null;
		}

		default String getMessage(Player player, String message)
		{
			return null;
		}

		default void sendMass(String message, Object... objects)
		{
		}

		default void sendMassMeta(String meta, String message, Object... objects)
		{
		}

		default void sendMassPerm(String permission, String message, Object... objects)
		{
		}

		default void sendMassPerm(String permission, boolean has, String message, Object... objects)
		{
		}

		default void send(CommandSender sender, String message, Object... objects)
		{
		}

		default void send(Player player, String message, Object... objects)
		{
		}

		default void sendWarning(CommandSender sender, String message, Object... objects)
		{
		}

		default void sendWarning(Player player, String message, Object... objects)
		{
		}

		default void sendError(CommandSender sender, String message, Object... objects)
		{
		}

		default void sendError(Player player, String message, Object... objects)
		{
		}

		default String asError(CommandSender sender, String message, Object... objects)
		{
			return null;
		}

		default String asError(Player player, String message, Object... objects)
		{
			return null;
		}

		default void sendSuccess(CommandSender sender, String message, Object... objects)
		{
		}

		default void sendSuccess(Player player, String message, Object... objects)
		{
		}

		default String asSuccess(CommandSender sender, String message, Object... objects)
		{
			return null;
		}

		default String asSuccess(Player player, String message, Object... objects)
		{
			return null;
		}

		default String getSpecial(CommandSender sender, String special, String message, Object... objects)
		{
			return null;
		}

		default String getSpecial(Player player, String special, String message, Object... objects)
		{
			return null;
		}

		default String getSpecial(Map<String, String> locale, String name, String special, String message, Object... objects)
		{
			return null;
		}

		default void sendSpecial(CommandSender sender, String special, String message, Object... objects)
		{
		}

		default void sendSpecial(Player player, String special, String message, Object... objects)
		{
		}

		default void sendTimelock(Player player, String message, long timelock)
		{
		}

		default Pattern pattern()
		{
			return null;
		}
	}

}
