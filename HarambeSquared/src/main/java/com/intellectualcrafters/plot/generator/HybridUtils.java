package com.intellectualcrafters.plot.generator;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.intellectualcrafters.jnbt.CompoundTag;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.flag.FlagManager;
import com.intellectualcrafters.plot.flag.Flags;
import com.intellectualcrafters.plot.object.ChunkLoc;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotManager;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.ChunkManager;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.SchematicHandler;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.intellectualcrafters.plot.util.block.LocalBlockQueue;
import com.intellectualcrafters.plot.util.expiry.PlotAnalysis;

public abstract class HybridUtils {

	public static HybridUtils manager;
	public static Set<ChunkLoc> regions;
	public static Set<ChunkLoc> chunks = new HashSet<>();
	public static PlotArea area;
	public static boolean UPDATE;

	public abstract void analyzeRegion(String world, RegionWrapper region, RunnableVal<PlotAnalysis> whenDone);

	public void analyzePlot(Plot origin, RunnableVal<PlotAnalysis> whenDone)
	{
		ArrayDeque<RegionWrapper> zones = new ArrayDeque<>(origin.getRegions());
		ArrayList<PlotAnalysis> analysis = new ArrayList<>();
		Runnable run = new Runnable() {
			@Override
			public void run()
			{
				if (zones.isEmpty())
				{
					if (!analysis.isEmpty())
					{
						whenDone.value = new PlotAnalysis();
						for (PlotAnalysis data : analysis)
						{
							whenDone.value.air += data.air;
							whenDone.value.air_sd += data.air_sd;
							whenDone.value.changes += data.changes;
							whenDone.value.changes_sd += data.changes_sd;
							whenDone.value.data += data.data;
							whenDone.value.data_sd += data.data_sd;
							whenDone.value.faces += data.faces;
							whenDone.value.faces_sd += data.faces_sd;
							whenDone.value.variety += data.variety;
							whenDone.value.variety_sd += data.variety_sd;
						}
						whenDone.value.air /= analysis.size();
						whenDone.value.air_sd /= analysis.size();
						whenDone.value.changes /= analysis.size();
						whenDone.value.changes_sd /= analysis.size();
						whenDone.value.data /= analysis.size();
						whenDone.value.data_sd /= analysis.size();
						whenDone.value.faces /= analysis.size();
						whenDone.value.faces_sd /= analysis.size();
						whenDone.value.variety /= analysis.size();
						whenDone.value.variety_sd /= analysis.size();
					}
					else
					{
						whenDone.value = analysis.get(0);
					}
					List<Integer> result = new ArrayList<>();
					result.add(whenDone.value.changes);
					result.add(whenDone.value.faces);
					result.add(whenDone.value.data);
					result.add(whenDone.value.air);
					result.add(whenDone.value.variety);

					result.add(whenDone.value.changes_sd);
					result.add(whenDone.value.faces_sd);
					result.add(whenDone.value.data_sd);
					result.add(whenDone.value.air_sd);
					result.add(whenDone.value.variety_sd);
					FlagManager.addPlotFlag(origin, Flags.ANALYSIS, result);
					TaskManager.runTask(whenDone);
					return;
				}
				RegionWrapper region = zones.poll();
				Runnable task = this;
				HybridUtils.this.analyzeRegion(origin.getArea().worldname, region, new RunnableVal<PlotAnalysis>() {
					@Override
					public void run(PlotAnalysis value)
					{
						analysis.add(value);
						TaskManager.runTaskLater(task, 1);
					}
				});
			}
		};
		run.run();
	}

	public int checkModified(LocalBlockQueue queue, int x1, int x2, int y1, int y2, int z1, int z2, PlotBlock[] blocks)
	{
		int count = 0;
		for (int y = y1; y <= y2; y++)
		{
			for (int x = x1; x <= x2; x++)
			{
				for (int z = z1; z <= z2; z++)
				{
					PlotBlock block = queue.getBlock(x, y, z);
					boolean same = false;
					for (PlotBlock p : blocks)
					{
						if (block.id == p.id)
						{
							same = true;
							break;
						}
					}
					if (!same)
					{
						count++;
					}
				}
			}
		}
		return count;
	}

	public final ArrayList<ChunkLoc> getChunks(ChunkLoc region)
	{
		ArrayList<ChunkLoc> chunks = new ArrayList<>();
		int sx = region.x << 5;
		int sz = region.z << 5;
		for (int x = sx; x < sx + 32; x++)
		{
			for (int z = sz; z < sz + 32; z++)
			{
				chunks.add(new ChunkLoc(x, z));
			}
		}
		return chunks;
	}

