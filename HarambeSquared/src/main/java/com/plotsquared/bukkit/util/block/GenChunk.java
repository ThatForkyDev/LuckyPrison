package com.plotsquared.bukkit.util.block;

import com.intellectualcrafters.plot.object.ChunkWrapper;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.block.ScopedLocalBlockQueue;
import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

public class GenChunk extends ScopedLocalBlockQueue {

	public final Biome[] biomes;
	public short[][] result;
	public byte[][] result_data;
	public ChunkGenerator.ChunkData cd;
	public ChunkGenerator.BiomeGrid grid;

	public Chunk chunk;
	public String world;
	public int cx;
	public int cz;

	public GenChunk(Chunk chunk, ChunkWrapper wrap)
	{
		super(null, new Location(null, 0, 0, 0), new Location(null, 15, 255, 15));
		if ((this.chunk = chunk) == null && wrap != null)
		{
			World world = BukkitUtil.getWorld(wrap.world);
			if (world != null)
			{
				this.chunk = world.getChunkAt(wrap.x, wrap.z);
			}
		}
		this.biomes = Biome.values();
	}

	public void setChunk(Chunk chunk)
	{
		this.chunk = chunk;
	}

	public void setChunk(ChunkWrapper wrap)
	{
		this.chunk = null;
		this.world = wrap.world;
		this.cx = wrap.x;
		this.cz = wrap.z;
	}

	public Chunk getChunk()
	{
		if (this.chunk == null)
		{
			World worldObj = BukkitUtil.getWorld(this.world);
			if (worldObj != null)
			{
				this.chunk = worldObj.getChunkAt(this.cx, this.cz);
			}
		}
		return this.chunk;
	}

	public ChunkWrapper getChunkWrapper()
	{
		if (this.chunk == null)
		{
			return new ChunkWrapper(this.world, this.cx, this.cz);
		}
		return new ChunkWrapper(this.chunk.getWorld().getName(), this.chunk.getX(), this.chunk.getZ());
	}

	@Override
	public void fillBiome(String biomeName)
	{
		if (this.grid == null)
		{
			return;
		}
		Biome biome = Biome.valueOf(biomeName.toUpperCase());
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				this.grid.setBiome(x, z, biome);
			}
		}
	}

	@Override
	public boolean setBiome(int x, int z, String biome)
	{
		return this.setBiome(x, z, Biome.valueOf(biome.toUpperCase()));
	}

	public boolean setBiome(int x, int z, int biome)
	{
		if (this.grid != null)
		{
			this.grid.setBiome(x, z, this.biomes[biome]);
			return true;
		}
		return false;
	}

	public boolean setBiome(int x, int z, Biome biome)
	{
		if (this.grid != null)
		{
			this.grid.setBiome(x, z, biome);
			return true;
		}
		return false;
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id, int data)
	{
		if (this.result == null)
		{
			this.cd.setBlock(x, y, z, id, (byte) data);
			return true;
		}
		int i = MainUtil.CACHE_I[y][x][z];
		short[] v = this.result[i];
		if (v == null)
		{
			this.result[i] = v = new short[4096];
		}
		int j = MainUtil.CACHE_J[y][x][z];
		v[j] = (short) id;
		if (data != 0)
		{
			byte[] vd = this.result_data[i];
			if (vd == null)
			{
				this.result_data[i] = vd = new byte[4096];
			}
			vd[j] = (byte) data;
		}
		return true;
	}

	@Override
	public PlotBlock getBlock(int x, int y, int z)
	{
		int i = MainUtil.CACHE_I[y][x][z];
		if (this.result == null)
		{
			MaterialData md = this.cd.getTypeAndData(x, y, z);
			return PlotBlock.get(md.getItemTypeId(), md.getData());
		}
		short[] array = this.result[i];
		if (array == null)
		{
			return PlotBlock.get(0, 0);
		}
		int j = MainUtil.CACHE_J[y][x][z];
		short id = array[j];
		if (id == 0)
		{
			return PlotBlock.get(0, 0);
		}
		byte[] dataArray = this.result_data[i];
		if (dataArray == null)
		{
			return PlotBlock.get(id, 0);
		}
		return PlotBlock.get(id, dataArray[j]);
	}

	public int getX()
	{
		return this.chunk == null ? this.cx : this.chunk.getX();
	}

	public int getZ()
	{
		return this.chunk == null ? this.cz : this.chunk.getZ();
	}

	@Override
	public String getWorld()
	{
		return this.chunk == null ? this.world : this.chunk.getWorld().getName();
	}

	@Override
	public Location getMax()
	{
		return new Location(this.getWorld(), 15 + (this.getX() << 4), 255, 15 + (this.getZ() << 4));
	}

	@Override
	public Location getMin()
	{
		return new Location(this.getWorld(), this.getX() << 4, 0, this.getZ() << 4);
	}

	@Override public GenChunk clone()
	{
		GenChunk toReturn = new GenChunk(this.chunk, new ChunkWrapper(this.getWorld(), this.chunk.getX(), this.chunk.getZ()));
		if (this.result != null)
		{
			for (int i = 0; i < this.result.length; i++)
			{
				short[] matrix = this.result[i];
				if (matrix != null)
				{
					toReturn.result[i] = new short[matrix.length];
					System.arraycopy(matrix, 0, toReturn.result[i], 0, matrix.length);
				}
			}
			for (int i = 0; i < this.result_data.length; i++)
			{
				byte[] matrix = this.result_data[i];
				if (matrix != null)
				{
					toReturn.result_data[i] = new byte[matrix.length];
					System.arraycopy(matrix, 0, toReturn.result_data[i], 0, matrix.length);
				}
			}
		}
		toReturn.cd = this.cd;
		return toReturn;
	}

	public GenChunk shallowClone()
	{
		GenChunk toReturn = new GenChunk(this.chunk, new ChunkWrapper(this.getWorld(), this.chunk.getX(), this.chunk.getZ()));
		toReturn.result = this.result;
		toReturn.result_data = this.result_data;
		toReturn.cd = this.cd;
		return toReturn;
	}
}
