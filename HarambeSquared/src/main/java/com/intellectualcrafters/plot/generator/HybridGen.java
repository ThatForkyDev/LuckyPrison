package com.intellectualcrafters.plot.generator;

import java.util.HashMap;
import java.util.Map;

import com.intellectualcrafters.jnbt.CompoundTag;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotManager;
import com.intellectualcrafters.plot.object.PseudoRandom;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.SchematicHandler;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.intellectualcrafters.plot.util.block.LocalBlockQueue;
import com.intellectualcrafters.plot.util.block.ScopedLocalBlockQueue;

public class HybridGen implements IndependentPlotGenerator {

	@Override
	public String getName()
	{
		return "PlotSquared";
	}

	@Override
	public void generateChunk(ScopedLocalBlockQueue result, PlotArea settings, PseudoRandom random)
	{
		HybridPlotWorld hpw = (HybridPlotWorld) settings;
		// Biome
		result.fillBiome(hpw.PLOT_BIOME);
		// Bedrock
		if (hpw.PLOT_BEDROCK)
		{
			for (short x = 0; x < 16; x++)
			{
				for (short z = 0; z < 16; z++)
				{
					result.setBlock(x, 0, z, (short) 7, (byte) 0);
				}
			}
		}
		// Coords
		Location min = result.getMin();
		int cx = min.getX() >> 4;
		int cz = min.getZ() >> 4;
		int bx = min.getX() - hpw.ROAD_OFFSET_X;
		int bz = min.getZ() - hpw.ROAD_OFFSET_Z;
		short rbx = bx < 0 ? (short) (hpw.SIZE + (bx % hpw.SIZE)) : (short) (bx % hpw.SIZE);
		short rbz = bz < 0 ? (short) (hpw.SIZE + (bz % hpw.SIZE)) : (short) (bz % hpw.SIZE);
		short[] rx = new short[16];
		boolean[] gx = new boolean[16];
		boolean[] wx = new boolean[16];
		for (short i = 0; i < 16; i++)
		{
			short v = (short) (rbx + i);
			if (v >= hpw.SIZE)
			{
				v -= hpw.SIZE;
			}
			rx[i] = v;
			if (hpw.ROAD_WIDTH != 0)
			{
				gx[i] = v < hpw.PATH_WIDTH_LOWER || v > hpw.PATH_WIDTH_UPPER;
				wx[i] = v == hpw.PATH_WIDTH_LOWER || v == hpw.PATH_WIDTH_UPPER;
			}
		}
		short[] rz = new short[16];
		boolean[] gz = new boolean[16];
		boolean[] wz = new boolean[16];
		for (short i = 0; i < 16; i++)
		{
			short v = (short) (rbz + i);
			if (v >= hpw.SIZE)
			{
				v -= hpw.SIZE;
			}
			rz[i] = v;
			if (hpw.ROAD_WIDTH != 0)
			{
				gz[i] = v < hpw.PATH_WIDTH_LOWER || v > hpw.PATH_WIDTH_UPPER;
				wz[i] = v == hpw.PATH_WIDTH_LOWER || v == hpw.PATH_WIDTH_UPPER;
			}
		}
		// generation
		HashMap<Integer, HashMap<Integer, PlotBlock>> sch = hpw.G_SCH;
		for (short x = 0; x < 16; x++)
		{
			if (gx[x])
			{
				for (short z = 0; z < 16; z++)
				{
					// Road
					for (int y = 1; y <= hpw.ROAD_HEIGHT; y++)
					{
						result.setBlock(x, y, z, hpw.ROAD_BLOCK);
					}
					if (hpw.ROAD_SCHEMATIC_ENABLED)
					{
						HashMap<Integer, PlotBlock> map = sch.get(MathMan.pair(rx[x], rz[z]));
						if (map != null)
						{
							for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
							{
								result.setBlock(x, entry.getKey(), z, entry.getValue());
							}
						}
					}
				}
			}
			else if (wx[x])
			{
				for (short z = 0; z < 16; z++)
				{
					if (gz[z])
					{
						// road
						for (int y = 1; y <= hpw.ROAD_HEIGHT; y++)
						{
							result.setBlock(x, y, z, hpw.ROAD_BLOCK);
						}
						if (hpw.ROAD_SCHEMATIC_ENABLED)
						{
							HashMap<Integer, PlotBlock> map = sch.get(MathMan.pair(rx[x], rz[z]));
							if (map != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
								{
									result.setBlock(x, entry.getKey(), z, entry.getValue());
								}
							}
						}
					}
					else
					{
						// wall
						for (int y = 1; y <= hpw.WALL_HEIGHT; y++)
						{
							result.setBlock(x, y, z, hpw.WALL_FILLING);
						}
						if (!hpw.ROAD_SCHEMATIC_ENABLED)
						{
							result.setBlock(x, hpw.WALL_HEIGHT + 1, z, hpw.WALL_BLOCK);
						}
						else
						{
							HashMap<Integer, PlotBlock> map = sch.get(MathMan.pair(rx[x], rz[z]));
							if (map != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
								{
									result.setBlock(x, entry.getKey(), z, entry.getValue());
								}
							}
						}
					}
				}
			}
			else
			{
				for (short z = 0; z < 16; z++)
				{
					if (gz[z])
					{
						// road
						for (int y = 1; y <= hpw.ROAD_HEIGHT; y++)
						{
							result.setBlock(x, y, z, hpw.ROAD_BLOCK);
						}
						if (hpw.ROAD_SCHEMATIC_ENABLED)
						{
							HashMap<Integer, PlotBlock> map = sch.get(MathMan.pair(rx[x], rz[z]));
							if (map != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
								{
									result.setBlock(x, entry.getKey(), z, entry.getValue());
								}
							}
						}
					}
					else if (wz[z])
					{
						// wall
						for (int y = 1; y <= hpw.WALL_HEIGHT; y++)
						{
							result.setBlock(x, y, z, hpw.WALL_FILLING);
						}
						if (!hpw.ROAD_SCHEMATIC_ENABLED)
						{
							result.setBlock(x, hpw.WALL_HEIGHT + 1, z, hpw.WALL_BLOCK);
						}
						else
						{
							HashMap<Integer, PlotBlock> map = sch.get(MathMan.pair(rx[x], rz[z]));
							if (map != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
								{
									result.setBlock(x, entry.getKey(), z, entry.getValue());
								}
							}
						}
					}
					else
					{
						// plot
						for (int y = 1; y < hpw.PLOT_HEIGHT; y++)
						{
							result.setBlock(x, y, z, hpw.MAIN_BLOCK[random.random(hpw.MAIN_BLOCK.length)]);
						}
						result.setBlock(x, hpw.PLOT_HEIGHT, z, hpw.TOP_BLOCK[random.random(hpw.TOP_BLOCK.length)]);
						if (hpw.PLOT_SCHEMATIC)
						{
							int pair = MathMan.pair(rx[x], rz[z]);
							HashMap<Integer, PlotBlock> map = sch.get(pair);
							if (map != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : map.entrySet())
								{
									result.setBlock(x, entry.getKey(), z, entry.getValue());
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean populateChunk(ScopedLocalBlockQueue result, PlotArea settings, PseudoRandom random)
	{
		HybridPlotWorld hpw = (HybridPlotWorld) settings;
		if (hpw.G_SCH_STATE != null)
		{
			Location min = result.getMin();
			int cx = min.getX() >> 4;
			int cz = min.getZ() >> 4;
			int p1x = cx << 4;
			int p1z = cz << 4;
			int bx = p1x - hpw.ROAD_OFFSET_X;
			int bz = p1z - hpw.ROAD_OFFSET_Z;
			short rbx = bx < 0 ? (short) (hpw.SIZE + (bx % hpw.SIZE)) : (short) (bx % hpw.SIZE);
			short rbz = bz < 0 ? (short) (hpw.SIZE + (bz % hpw.SIZE)) : (short) (bz % hpw.SIZE);
			short[] rx = new short[16];
			for (short i = 0; i < 16; i++)
			{
				short v = (short) (rbx + i);
				if (v >= hpw.SIZE)
				{
					v -= hpw.SIZE;
				}
				rx[i] = v;
			}
			short[] rz = new short[16];
			for (short i = 0; i < 16; i++)
			{
				short v = (short) (rbz + i);
				if (v >= hpw.SIZE)
				{
					v -= hpw.SIZE;
				}
				rz[i] = v;
			}
			LocalBlockQueue queue = null;
			for (short x = 0; x < 16; x++)
			{
				for (short z = 0; z < 16; z++)
				{
					int pair = MathMan.pair(rx[x], rz[z]);
					HashMap<Integer, CompoundTag> map = hpw.G_SCH_STATE.get(pair);
					if (map != null)
					{
						for (Map.Entry<Integer, CompoundTag> entry : map.entrySet())
						{
							if (queue == null)
							{
								queue = GlobalBlockQueue.IMP.getNewQueue(hpw.worldname, false);
							}
							SchematicHandler.manager.restoreTile(queue, entry.getValue(), p1x + x, entry.getKey(), p1z + z);
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public PlotArea getNewPlotArea(String world, String id, PlotId min, PlotId max)
	{
		return new HybridPlotWorld(world, id, this, min, max);
	}

	@Override
	public PlotManager getNewPlotManager()
	{
		return new HybridPlotManager();
	}

	@Override
	public void initialize(PlotArea area)
	{
		// All initialization is done in the PlotArea class
	}
}
