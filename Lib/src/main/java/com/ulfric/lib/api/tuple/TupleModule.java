package com.ulfric.lib.api.tuple;

import com.ulfric.lib.api.module.SimpleModule;

public final class TupleModule extends SimpleModule {

	public TupleModule()
	{
		super("tuple", "Tuples module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		Tuples.impl = new Tuples.ITuples() {
			@Override
			public <A, B> Pair<A, B> newPair(A a, B b)
			{
				return new Pair<>(a, b);
			}

			@Override
			public <A, B, C> Triplet<A, B, C> newTriplet(A a, B b, C c)
			{
				return new Triplet<>(a, b, c);
			}
		};
	}

	@Override
	public void postDisable()
	{
		Tuples.impl = Tuples.ITuples.EMPTY;
	}

}
