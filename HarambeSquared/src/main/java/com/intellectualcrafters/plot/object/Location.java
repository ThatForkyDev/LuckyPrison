package com.intellectualcrafters.plot.object;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.util.MathMan;
import org.jetbrains.annotations.NotNull;

public class Location implements Cloneable, Comparable<Location> {

	private int x;
	private int y;
	private int z;
	private float yaw;
	private float pitch;
	private String world;

	public Location(String world, int x, int y, int z, float yaw, float pitch)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public Location()
	{
		this.world = "";
	}

	public Location(String world, int x, int y, int z)
	{
		this(world, x, y, z, 0.0f, 0.0f);
	}

	@Override
	public Location clone()
	{
		return new Location(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
	}

	public int getX()
	{
		return this.x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return this.y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getZ()
	{
		return this.z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	public String getWorld()
	{
		return this.world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public PlotArea getPlotArea()
	{
		return PS.get().getPlotAreaAbs(this);
	}

	public Plot getOwnedPlot()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null ? area.getOwnedPlot(this) : null;
	}

	public Plot getOwnedPlotAbs()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null ? area.getOwnedPlotAbs(this) : null;
	}

	public boolean isPlotArea()
	{
		return PS.get().getPlotAreaAbs(this) != null;
	}

	public boolean isPlotRoad()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null && area.getPlotAbs(this) == null;
	}

	public boolean isUnownedPlotArea()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null && area.getOwnedPlotAbs(this) == null;
	}

	public PlotManager getPlotManager()
	{
		PlotArea pa = this.getPlotArea();
		return pa != null ? pa.getPlotManager() : null;
	}

	public Plot getPlotAbs()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null ? area.getPlotAbs(this) : null;
	}

	public Plot getPlot()
	{
		PlotArea area = PS.get().getPlotAreaAbs(this);
		return area != null ? area.getPlot(this) : null;
	}

	public ChunkLoc getChunkLoc()
	{
		return new ChunkLoc(this.x >> 4, this.z >> 4);
	}

	public float getYaw()
	{
		return this.yaw;
	}

	public void setYaw(float yaw)
	{
		this.yaw = yaw;
	}

	public float getPitch()
	{
		return this.pitch;
	}

	public void setPitch(float pitch)
	{
		this.pitch = pitch;
	}

	public Location add(int x, int y, int z)
	{
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public double getEuclideanDistanceSquared(Location l2)
	{
		double x = this.x - l2.x;
		double y = this.y - l2.y;
		double z = this.z - l2.z;
		return x * x + y * y + z * z;
	}

	public double getEuclideanDistance(Location l2)
	{
		return Math.sqrt(this.getEuclideanDistanceSquared(l2));
	}

	public boolean isInSphere(Location origin, int radius)
	{
		return this.getEuclideanDistanceSquared(origin) < radius * radius;
	}

	@Override
	public int hashCode()
	{
		return MathMan.pair((short) this.x, (short) this.z) * 17 + this.y;
	}

	public boolean isInAABB(Location min, Location max)
	{
		return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.x && this.z < max.z;
	}

	public void lookTowards(int x, int y)
	{
		double l = this.x - x;
		double c = Math.sqrt(l * l + 0.0);
		if (Math.asin(0 / c) / Math.PI * 180 > 90)
		{
			float yaw1 = (float) (180 - -Math.asin(l / c) / Math.PI * 180);
			this.yaw = yaw1;
		}
		else
		{
			float yaw1 = (float) (-Math.asin(l / c) / Math.PI * 180);
			this.yaw = yaw1;
		}
	}

	public Location subtract(int x, int y, int z)
	{
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		if (!(o instanceof Location))
		{
			return false;
		}
		Location l = (Location) o;
		return this.x == l.x && this.y == l.y && this.z == l.z && this.world.equals(l.world) && this.yaw == l.y
			   && this.pitch == l.pitch;
	}

	@Override
	public int compareTo(@NotNull Location o)
	{
		if (this.x == o.x && this.y == o.y || this.z == o.z)
		{
			return 0;
		}
		if (this.x < o.x && this.y < o.y && this.z < o.z)
		{
			return -1;
		}
		return 1;
	}

	@Override
	public String toString()
	{
		return "\"plotsquaredlocation\":{\"x\":" + this.x + ",\"y\":" + this.y + ",\"z\":" + this.z + ",\"yaw\":" + this.yaw + ",\"pitch\":"
			   + this.pitch
			   + ",\"world\":\"" + this.world + "\"}";
	}
}
