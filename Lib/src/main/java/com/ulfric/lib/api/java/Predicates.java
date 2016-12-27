package com.ulfric.lib.api.java;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class Predicates {

	static IPredicates impl = IPredicates.EMPTY;

	private Predicates()
	{
	}

	public static <T> Predicate<T> always(boolean value)
	{
		return impl.always(value);
	}

	public static <T> Predicate<T> alwaysTrue()
	{
		return impl.alwaysTrue();
	}

	public static <T> Predicate<T> alwaysFalse()
	{
		return impl.alwaysFalse();
	}

	public static <T> Predicate<T> alwaysRandom()
	{
		return impl.alwaysRandom();
	}

	public static <T, U> BiPredicate<T, U> alwaysBi(boolean value)
	{
		return impl.alwaysBi(value);
	}

	public static <T, U> BiPredicate<T, U> alwaysBiTrue()
	{
		return impl.alwaysBiTrue();
	}

	public static <T, U> BiPredicate<T, U> alwaysBiFalse()
	{
		return impl.alwaysBiFalse();
	}

	public static <T, U> BiPredicate<T, U> alwaysBiRandom()
	{
		return impl.alwaysBiRandom();
	}

	protected interface IPredicates {
		IPredicates EMPTY = new IPredicates() {
		};

		default <T> Predicate<T> always(boolean value)
		{
			return null;
		}

		default <T> Predicate<T> alwaysTrue()
		{
			return null;
		}

		default <T> Predicate<T> alwaysFalse()
		{
			return null;
		}

		default <T> Predicate<T> alwaysRandom()
		{
			return null;
		}

		default <T, U> BiPredicate<T, U> alwaysBi(boolean value)
		{
			return null;
		}

		default <T, U> BiPredicate<T, U> alwaysBiTrue()
		{
			return null;
		}

		default <T, U> BiPredicate<T, U> alwaysBiFalse()
		{
			return null;
		}

		default <T, U> BiPredicate<T, U> alwaysBiRandom()
		{
			return null;
		}
	}

}
