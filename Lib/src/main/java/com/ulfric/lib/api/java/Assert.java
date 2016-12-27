package com.ulfric.lib.api.java;

public final class Assert {

	static IAssert impl = IAssert.EMPTY;

	private Assert()
	{
	}

	public static <T> Assertion<T> of(T object)
	{
		return impl.of(object);
	}

	public static <T> Assertion<T> ofNullable(T object)
	{
		return impl.ofNullable(object);
	}

	public static void isTrue(boolean value)
	{
		impl.isTrue(value);
	}

	public static void isTrue(boolean value, String message)
	{
		impl.isTrue(value, message);
	}

	public static <T> T notNull(T object)
	{
		return impl.notNull(object);
	}

	public static <T> T notNull(T object, String message)
	{
		return impl.notNull(object, message);
	}

	public static <T> T isNull(T object)
	{
		return impl.isNull(object);
	}

	public static <T> T isNull(T object, String message)
	{
		return impl.isNull(object, message);
	}

	public static void noneNull(Object... array)
	{
		impl.noneNull(array);
	}

	public static void noneNull(Iterable<?> collection)
	{
		impl.noneNull(collection);
	}

	public static void isFalse(boolean value)
	{
		impl.isFalse(value);
	}

	public static void isFalse(boolean value, String message)
	{
		impl.isFalse(value, message);
	}

	public static void isNotEmpty(String string)
	{
		impl.isNotEmpty(string);
	}

	public static void isNotEmpty(String string, String nullMessage, String emptyMessage)
	{
		impl.isNotEmpty(string, nullMessage, emptyMessage);
	}

	public static void isInstanceof(Class<?> clazz, Object object)
	{
		impl.isInstanceof(clazz, object);
	}

	public static void isInstanceof(Class<?> clazz, Object object, String message)
	{
		impl.isInstanceof(clazz, object, message);
	}

	public static void isNotInstanceof(Class<?> clazz, Object object)
	{
		impl.isNotInstanceof(clazz, object);
	}

	public static void isNotInstanceof(Class<?> clazz, Object object, String message)
	{
		impl.isNotInstanceof(clazz, object, message);
	}

	protected interface IAssert {
		IAssert EMPTY = new IAssert() {
		};

		default void isTrue(boolean value)
		{
		}

		default <T> Assertion<T> of(T object)
		{
			return null;
		}

		default <T> Assertion<T> ofNullable(T object)
		{
			return null;
		}

		default void isTrue(boolean value, String message)
		{
		}

		default <T> T notNull(T object)
		{
			return object;
		}

		default <T> T notNull(T object, String message)
		{
			return object;
		}

		default void noneNull(Object... array)
		{
		}

		default void noneNull(Iterable<?> collection)
		{
		}

		default <T> T isNull(T object)
		{
			return object;
		}

		default <T> T isNull(T object, String message)
		{
			return object;
		}

		default void isFalse(boolean value)
		{
		}

		default void isFalse(boolean value, String message)
		{
		}

		default void isNotEmpty(String string)
		{
		}

		default void isNotEmpty(String string, String nullMessage, String emptyMessage)
		{
		}

		default void isInstanceof(Class<?> clazz, Object object)
		{
		}

		default void isInstanceof(Class<?> clazz, Object object, String message)
		{
		}

		default void isNotInstanceof(Class<?> clazz, Object object)
		{
		}

		default void isNotInstanceof(Class<?> clazz, Object object, String message)
		{
		}
	}

}
