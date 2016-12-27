package com.intellectualcrafters.plot.object;

public abstract class RunnableVal3<T, U, V> implements Runnable {
	public T value1;
	public U value2;
	public V value3;

	protected RunnableVal3() {}

	protected RunnableVal3(T value1, U value2, V value3)
	{
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}

	@Override
	public void run()
	{
		this.run(this.value1, this.value2, this.value3);
	}

	public abstract void run(T value1, U value2, V value3);
}
