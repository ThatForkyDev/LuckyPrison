package com.ulfric.lib.api.java;

import com.ulfric.lib.api.chat.Chat;

import java.util.UUID;
import java.util.stream.IntStream;

public final class Strings {

	public static final String BLANK = "";

	public static final String FAKE_LINEBREAK = "<n>";

	public static final String FAKE_SPACE = "<s>";

	public static final String PLAYER = "<player>";

	public static final UUID PACKETS_UUID = UUID.fromString("e8ff7692-813a-4f8c-8af6-ccf717d7f3f7");

	private Strings()
	{
	}

	public static boolean isBlank(String string)
	{
		return string.isEmpty() || IntStream.range(0, string.length())
				.allMatch(i -> Character.isWhitespace(string.charAt(i)));
	}

	public static boolean isNullOrBlank(String string)
	{
		return string == null || isBlank(string);
	}

	public static String fromString(String string)
	{
		return string;
	}

	public static String fakeSpace(String string)
	{
		return string.replace(" ", FAKE_SPACE);
	}

	public static String space(String string)
	{
		return string.replace(FAKE_SPACE, " ");
	}

	public static String format(String format, Object object)
	{
		return format.replace("{0}", String.valueOf(object));
	}

	public static String format(String format, Object... objects)
	{
		for (int x = 0; x < objects.length; x++)
		{
			format = format.replace(format("{{0}}", x), String.valueOf(objects[x]));
		}

		return format;
	}

	public static String formatF(String format, Object... objects)
	{
		return Chat.color(format(format, objects).replace("<n>", "\n"));
	}

}
