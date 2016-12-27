package com.ulfric.lib.api.tuple;

import com.ulfric.lib.api.java.Strings;

public class Pair<A, B> implements Tuple<A> {

	private A a;
	private B b;

	protected Pair(A a, B b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public A getA()
	{
		return this.a;
	}

	@Override
	public void setA(A a)
	{
		this.a = a;
	}

	public B getB()
	{
		return this.b;
	}

	public void setB(B b)
	{
		this.b = b;
	}

	@Override
	public int hashCode()
	{
		int hashcode = 0;

		if (this.a != null)
		{
			hashcode += this.a.hashCode();
		}

		if (this.b != null)
		{
			hashcode += this.b.hashCode();
		}

		return hashcode;
	}

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Pair)) return false;

		Pair<?, ?> other = (Pair<?, ?>) object;

		Object oa = other.a;

		Object ob = other.b;

		Object a = this.a;

		Object b = this.b;

		if ((oa != null && a == null)
			|| (a != null && oa == null)
			|| (ob != null && b == null)
			|| (b != null && ob == null)) { return false; }

		if (a != null)
		{
			if (!a.equals(oa)) return false;
		}

		if (b != null)
		{
			if (!b.equals(ob)) return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return Strings.format("[{0}:{1}]", this.a, this.b);
	}

}
