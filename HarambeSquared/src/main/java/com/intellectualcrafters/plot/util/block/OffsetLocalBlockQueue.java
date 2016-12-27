package com.intellectualcrafters.plot.util.block;

public class OffsetLocalBlockQueue extends DelegateLocalBlockQueue {
	private final int ox;
	private final int oy;
	private final int oz;

	public OffsetLocalBlockQueue(LocalBlockQueue parent, int ox, int oy, int oz)
	{
		super(parent);
		this.ox = ox;
		this.oy = oy;
		this.oz = oz;
	}

	@Override
	public boolean setBiome(int x, int y, String biome)
	{
		return super.setBiome(this.ox + x, this.oy + y, biome);
	}

	@Override
	public boolean setBlock(int x, int y, int z, int id, int data)
	{
		return super.setBlock(this.ox + x, this.oy + y, this.oz + z, id, data);
	}
}