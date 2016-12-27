package com.ulfric.lib.api.math;

public final class PositiveInteger {

	private int value;

	public PositiveInteger()
	{
	}

	public PositiveInteger(int value)
	{
		if (value < 0) throw new IllegalArgumentException();

		this.value = value;
	}

	public int intValue()
	{
		return this.value;
	}

	public void increment()
	{
		this.value++;
	}

	public void increment(int amount)
	{
		if (amount < 0) amount = Math.abs(amount);

		this.value += amount;
	}

	public int getThenIncrement()
	{
		int returnValue = this.value;

		this.value++;

		return returnValue;
	}

	public int getIncremented()
	{
		this.value++;

		return this.value;
	}

	public void add(PositiveInteger value)
	{
		this.add(value.intValue());
	}

	public void add(int value)
	{
		this.value += value;
	}

	public boolean canDecrement()
	{
		return this.value > 0;
	}

	public void decrement()
	{
		if (!this.canDecrement()) throw new IllegalStateException();

		this.value--;
	}

	public boolean tryDecrement()
	{
		try
		{
			this.decrement();
		}
		catch (IllegalStateException exception) { return false; }

		return true;
	}

	public int getThenDecrement()
	{
		if (!this.canDecrement()) throw new IllegalStateException();

		int returnValue = this.value;

		this.value--;

		return returnValue;
	}

	public int getDecrement()
	{
		if (!this.canDecrement()) throw new IllegalStateException();

		this.value--;

		return this.value;
	}

	public void subtract(PositiveInteger value)
	{
		this.subtract(value.intValue());
	}

	public void subtract(int value)
	{
		this.set(this.value - value);
	}

	public void set(PositiveInteger value)
	{
		this.set(value.intValue());
	}

	public void set(int value)
	{
		if (value < 0) throw new IllegalArgumentException();

		this.value = value;
	}

	@Override
	public boolean equals(Object object)
	{
		if (object instanceof PositiveInteger)
		{
			return ((PositiveInteger) object).intValue() == this.intValue();
		}

		return object.equals(this.intValue());
	}

	@Override
	public int hashCode()
	{
		return this.value;
	}
}
