package com.ulfric.lib.api.tuple;

public final class Triplet<A, B, C> extends Pair<A, B> {

	private C c;

	Triplet(A a, B b, C c)
	{
		super(a, b);

		this.c = c;
	}

	public C getC()
	{
		return this.c;
	}

	public void setC(C c)
	{
		this.c = c;
	}

}
