package com.ulfric.lib.api.world;

import org.bukkit.Chunk;
import org.bukkit.World;

public final class ChunkLocation {

	private final World world;
	private final int x;
	private final int y;
	private Chunk chunk;

	public ChunkLocation(Chunk chunk)
	{
		this(chunk.getWorld(), chunk.getX(), chunk.getZ());

		this.chunk = chunk;
	}

	public ChunkLocation(World world, int x, int y)
	{
		this.world = world;

		this.x = x;

		this.y = y;
	}

	public World getWorld()
	{
		return this.world;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public boolean isLoaded()
	{
		if (this.chunk != null)
		{
			return this.chunk.isLoaded();
		}

		return this.world.isChunkLoaded(this.x, this.y);
	}

	public Chunk asChunk()
	{
		if (this.chunk == null)
		{
			this.chunk = this.world.getChunkAt(this.x, this.y);
		}

		return this.chunk;
	}

	public boolean load(boolean flag)
	{
		return this.asChunk().load(flag);
	}

	public void unload(boolean flag1, boolean flag2)
	{
		if (!this.isLoaded()) return;

		this.asChunk().unload(flag1, flag2);
	}

}