	/**
	 * Checks all connected plots.
	 *
	 * @param plot
	 * @param whenDone
	 */
	public void checkModified(Plot plot, RunnableVal<Integer> whenDone)
	{
		if (whenDone == null)
		{
			return;
		}
		PlotArea plotArea = plot.getArea();
		if (!(plotArea instanceof ClassicPlotWorld))
		{
			whenDone.value = -1;
			TaskManager.runTask(whenDone);
			return;
		}
		whenDone.value = 0;
		ClassicPlotWorld cpw = (ClassicPlotWorld) plotArea;
		ArrayDeque<RegionWrapper> zones = new ArrayDeque<>(plot.getRegions());
		LocalBlockQueue queue = GlobalBlockQueue.IMP.getNewQueue(cpw.worldname, false);
		Runnable run = new Runnable() {
			@Override
			public void run()
			{
				if (zones.isEmpty())
				{

					TaskManager.runTask(whenDone);
					return;
				}
				RegionWrapper region = zones.poll();
				Location pos1 = new Location(plot.getArea().worldname, region.minX, region.minY, region.minZ);
				Location pos2 = new Location(plot.getArea().worldname, region.maxX, region.maxY, region.maxZ);
				ChunkManager.chunkTask(pos1, pos2, new RunnableVal<int[]>() {
					@Override
					public void run(int[] value)
					{
						ChunkLoc loc = new ChunkLoc(value[0], value[1]);
						ChunkManager.manager.loadChunk(plot.getArea().worldname, loc, false);
						int bx = value[2];
						int bz = value[3];
						int ex = value[4];
						int ez = value[5];
						whenDone.value += HybridUtils.this.checkModified(queue, bx, ex, 1, cpw.PLOT_HEIGHT - 1, bz, ez, cpw.MAIN_BLOCK);
						whenDone.value += HybridUtils.this.checkModified(queue, bx, ex, cpw.PLOT_HEIGHT, cpw.PLOT_HEIGHT, bz, ez, cpw.TOP_BLOCK);
						whenDone.value += HybridUtils.this.checkModified(
								queue, bx, ex, cpw.PLOT_HEIGHT + 1, 255, bz, ez, new PlotBlock[]{PlotBlock.get((short) 0, (byte) 0)});
					}
				}, this, 5);
			}
		};
		run.run();
	}

	public boolean scheduleRoadUpdate(PlotArea area, int extend)
	{
		if (UPDATE)
		{
			return false;
		}
		UPDATE = true;
		Set<ChunkLoc> regions = ChunkManager.manager.getChunkChunks(area.worldname);
		return this.scheduleRoadUpdate(area, regions, extend);
	}

