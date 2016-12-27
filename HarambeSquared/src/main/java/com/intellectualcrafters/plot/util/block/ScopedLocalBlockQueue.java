package com.intellectualcrafters.plot.util.block;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotManager;
import com.intellectualcrafters.plot.object.RunnableVal3;

public class ScopedLocalBlockQueue extends DelegateLocalBlockQueue {
	private final int minX;
	private final int minY;
	private final int minZ;

	private final int maxX;
	private final int maxY;
	private final int maxZ;

	private final int dx;
	private final int dy;
	private final int dz;

	public ScopedLocalBlockQueue(LocalBlockQueue parent, Location min, Location max)
	{
		super(parent);
		this.minX = min.getX();
		this.minY = min.getY();
		this.minZ = min.getZ();

		this.maxX = max.getX();
		this.maxY = max.getY();
		this.maxZ = max.getZ();

		this.dx = this.maxX - this.minX;
		this.dy = this.maxY - this.minY;
		this.dz = this.maxZ - this.minZ;
	}

	@Override
	public boolean setBiome(int x, int z, String biome)
	{
		return x >= 0 && x <= this.dx && z >= 0 && z <= this.dz && super.setBiome(x + this.minX, z + this.minZ, biome);
	}

	public void fillBiome(String biome)
	{
		for (int x = 0; x <= this.dx; x++)
		{
			for (int z = 0; z < this.dz; z++)
			{
				this.setBiome(x, z, biome);
			}
		}
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id, int data)
	{
		return x >= 0 && x <= this.dx && y >= 0 && y <= this.dy && z >= 0 && z <= this.dz && super.setBlock(x + this.minX, y + this.minY, z + this.minZ, id, data);
	}

	public Location getMin()
	{
		return new Location(this.getWorld(), this.minX, this.minY, this.minZ);
	}

	public Location getMax()
	{
		return new Location(this.getWorld(), this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * Run a task for each x,z value corresponding to the plot at that location<br>
	 * - Plot: The plot at the x,z (may be null)<br>
	 * - Location: The location in the chunk (y = 0)<br>
	 * - PlotChunk: Reference to this chunk object<br>
	 *
	 * @param task
	 */
	public void mapByType2D(RunnableVal3<Plot, Integer, Integer> task)
	{
		int bx = this.minX;
		int bz = this.minZ;
		PlotArea area = PS.get().getPlotArea(this.getWorld(), null);
		Location loc = new Location(this.getWorld(), bx, 0, bz);
		if (area != null)
		{
			PlotManager manager = area.getPlotManager();
			for (int x = 0; x < 16; x++)
			{
				loc.setX(bx + x);
				for (int z = 0; z < 16; z++)
				{
					loc.setZ(bz + z);
					task.run(area.getPlotAbs(loc), x, z);
				}
			}
		}
		else
		{
			for (int x = 0; x < 16; x++)
			{
				loc.setX(bx + x);
				for (int z = 0; z < 16; z++)
				{
					loc.setZ(bz + z);
					task.run(loc.getPlotAbs(), x, z);
				}
			}
		}
	}
}
