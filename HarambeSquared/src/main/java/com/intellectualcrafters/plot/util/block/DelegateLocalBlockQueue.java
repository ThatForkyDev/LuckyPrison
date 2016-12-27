package com.intellectualcrafters.plot.util.block;

import com.intellectualcrafters.plot.object.PlotBlock;

public class DelegateLocalBlockQueue extends LocalBlockQueue {

	private final LocalBlockQueue parent;

	public LocalBlockQueue getParent()
	{
		return this.parent;
	}

	@Override
	public boolean next()
	{
		return this.parent.next();
	}

	@Override
	public void startSet(boolean parallel)
	{
		if (this.parent != null)
		{
			this.parent.startSet(parallel);
		}
	}

	@Override
	public void endSet(boolean parallel)
	{
		if (this.parent != null)
		{
			this.parent.endSet(parallel);
		}
	}

	@Override
	public int size()
	{
		if (this.parent != null)
		{
			return this.parent.size();
		}
		return 0;
	}

	@Override
	public void optimize()
	{
		if (this.parent != null)
		{
			this.parent.optimize();
		}
	}

	@Override
	public void setModified(long modified)
	{
		if (this.parent != null)
		{
			this.parent.setModified(modified);
		}
	}

	@Override
	public long getModified()
	{
		if (this.parent != null)
		{
			return this.parent.getModified();
		}
		return 0;
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id, int data)
	{
		return this.parent.setBlock(x, y, z, id, data);
	}

	@Override
	public PlotBlock getBlock(int x, int y, int z)
	{
		return this.parent.getBlock(x, y, z);
	}

	@Override
	public boolean setBiome(int x, int y, String biome)
	{
		return this.parent.setBiome(x, y, biome);
	}

	@Override
	public String getWorld()
	{
		return this.parent.getWorld();
	}

	@Override
	public void flush()
	{
		if (this.parent != null)
		{
			this.parent.flush();
		}
	}

	@Override
	public void refreshChunk(int x, int z)
	{
		if (this.parent != null)
		{
			this.parent.refreshChunk(x, z);
		}
	}

	@Override
	public void fixChunkLighting(int x, int z)
	{
		if (this.parent != null)
		{
			this.parent.fixChunkLighting(x, z);
		}
	}

	@Override
	public void regenChunk(int x, int z)
	{
		if (this.parent != null)
		{
			this.parent.regenChunk(x, z);
		}
	}

	@Override
	public void enqueue()
	{
		if (this.parent != null)
		{
			this.parent.enqueue();
		}
	}

	public DelegateLocalBlockQueue(LocalBlockQueue parent)
	{
		super(parent == null ? null : parent.getWorld());
		this.parent = parent;
	}
}