	public boolean scheduleRoadUpdate(PlotArea area, Set<ChunkLoc> rgs, int extend)
	{
		regions = rgs;
		HybridUtils.area = area;
		chunks = new HashSet<>();
		AtomicInteger count = new AtomicInteger(0);
		long baseTime = System.currentTimeMillis();
		AtomicInteger last = new AtomicInteger();
		TaskManager.runTask(new Runnable() {
			@Override
			public void run()
			{
				if (!UPDATE)
				{
					last.set(0);
					Iterator<ChunkLoc> iter = chunks.iterator();
					while (iter.hasNext())
					{
						ChunkLoc chunk = iter.next();
						iter.remove();
						HybridUtils.this.regenerateRoad(area, chunk, extend);
						ChunkManager.manager.unloadChunk(area.worldname, chunk, true, true);
					}
					PS.debug("&cCancelled road task");
					return;
				}
				count.incrementAndGet();
				if (count.intValue() % 20 == 0)
				{
					PS.debug("PROGRESS: " + 100 * (2048 - chunks.size()) / 2048 + '%');
				}
				if (regions.isEmpty() && chunks.isEmpty())
				{
					UPDATE = false;
					PS.debug(C.PREFIX.s() + "Finished road conversion");
					// CANCEL TASK
				}
				else
				{
					Runnable task = this;
					TaskManager.runTaskAsync(() ->
											 {
												 try
												 {
													 if (last.get() == 0)
													 {
														 last.set((int) (System.currentTimeMillis() - baseTime));
													 }
													 if (chunks.size() < 1024)
													 {
														 if (!regions.isEmpty())
														 {
															 Iterator<ChunkLoc> iterator = regions.iterator();
															 ChunkLoc loc = iterator.next();
															 iterator.remove();
															 PS.debug("&3Updating .mcr: " + loc.x + ", " + loc.z + " (aprrox 1024 chunks)");
															 PS.debug(" - Remaining: " + regions.size());
															 chunks.addAll(HybridUtils.this.getChunks(loc));
															 System.gc();
														 }
													 }
													 if (!chunks.isEmpty())
													 {
														 long diff = System.currentTimeMillis() + 1;
														 if (System.currentTimeMillis() - baseTime - last.get() > 2000 && last.get() != 0)
														 {
															 last.set(0);
															 PS.debug(C.PREFIX.s() + "Detected low TPS. Rescheduling in 30s");
															 Iterator<ChunkLoc> iterator = chunks.iterator();
															 ChunkLoc chunk = iterator.next();
															 iterator.remove();
															 TaskManager.runTask(() -> HybridUtils.this.regenerateRoad(area, chunk, extend));
															 // DELAY TASK
															 TaskManager.runTaskLater(task, 600);
															 return;
														 }
														 if (System.currentTimeMillis() - baseTime - last.get() < 1500 && last.get() != 0)
														 {
															 while (System.currentTimeMillis() < diff && !chunks.isEmpty())
															 {
																 Iterator<ChunkLoc> iterator = chunks.iterator();
																 ChunkLoc chunk = iterator.next();
																 iterator.remove();
																 TaskManager.runTask(() -> HybridUtils.this.regenerateRoad(area, chunk, extend));
															 }
														 }
														 last.set((int) (System.currentTimeMillis() - baseTime));
													 }
												 }
												 catch (Exception e)
												 {
													 e.printStackTrace();
													 Iterator<ChunkLoc> iterator = regions.iterator();
													 ChunkLoc loc = iterator.next();
													 iterator.remove();
													 PS.debug("&c[ERROR]&7 Could not update '" + area.worldname + "/region/r." + loc.x + '.' + loc.z
															  + ".mca' (Corrupt chunk?)");
													 int sx = loc.x << 5;
													 int sz = loc.z << 5;
													 for (int x = sx; x < sx + 32; x++)
													 {
														 for (int z = sz; z < sz + 32; z++)
														 {
															 ChunkManager.manager.unloadChunk(area.worldname, new ChunkLoc(x, z), true, true);
														 }
													 }
													 PS.debug("&d - Potentially skipping 1024 chunks");
													 PS.debug("&d - TODO: recommend chunkster if corrupt");
												 }
												 GlobalBlockQueue.IMP.addTask(() -> TaskManager.runTaskLater(task, 20));
											 });
				}
			}
		});
		return true;
	}

	public boolean setupRoadSchematic(Plot plot)
	{
		String world = plot.getArea().worldname;
		LocalBlockQueue queue = GlobalBlockQueue.IMP.getNewQueue(world, false);
		Location bot = plot.getBottomAbs().subtract(1, 0, 1);
		Location top = plot.getTopAbs();
		HybridPlotWorld plotworld = (HybridPlotWorld) plot.getArea();
		int sx = bot.getX() - plotworld.ROAD_WIDTH + 1;
		int sz = bot.getZ() + 1;
		int sy = plotworld.ROAD_HEIGHT;
		int ex = bot.getX();
		int ez = top.getZ();
		int ey = this.get_ey(queue, sx, ex, sz, ez, sy);
		int bz = sz - plotworld.ROAD_WIDTH;
		int tz = sz - 1;
		int ty = this.get_ey(queue, sx, ex, bz, tz, sy);

		Set<RegionWrapper> sideRoad = new HashSet<>(Collections.singletonList(new RegionWrapper(sx, ex, sy, ey, sz, ez)));
		Set<RegionWrapper> intersection = new HashSet<>(Collections.singletonList(new RegionWrapper(sx, ex, sy, ty, bz, tz)));

		String dir = "schematics" + File.separator + "GEN_ROAD_SCHEMATIC" + File.separator + plot
																										   .getArea() + File.separator;
		SchematicHandler.manager.getCompoundTag(world, sideRoad, new RunnableVal<CompoundTag>() {
			@Override
			public void run(CompoundTag value)
			{
				SchematicHandler.manager.save(value, dir + "sideroad.schematic");
				SchematicHandler.manager.getCompoundTag(world, intersection, new RunnableVal<CompoundTag>() {
					@Override
					public void run(CompoundTag value)
					{
						SchematicHandler.manager.save(value, dir + "intersection.schematic");
						plotworld.ROAD_SCHEMATIC_ENABLED = true;
						plotworld.setupSchematics();
					}
				});
			}
		});
		return true;
	}

