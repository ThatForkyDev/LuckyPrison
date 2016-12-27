package com.plotsquared.bukkit.util;

import java.util.HashSet;
import java.util.Random;

import com.intellectualcrafters.plot.generator.HybridUtils;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.util.ChunkManager;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.MathMan;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.block.GlobalBlockQueue;
import com.intellectualcrafters.plot.util.block.LocalBlockQueue;
import com.intellectualcrafters.plot.util.expiry.PlotAnalysis;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.Directional;
import org.bukkit.material.MaterialData;

public class BukkitHybridUtils extends HybridUtils {

	@Override
	public void analyzeRegion(String world, RegionWrapper region, RunnableVal<PlotAnalysis> whenDone)
	{
		// int diff, int variety, int vertices, int rotation, int height_sd
		/*
         * diff: compare to base by looping through all blocks
         * variety: add to HashSet for each PlotBlock
         * height_sd: loop over all blocks and get top block
         *
         * vertices: store air map and compare with neighbours
         * for each block check the adjacent
         *  - Store all blocks then go through in second loop
         *  - recheck each block
         *
         */
		TaskManager.runTaskAsync(() ->
								 {
									 LocalBlockQueue queue = GlobalBlockQueue.IMP.getNewQueue(world, false);
									 World worldObj = Bukkit.getWorld(world);
									 ChunkGenerator gen = worldObj.getGenerator();
									 if (gen == null)
									 {
										 return;
									 }
									 ChunkGenerator.BiomeGrid nullBiomeGrid = new ChunkGenerator.BiomeGrid() {
										 @Override
										 public void setBiome(int a, int b, Biome c) {}

										 @Override
										 public Biome getBiome(int a, int b)
										 {
											 return null;
										 }
									 };

									 Location bot = new Location(world, region.minX, region.minY, region.minZ);
									 Location top = new Location(world, region.maxX, region.maxY, region.maxZ);

									 int bx = bot.getX();
									 int bz = bot.getZ();
									 int tx = top.getX();
									 int tz = top.getZ();
									 MainUtil.initCache();

									 System.gc();
									 System.gc();
									 int length = tz - bz + 1;
									 int width = tx - bx + 1;

									 System.gc();
									 MainUtil.initCache();
									 Random r = new Random();
									 short[][][] newBlocks = new short[256][width][length];
									 short[][][] oldBlocks = new short[256][width][length];
									 Runnable run = () -> ChunkManager.chunkTask(bot, top, new RunnableVal<int[]>() {
										 @Override
										 public void run(int[] value)
										 {
											 // [chunkx, chunkz, pos1x, pos1z, pos2x, pos2z, isedge]
											 int X = value[0];
											 int Z = value[1];
											 short[][] result = gen.generateExtBlockSections(worldObj, r, X, Z, nullBiomeGrid);
											 int xb = (X << 4) - bx;
											 int zb = (Z << 4) - bz;
											 for (int i = 0; i < result.length; i++)
											 {
												 if (result[i] == null)
												 {
													 for (int j = 0; j < 4096; j++)
													 {
														 int x = MainUtil.x_loc[i][j] + xb;
														 if (x < 0 || x >= width)
														 {
															 continue;
														 }
														 int z = MainUtil.z_loc[i][j] + zb;
														 if (z < 0 || z >= length)
														 {
															 continue;
														 }
														 int y = MainUtil.y_loc[i][j];
														 oldBlocks[y][x][z] = 0;
													 }
													 continue;
												 }
												 for (int j = 0; j < result[i].length; j++)
												 {
													 int x = MainUtil.x_loc[i][j] + xb;
													 if (x < 0 || x >= width)
													 {
														 continue;
													 }
													 int z = MainUtil.z_loc[i][j] + zb;
													 if (z < 0 || z >= length)
													 {
														 continue;
													 }
													 int y = MainUtil.y_loc[i][j];
													 oldBlocks[y][x][z] = result[i][j];
												 }
											 }
										 }
									 }, () -> TaskManager.runTaskAsync(() ->
															  {
																  int size = width * length;
																  int[] changes = new int[size];
																  int[] faces = new int[size];
																  int[] data = new int[size];
																  int[] air = new int[size];
																  int[] variety = new int[size];
																  int i = 0;
																  for (int x = 0; x < width; x++)
																  {
																	  for (int z = 0; z < length; z++)
																	  {
																		  HashSet<Short> types = new HashSet<>();
																		  for (int y = 0; y < 256; y++)
																		  {
																			  short old = oldBlocks[y][x][z];
																			  short now = newBlocks[y][x][z];
																			  if (old != now)
																			  {
																				  changes[i]++;
																			  }
																			  if (now == 0)
																			  {
																				  air[i]++;
																			  }
																			  else
																			  {
																				  // check vertices
																				  // modifications_adjacent
																				  if (x > 0 && z > 0 && y > 0 && x < width - 1 && z < length - 1 && y < 255)
																				  {
																					  if (newBlocks[y - 1][x][z] == 0)
																					  {
																						  faces[i]++;
																					  }
																					  if (newBlocks[y][x - 1][z] == 0)
																					  {
																						  faces[i]++;
																					  }
																					  if (newBlocks[y][x][z - 1] == 0)
																					  {
																						  faces[i]++;
																					  }
																					  if (newBlocks[y + 1][x][z] == 0)
																					  {
																						  faces[i]++;
																					  }
																					  if (newBlocks[y][x + 1][z] == 0)
																					  {
																						  faces[i]++;
																					  }
																					  if (newBlocks[y][x][z + 1] == 0)
																					  {
																						  faces[i]++;
																					  }
																				  }

																				  Material material = Material.getMaterial(now);
																				  Class<? extends MaterialData> md = material.getData();
																				  if (md.equals(Directional.class))
																				  {
																					  data[i] += 8;
																				  }
																				  else if (!md.equals(MaterialData.class))
																				  {
																					  data[i]++;
																				  }
																				  types.add(now);
																			  }
																		  }
																		  variety[i] = types.size();
																		  i++;
																	  }
																  }
																  // analyze plot
																  // put in analysis obj

																  // run whenDone
																  PlotAnalysis analysis = new PlotAnalysis();
																  analysis.changes = (int) (MathMan.getMean(changes) * 100);
																  analysis.faces = (int) (MathMan.getMean(faces) * 100);
																  analysis.data = (int) (MathMan.getMean(data) * 100);
																  analysis.air = (int) (MathMan.getMean(air) * 100);
																  analysis.variety = (int) (MathMan.getMean(variety) * 100);

																  analysis.changes_sd = (int) MathMan.getSD(changes, analysis.changes);
																  analysis.faces_sd = (int) MathMan.getSD(faces, analysis.faces);
																  analysis.data_sd = (int) MathMan.getSD(data, analysis.data);
																  analysis.air_sd = (int) MathMan.getSD(air, analysis.air);
																  analysis.variety_sd = (int) MathMan.getSD(variety, analysis.variety);
																  System.gc();
																  System.gc();
																  whenDone.value = analysis;
																  whenDone.run();
															  }), 5);
									 int ctz = tz >> 4;
									 int ctx = tx >> 4;
									 int cbz = bz >> 4;
									 int cbx = bx >> 4;
									 ChunkManager.chunkTask(bot, top, new RunnableVal<int[]>() {

										 @Override
										 public void run(int[] value)
										 {
											 int X = value[0];
											 int Z = value[1];
											 worldObj.loadChunk(X, Z);
											 int minX = X == cbx ? bx & 15 : 0;
											 int minZ = Z == cbz ? bz & 15 : 0;
											 int maxX = X == ctx ? tx & 15 : 16;
											 int maxZ = Z == ctz ? tz & 15 : 16;

											 int cbx = X << 4;
											 int cbz = Z << 4;

											 int xb = cbx - bx;
											 int zb = cbz - bz;
											 for (int x = minX; x <= maxX; x++)
											 {
												 int xx = cbx + x;
												 for (int z = minZ; z <= maxZ; z++)
												 {
													 int zz = cbz + z;
													 for (int y = 0; y < 256; y++)
													 {
														 PlotBlock block = queue.getBlock(xx, y, zz);
														 int xr = xb + x;
														 int zr = zb + z;
														 newBlocks[y][xr][zr] = block.id;
													 }
												 }
											 }
											 worldObj.unloadChunkRequest(X, Z, true);
										 }
									 }, () -> TaskManager.runTaskAsync(run), 5);
								 });
	}
}
