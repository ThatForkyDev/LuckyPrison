package com.intellectualcrafters.plot.util.block;

public interface QueueProvider {

	LocalBlockQueue getNewQueue(String world);

	static QueueProvider of(Class<? extends LocalBlockQueue> primary, Class<? extends LocalBlockQueue> fallback)
	{
		return new QueueProvider() {

			private boolean failed;

			@Override
			public LocalBlockQueue getNewQueue(String world)
			{
				if (!this.failed)
				{
					try
					{
						return (LocalBlockQueue) primary.getConstructors()[0].newInstance(world);
					}
					catch (Throwable e)
					{
						e.printStackTrace();
						this.failed = true;
					}
				}
				try
				{
					return (LocalBlockQueue) fallback.getConstructors()[0].newInstance(world);
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
				return null;
			}
		};
	}
}
