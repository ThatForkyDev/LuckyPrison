package com.ulfric.lib.api.chat;

import com.ulfric.lib.api.time.Timestamp;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.ChatColor;

import java.util.Collection;

public final class Chat {

	static IChat impl = IChat.EMPTY;

	private Chat()
	{
	}

	public static char seperator()
	{
		return impl.seperator();
	}

	public static String color(String message)
	{
		return impl.color(message);
	}

	public static Collection<String> color(Collection<String> messages)
	{
		return impl.color(messages);
	}

	public static String serializeColor(String string)
	{
		return impl.serializeColor(string);
	}

	public static ChatMessage newChatMessage(Timestamp time, String message)
	{
		return impl.newChatMessage(time, message);
	}

	public static IChatBaseComponent asMinecraftText(String string)
	{
		return impl.asMinecraftText(string);
	}

	public static ChatBook newChatBook(String title, String command, int pageSize)
	{
		return impl.newChatBook(title, command, pageSize);
	}

	public static Message newStringMessage(String message)
	{
		return impl.newStringMessage(message);
	}

	public static Message newLocaleMessage(String message)
	{
		return impl.newLocaleMessage(message);
	}

	public static String stripBadColor(String message)
	{
		return impl.stripBadColor(message);
	}

	public static String invisibleSpace(int amount)
	{
		return impl.invisibleSpace(amount);
	}

	public static String linePercentage(Number number, Number total, char bar, int bars, ChatColor used, ChatColor available, ChatColor full, boolean showPercentile)
	{
		return impl.linePercentage(number, total, bar, bars, used, available, full, showPercentile);
	}

	protected interface IChat {
		IChat EMPTY = new IChat() {
		};

		default String color(String message)
		{
			return message;
		}

		default String linePercentage(Number number, Number total, char bar, int bars, ChatColor used, ChatColor available, ChatColor full, boolean showPercentile)
		{
			return null;
		}

		default char seperator()
		{
			return ' ';
		}

		default Collection<String> color(Collection<String> messages)
		{
			return messages;
		}

		default String serializeColor(String string)
		{
			return string;
		}

		default String stripBadColor(String message)
		{
			return message;
		}

		default ChatMessage newChatMessage(Timestamp time, String message)
		{
			return null;
		}

		default IChatBaseComponent asMinecraftText(String string)
		{
			return null;
		}

		default ChatBook newChatBook(String title, String command, int pageSize)
		{
			return null;
		}

		default Message newStringMessage(String message)
		{
			return null;
		}

		default Message newLocaleMessage(String message)
		{
			return null;
		}

		default String invisibleSpace(int amount)
		{
			return null;
		}
	}

}
