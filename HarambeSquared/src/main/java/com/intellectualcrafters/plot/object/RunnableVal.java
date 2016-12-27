package com.intellectualcrafters.plot.object;

public abstract class RunnableVal<T> implements Runnable {
	public T value;

	protected RunnableVal() {}

	protected RunnableVal(T value)
	{
		this.value = value;
	}

	@Override
	public void run()
	{
		this.run(this.value);
	}

	public abstract void run(T value);
}
