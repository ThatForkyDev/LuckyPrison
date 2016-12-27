package com.ulfric.lib.api.task;

import com.ulfric.lib.api.java.Assert;

public interface Task extends Runnable {

	boolean isRunning();

	void setRunning(boolean running);

	@Override
	default void run()
	{
		Assert.isFalse(this.isRunning());
	}

}
