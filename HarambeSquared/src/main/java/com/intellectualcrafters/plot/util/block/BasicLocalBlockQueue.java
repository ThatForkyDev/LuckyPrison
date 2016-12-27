package com.intellectualcrafters.plot.util.block;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.TaskManager;

public abstract class BasicLocalBlockQueue<T> extends LocalBlockQueue {

	private final String world;
	private long modified;

	private final ConcurrentHashMap<Long, LocalChunk> blocks = new ConcurrentHashMap<>();
	private final ConcurrentLinkedDeque<LocalChunk> chunks = new ConcurrentLinkedDeque<>();

	protected BasicLocalBlockQueue(String world)
	{
		super(world);
		this.world = world;
		this.modified = System.currentTimeMillis();
	}

	public abstract LocalChunk getLocalChunk(int x, int z);

	public abstract void setComponents(LocalChunk<T> lc);

	@Override
	public final String getWorld()
	{
		return this.world;
	}

	private LocalChunk lastWrappedChunk;
	private int lastX = Integer.MIN_VALUE;
	private int lastZ = Integer.MIN_VALUE;

	@Override
	public final boolean next()
	{
		this.lastX = Integer.MIN_VALUE;
		this.lastZ = Integer.MIN_VALUE;
		try
		{
			if (this.blocks.isEmpty())
			{
				return false;
			}
			synchronized (this.blocks)
			{
				LocalChunk chunk = this.chunks.poll();
				if (chunk != null)
				{
					this.blocks.remove(chunk.longHash());
					this.execute(chunk);
					return true;
				}
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public final boolean execute(LocalChunk<T> lc)
	{
		if (lc == null)
		{
			return false;
		}
		this.setComponents(lc);
		return true;
	}

	@Override
	public void startSet(boolean parallel)
	{
		// Do nothing
	}

	@Override
	public void endSet(boolean parallel)
	{
		// Do nothing
	}

	@Override
	public final int size()
	{
		return this.chunks.size();
	}

	@Override
	public final void setModified(long modified)
	{
		this.modified = modified;
	}

	@Override
	public final long getModified()
	{
		return this.modified;
	}

	@Override
	public final boolean setBlock(int x, int y, int z, int id, int data)
	{
		if ((y > 255) || (y < 0))
		{
			return false;
		}
		int cx = x >> 4;
		int cz = z >> 4;
		if (cx != this.lastX || cz != this.lastZ)
		{
			this.lastX = cx;
			this.lastZ = cz;
			long pair = (long) cx << 32 | cz & 0xFFFFFFFFL;
			this.lastWrappedChunk = this.blocks.get(pair);
			if (this.lastWrappedChunk == null)
			{
				this.lastWrappedChunk = this.getLocalChunk(x >> 4, z >> 4);
				this.lastWrappedChunk.setBlock(x & 15, y, z & 15, id, data);
				LocalChunk previous = this.blocks.put(pair, this.lastWrappedChunk);
				if (previous == null)
				{
					this.chunks.add(this.lastWrappedChunk);
					return true;
				}
				this.blocks.put(pair, previous);
				this.lastWrappedChunk = previous;
			}
		}
		this.lastWrappedChunk.setBlock(x & 15, y, z & 15, id, data);
		return true;
	}

	@Override
	public final boolean setBiome(int x, int z, String biome)
	{
		long pair = (long) (x >> 4) << 32 | (z >> 4) & 0xFFFFFFFFL;
		LocalChunk result = this.blocks.get(pair);
		if (result == null)
		{
			result = this.getLocalChunk(x >> 4, z >> 4);
			LocalChunk previous = this.blocks.put(pair, result);
			if (previous != null)
			{
				this.blocks.put(pair, previous);
				result = previous;
			}
			else
			{
				this.chunks.add(result);
			}
		}
		result.setBiome(x & 15, z & 15, biome);
		return true;
	}

	public final void setChunk(LocalChunk<T> chunk)
	{
		LocalChunk previous = this.blocks.put(chunk.longHash(), (LocalChunk) chunk);
		if (previous != null)
		{
			this.chunks.remove(previous);
		}
		this.chunks.add((LocalChunk) chunk);
	}

	public abstract static class LocalChunk<T> {
		public final BasicLocalBlockQueue parent;
		public final int z;
		public final int x;

		public T[] blocks;
		public String[][] biomes;

		protected LocalChunk(BasicLocalBlockQueue<T> parent, int x, int z)
		{
			this.parent = parent;
			this.x = x;
			this.z = z;
		}

		/**
		 * Get the parent queue this chunk belongs to
		 *
		 * @return
		 */
		public BasicLocalBlockQueue getParent()
		{
			return this.parent;
		}

		public int getX()
		{
			return this.x;
		}

		public int getZ()
		{
			return this.z;
		}

		/**
		 * Add the chunk to the queue
		 */
		public void addToQueue()
		{
			this.parent.setChunk(this);
		}

		public void fill(int id, int data)
		{
			this.fillCuboid(0, 15, 0, 255, 0, 15, id, data);
		}

		/**
		 * Fill a cuboid in this chunk with a block
		 *
		 * @param x1
		 * @param x2
		 * @param y1
		 * @param y2
		 * @param z1
		 * @param z2
		 * @param id
		 * @param data
		 */
		public void fillCuboid(int x1, int x2, int y1, int y2, int z1, int z2, int id, int data)
		{
			for (int x = x1; x <= x2; x++)
			{
				for (int y = y1; y <= y2; y++)
				{
					for (int z = z1; z <= z2; z++)
					{
						this.setBlock(x, y, z, id, data);
					}
				}
			}
		}

		public abstract void setBlock(int x, int y, int z, int id, int data);

		public void setBiome(int x, int z, String biome)
		{
			if (this.biomes == null)
			{
				this.biomes = new String[16][];
			}
			String[] index = this.biomes[x];
			if (index == null)
			{
				index = this.biomes[x] = new String[16];
			}
			index[z] = biome;
		}

		public long longHash()
		{
			return MathMan.pairInt(this.x, this.z);
		}

		@Override
		public int hashCode()
		{
			return MathMan.pair((short) this.x, (short) this.z);
		}
	}

	public class BasicLocalChunk extends LocalChunk<PlotBlock[]> {
		public BasicLocalChunk(BasicLocalBlockQueue parent, int x, int z)
		{
			super(parent, x, z);
			this.blocks = new PlotBlock[16][];
		}

		@Override public void setBlock(int x, int y, int z, int id, int data)
		{
			PlotBlock block = PlotBlock.get(id, data);
			int i = MainUtil.CACHE_I[y][x][z];
			int j = MainUtil.CACHE_J[y][x][z];
			PlotBlock[] array = this.blocks[i];
			if (array == null)
			{
				array = this.blocks[i] = new PlotBlock[4096];
			}
			array[j] = block;
		}
	}

	public class CharLocalChunk extends LocalChunk<char[]> {

		public CharLocalChunk(BasicLocalBlockQueue parent, int x, int z)
		{
			super(parent, x, z);
			this.blocks = new char[16][];
		}

		@Override
		public void setBlock(int x, int y, int z, int id, int data)
		{
			PlotBlock block = PlotBlock.get(id, data);
			int i = MainUtil.CACHE_I[y][x][z];
			int j = MainUtil.CACHE_J[y][x][z];
			char[] array = this.blocks[i];
			if (array == null)
			{
				this.blocks[i] = new char[4096];
				array = this.blocks[i];
			}
			array[j] = (char) ((block.id << 4) + block.data);
		}
	}

	@Override
	public void flush()
	{
		GlobalBlockQueue.IMP.dequeue(this);
		TaskManager.IMP.sync(new RunnableVal<Object>() {
			@Override
			public void run(Object value)
			{
				while (BasicLocalBlockQueue.this.next()) ;
			}
		});
	}
}
