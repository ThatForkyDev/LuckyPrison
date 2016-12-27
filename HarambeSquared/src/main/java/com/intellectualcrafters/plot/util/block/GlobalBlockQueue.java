package com.intellectualcrafters.plot.util.block;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.RunnableVal2;
import com.intellectualcrafters.plot.util.TaskManager;

public class GlobalBlockQueue {

	public static GlobalBlockQueue IMP;
	private final int PARALLEL_THREADS;

	private QueueProvider provider;
	private final ConcurrentLinkedDeque<LocalBlockQueue> activeQueues;
	private final ConcurrentLinkedDeque<LocalBlockQueue> inactiveQueues;
	private final ConcurrentLinkedDeque<Runnable> runnables;

	/**
	 * Used to calculate elapsed time in milliseconds and ensure block placement doesn't lag the server
	 */
	private long last;
	private long secondLast;
	private long lastSuccess;
	private final AtomicBoolean running;

	public enum QueueStage {
		INACTIVE, ACTIVE, NONE
	}

	public GlobalBlockQueue(QueueProvider provider, int threads)
	{
		this.provider = provider;
		this.activeQueues = new ConcurrentLinkedDeque<>();
		this.inactiveQueues = new ConcurrentLinkedDeque<>();
		this.runnables = new ConcurrentLinkedDeque<>();
		this.running = new AtomicBoolean();
		this.PARALLEL_THREADS = threads;
	}

	private final RunnableVal2<Long, LocalBlockQueue> SET_TASK = new RunnableVal2<Long, LocalBlockQueue>() {
		@Override
		public void run(Long free, LocalBlockQueue queue)
		{
			do
			{
				boolean more = queue.next();
				if (!more)
				{
					GlobalBlockQueue.this.lastSuccess = GlobalBlockQueue.this.last;
					if (GlobalBlockQueue.this.inactiveQueues.isEmpty() && GlobalBlockQueue.this.activeQueues.isEmpty())
					{
						GlobalBlockQueue.this.tasks();
					}
					return;
				}
			}
			while (((GlobalBlockQueue.this.secondLast = System.currentTimeMillis()) - GlobalBlockQueue.this.last) < free);
		}
	};

	public QueueProvider getProvider()
	{
		return this.provider;
	}

	public void setProvider(QueueProvider provider)
	{
		this.provider = provider;
	}

	public LocalBlockQueue getNewQueue(String world, boolean autoQueue)
	{
		LocalBlockQueue queue = this.provider.getNewQueue(world);
		if (autoQueue)
		{
			this.inactiveQueues.add(queue);
		}
		return queue;
	}

	public boolean stop()
	{
		if (!this.running.get())
		{
			return false;
		}
		this.running.set(false);
		return true;
	}

	public boolean runTask()
	{
		if (this.running.get())
		{
			return false;
		}
		this.running.set(true);
		TaskManager.runTaskRepeat(() ->
								  {
									  if (this.inactiveQueues.isEmpty() && this.activeQueues.isEmpty())
									  {
										  this.lastSuccess = System.currentTimeMillis();
										  this.tasks();
										  return;
									  }
									  this.SET_TASK.value1 = 50 + Math.min((50 + this.last) - (this.last = System.currentTimeMillis()), this.secondLast - System.currentTimeMillis());
									  this.SET_TASK.value2 = this.getNextQueue();
									  if (this.SET_TASK.value2 == null)
									  {
										  return;
									  }
									  if (!PS.get().isMainThread(Thread.currentThread()))
									  {
										  throw new IllegalStateException("This shouldn't be possible for placement to occur off the main thread");
									  }
									  // Disable the async catcher as it can't discern async vs parallel
									  this.SET_TASK.value2.startSet(true);
									  try
									  {
										  if (this.PARALLEL_THREADS <= 1)
										  {
											  this.SET_TASK.run();
										  }
										  else
										  {
											  ArrayList<Thread> threads = new ArrayList<>();
											  for (int i = 0; i < this.PARALLEL_THREADS; i++)
											  {
												  threads.add(new Thread(this.SET_TASK));
											  }
											  for (Thread thread : threads)
											  {
												  thread.start();
											  }
											  for (Thread thread : threads)
											  {
												  try
												  {
													  thread.join();
												  }
												  catch (InterruptedException e)
												  {
													  e.printStackTrace();
												  }
											  }
										  }
									  }
									  catch (Throwable e)
									  {
										  e.printStackTrace();
									  }
									  finally
									  {
										  // Enable it again (note that we are still on the main thread)
										  this.SET_TASK.value2.endSet(true);
									  }
								  }, 1);
		return true;
	}

