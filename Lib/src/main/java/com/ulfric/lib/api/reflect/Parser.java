package com.ulfric.lib.api.reflect;

public final class Parser {

	static IParser impl = IParser.EMPTY;

	private Parser()
	{
	}

	public static <T> T parse(String string, Class<? extends T> clazz)
	{
		return impl.parse(string, clazz);
	}

	public static void register(Class<?> value, Class<?> parser, String method)
	{
		impl.register(value, parser, method);
	}

	protected interface IParser {
		IParser EMPTY = new IParser() {
		};

		default <T> T parse(String string, Class<? extends T> clazz)
		{
			return null;
		}

		default void register(Class<?> value, Class<?> parser, String method)
		{
		}
	}

}