	public int get_ey(LocalBlockQueue queue, int sx, int ex, int sz, int ez, int sy)
	{
		int ey = sy;
		for (int x = sx; x <= ex; x++)
		{
			for (int z = sz; z <= ez; z++)
			{
				for (int y = sy; y < 256; y++)
				{
					if (y > ey)
					{
						PlotBlock block = queue.getBlock(x, y, z);
						if (block.id != 0)
						{
							ey = y;
						}
					}
				}
			}
		}
		return ey;
	}

	public boolean regenerateRoad(PlotArea area, ChunkLoc chunk, int extend)
	{
		int x = chunk.x << 4;
		int z = chunk.z << 4;
		int ex = x + 15;
		int ez = z + 15;
		HybridPlotWorld plotWorld = (HybridPlotWorld) area;
		extend = Math.min(extend, 255 - plotWorld.ROAD_HEIGHT - plotWorld.SCHEMATIC_HEIGHT);
		if (!plotWorld.ROAD_SCHEMATIC_ENABLED)
		{
			return false;
		}
		boolean toCheck = false;
		if (plotWorld.TYPE == 2)
		{
			boolean c1 = area.contains(x, z);
			boolean c2 = area.contains(ex, ez);
			if (!c1 && !c2)
			{
				return false;
			}
			else
			{
				toCheck = c1 ^ c2;
			}
		}
		PlotManager manager = area.getPlotManager();
		PlotId id1 = manager.getPlotId(plotWorld, x, 0, z);
		PlotId id2 = manager.getPlotId(plotWorld, ex, 0, ez);
		x -= plotWorld.ROAD_OFFSET_X;
		z -= plotWorld.ROAD_OFFSET_Z;
		LocalBlockQueue queue = GlobalBlockQueue.IMP.getNewQueue(plotWorld.worldname, false);
		if (id1 == null || id2 == null || id1 != id2)
		{
			boolean result = ChunkManager.manager.loadChunk(area.worldname, chunk, false);
			if (result)
			{
				if (id1 != null)
				{
					Plot p1 = area.getPlotAbs(id1);
					if (p1 != null && p1.hasOwner() && p1.isMerged())
					{
						toCheck = true;
					}
				}
				if (id2 != null && !toCheck)
				{
					Plot p2 = area.getPlotAbs(id2);
					if (p2 != null && p2.hasOwner() && p2.isMerged())
					{
						toCheck = true;
					}
				}
				int size = plotWorld.SIZE;
				for (int X = 0; X < 16; X++)
				{
					short absX = (short) ((x + X) % size);
					for (int Z = 0; Z < 16; Z++)
					{
						short absZ = (short) ((z + Z) % size);
						if (absX < 0)
						{
							absX += size;
						}
						if (absZ < 0)
						{
							absZ += size;
						}
						boolean condition;
						if (toCheck)
						{
							condition = manager.getPlotId(plotWorld, x + X + plotWorld.ROAD_OFFSET_X, 1, z + Z + plotWorld.ROAD_OFFSET_Z) == null;
							//                            condition = MainUtil.isPlotRoad(new Location(plotworld.worldname, x + X, 1, z + Z));
						}
						else
						{
							boolean gx = absX > plotWorld.PATH_WIDTH_LOWER;
							boolean gz = absZ > plotWorld.PATH_WIDTH_LOWER;
							boolean lx = absX < plotWorld.PATH_WIDTH_UPPER;
							boolean lz = absZ < plotWorld.PATH_WIDTH_UPPER;
							condition = !gx || !gz || !lx || !lz;
						}
						if (condition)
						{
							HashMap<Integer, PlotBlock> blocks = plotWorld.G_SCH.get(MathMan.pair(absX, absZ));
							for (short y = (short) plotWorld.ROAD_HEIGHT; y <= plotWorld.ROAD_HEIGHT + plotWorld.SCHEMATIC_HEIGHT + extend; y++)
							{
								queue.setBlock(x + X + plotWorld.ROAD_OFFSET_X, y, z + Z + plotWorld.ROAD_OFFSET_Z, 0);
							}
							if (blocks != null)
							{
								for (Map.Entry<Integer, PlotBlock> entry : blocks.entrySet())
								{
									queue.setBlock(x + X + plotWorld.ROAD_OFFSET_X, entry.getKey(), z + Z + plotWorld.ROAD_OFFSET_Z, entry.getValue());
								}
							}
						}
					}
				}
				queue.enqueue();
				return true;
			}
		}
		return false;
	}
}
