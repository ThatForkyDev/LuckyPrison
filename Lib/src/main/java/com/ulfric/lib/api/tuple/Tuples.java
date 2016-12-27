package com.ulfric.lib.api.tuple;

public final class Tuples {

	static ITuples impl = ITuples.EMPTY;

	private Tuples()
	{
	}

	public static <A, B> Pair<A, B> newPair(A a, B b)
	{
		return impl.newPair(a, b);
	}

	public static <A, B, C> Triplet<A, B, C> newTriplet(A a, B b, C c)
	{
		return impl.newTriplet(a, b, c);
	}

	protected interface ITuples {
		ITuples EMPTY = new ITuples() {
		};

		default <A, B> Pair<A, B> newPair(A a, B b)
		{
			return null;
		}

		default <A, B, C> Triplet<A, B, C> newTriplet(A a, B b, C c)
		{
			return null;
		}
	}

}