	public QueueStage getStage(LocalBlockQueue queue)
	{
		if (this.activeQueues.contains(queue))
		{
			return QueueStage.ACTIVE;
		}
		else if (this.inactiveQueues.contains(queue))
		{
			return QueueStage.INACTIVE;
		}
		return QueueStage.NONE;
	}

	public boolean isStage(LocalBlockQueue queue, QueueStage stage)
	{
		switch (stage)
		{
			case ACTIVE:
				return this.activeQueues.contains(queue);
			case INACTIVE:
				return this.inactiveQueues.contains(queue);
			case NONE:
				return !this.activeQueues.contains(queue) && !this.inactiveQueues.contains(queue);
		}
		return false;
	}

	public void enqueue(LocalBlockQueue queue)
	{
		this.inactiveQueues.remove(queue);
		if (queue.size() > 0 && !this.activeQueues.contains(queue))
		{
			queue.optimize();
			this.activeQueues.add(queue);
		}
	}

	public void dequeue(LocalBlockQueue queue)
	{
		this.inactiveQueues.remove(queue);
		this.activeQueues.remove(queue);
	}

	public List<LocalBlockQueue> getAllQueues()
	{
		ArrayList<LocalBlockQueue> list = new ArrayList<>(this.activeQueues.size() + this.inactiveQueues.size());
		list.addAll(this.inactiveQueues);
		list.addAll(this.activeQueues);
		return list;
	}

	public List<LocalBlockQueue> getActiveQueues()
	{
		return new ArrayList<>(this.activeQueues);
	}

	public List<LocalBlockQueue> getInactiveQueues()
	{
		return new ArrayList<>(this.inactiveQueues);
	}

	public void flush(LocalBlockQueue queue)
	{
		this.SET_TASK.value1 = Long.MAX_VALUE;
		this.SET_TASK.value2 = queue;
		if (this.SET_TASK.value2 == null)
		{
			return;
		}
		if (PS.get().isMainThread(Thread.currentThread()))
		{
			throw new IllegalStateException("Must be flushed on the main thread!");
		}
		// Disable the async catcher as it can't discern async vs parallel
		this.SET_TASK.value2.startSet(true);
		try
		{
			if (this.PARALLEL_THREADS <= 1)
			{
				this.SET_TASK.run();
			}
			else
			{
				ArrayList<Thread> threads = new ArrayList<>();
				for (int i = 0; i < this.PARALLEL_THREADS; i++)
				{
					threads.add(new Thread(this.SET_TASK));
				}
				for (Thread thread : threads)
				{
					thread.start();
				}
				for (Thread thread : threads)
				{
					try
					{
						thread.join();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			// Enable it again (note that we are still on the main thread)
			this.SET_TASK.value2.endSet(true);
			this.dequeue(queue);
		}
	}

	public LocalBlockQueue getNextQueue()
	{
		long now = System.currentTimeMillis();
		while (!this.activeQueues.isEmpty())
		{
			LocalBlockQueue queue = this.activeQueues.peek();
			if (queue != null && queue.size() > 0)
			{
				queue.setModified(now);
				return queue;
			}
			else
			{
				this.activeQueues.poll();
			}
		}
		int size = this.inactiveQueues.size();
		if (size > 0)
		{
			Iterator<LocalBlockQueue> iter = this.inactiveQueues.iterator();
			try
			{
				int total = 0;
				LocalBlockQueue firstNonEmpty = null;
				while (iter.hasNext())
				{
					LocalBlockQueue queue = iter.next();
					long age = now - queue.getModified();
					total += queue.size();
					if (queue.size() == 0)
					{
						if (age > 1000)
						{
							iter.remove();
						}
						continue;
					}
					if (firstNonEmpty == null)
					{
						firstNonEmpty = queue;
					}
					if (total > 64)
					{
						firstNonEmpty.setModified(now);
						return firstNonEmpty;
					}
					if (age > 60000)
					{
						queue.setModified(now);
						return queue;
					}
				}
			}
			catch (ConcurrentModificationException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public boolean isDone()
	{
		return this.activeQueues.isEmpty() && this.inactiveQueues.isEmpty();
	}

	public boolean addTask(Runnable whenDone)
	{
		if (this.isDone())
		{
			// Run
			this.tasks();
			if (whenDone != null)
			{
				whenDone.run();
			}
			return true;
		}
		if (whenDone != null)
		{
			this.runnables.add(whenDone);
		}
		return false;
	}

	public synchronized boolean tasks()
	{
		if (this.runnables.isEmpty())
		{
			return false;
		}
		this.runnables.clear();
		ArrayList<Runnable> tmp = new ArrayList<>(this.runnables);
		for (Runnable runnable : tmp)
		{
			runnable.run();
		}
		return true;
	}
}
