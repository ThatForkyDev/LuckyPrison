package com.plotsquared.bukkit.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.ChunkLoc;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.ReflectionUtils;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.plotsquared.bukkit.object.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static com.intellectualcrafters.plot.util.ReflectionUtils.getRefClass;

/**
 * An utility that can be used to send chunks, rather than using bukkit code
 * to do so (uses heavy NMS).
 */
public class SendChunk {

	private final ReflectionUtils.RefMethod methodGetHandlePlayer;
	private final ReflectionUtils.RefMethod methodGetHandleChunk;
	private final ReflectionUtils.RefConstructor mapChunk;
	private final ReflectionUtils.RefField connection;
	private final ReflectionUtils.RefMethod send;
	private final ReflectionUtils.RefMethod methodInitLighting;

	/**
	 * Constructor.
	 */
	public SendChunk() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException
	{
		ReflectionUtils.RefClass classCraftPlayer = getRefClass("{cb}.entity.CraftPlayer");
		this.methodGetHandlePlayer = classCraftPlayer.getMethod("getHandle");
		ReflectionUtils.RefClass classCraftChunk = getRefClass("{cb}.CraftChunk");
		this.methodGetHandleChunk = classCraftChunk.getMethod("getHandle");
		ReflectionUtils.RefClass classChunk = getRefClass("{nms}.Chunk");
		this.methodInitLighting = classChunk.getMethod("initLighting");
		ReflectionUtils.RefClass classMapChunk = getRefClass("{nms}.PacketPlayOutMapChunk");
		ReflectionUtils.RefConstructor tempMapChunk;
		if (PS.get().checkVersion(PS.get().IMP.getServerVersion(), 1, 9, 4))
		{
			//this works for 1.9.4 and 1.10
			tempMapChunk = classMapChunk.getConstructor(classChunk.getRealClass(), int.class);
		}
		else
		{
			try
			{
				tempMapChunk = classMapChunk.getConstructor(classChunk.getRealClass(), boolean.class, int.class);
			}
			catch (NoSuchMethodException ignored)
			{
				tempMapChunk = classMapChunk.getConstructor(classChunk.getRealClass(), boolean.class, int.class, int.class);
			}
		}
		this.mapChunk = tempMapChunk;
		ReflectionUtils.RefClass classEntityPlayer = getRefClass("{nms}.EntityPlayer");
		this.connection = classEntityPlayer.getField("playerConnection");
		ReflectionUtils.RefClass classPacket = getRefClass("{nms}.Packet");
		ReflectionUtils.RefClass classConnection = getRefClass("{nms}.PlayerConnection");
		this.send = classConnection.getMethod("sendPacket", classPacket.getRealClass());
	}

	public void sendChunk(Collection<Chunk> input)
	{
		HashSet<Chunk> chunks = new HashSet<>(input);
		HashMap<String, ArrayList<Chunk>> map = new HashMap<>();
		int view = Bukkit.getServer().getViewDistance();
		for (Chunk chunk : chunks)
		{
			String world = chunk.getWorld().getName();
			ArrayList<Chunk> list = map.get(world);
			if (list == null)
			{
				list = new ArrayList<>();
				map.put(world, list);
			}
			list.add(chunk);
			Object c = this.methodGetHandleChunk.of(chunk).call();
			this.methodInitLighting.of(c).call();
		}
		for (Map.Entry<String, PlotPlayer> entry : UUIDHandler.getPlayers().entrySet())
		{
			PlotPlayer pp = entry.getValue();
			Plot plot = pp.getCurrentPlot();
			Location location = null;
			String world;
			if (plot != null)
			{
				world = plot.getArea().worldname;
			}
			else
			{
				location = pp.getLocation();
				world = location.getWorld();
			}
			ArrayList<Chunk> list = map.get(world);
			if (list == null)
			{
				continue;
			}
			if (location == null)
			{
				location = pp.getLocation();
			}
			int cx = location.getX() >> 4;
			int cz = location.getZ() >> 4;
			Player player = ((BukkitPlayer) pp).player;
			Object entity = this.methodGetHandlePlayer.of(player).call();

			for (Chunk chunk : list)
			{
				int dx = Math.abs(cx - chunk.getX());
				int dz = Math.abs(cz - chunk.getZ());
				if ((dx > view) || (dz > view))
				{
					continue;
				}
				Object c = this.methodGetHandleChunk.of(chunk).call();
				chunks.remove(chunk);
				Object con = this.connection.of(entity).get();
				Object packet = null;
				if (PS.get().checkVersion(PS.get().IMP.getServerVersion(), 1, 9, 4))
				{
					try
					{
						packet = this.mapChunk.create(c, 65535);
					}
					catch (Exception ignored) {}
				}
				else
				{
					try
					{
						packet = this.mapChunk.create(c, true, 65535);
					}
					catch (ReflectiveOperationException | IllegalArgumentException e)
					{
						try
						{
							packet = this.mapChunk.create(c, true, 65535, 5);
						}
						catch (ReflectiveOperationException | IllegalArgumentException e1)
						{
							e1.printStackTrace();
						}
					}
				}
				if (packet == null)
				{
					PS.debug("Error with PacketPlayOutMapChunk reflection.");
				}
				this.send.of(con).call(packet);
			}
		}
		for (Chunk chunk : chunks)
		{
			TaskManager.runTask(() ->
								{
									try
									{
										chunk.unload(true, false);
									}
									catch (Throwable ignored)
									{
										String worldName = chunk.getWorld().getName();
										PS.debug("$4Could not save chunk: " + worldName + ';' + chunk.getX() + ';' + chunk.getZ());
										PS.debug("$3 - $4File may be open in another process (e.g. MCEdit)");
										PS.debug("$3 - $4" + worldName + "/level.dat or " + worldName
												 + "/level_old.dat may be corrupt (try repairing or removing these)");
									}
								});
		}
	}

	public void sendChunk(String worldName, Collection<ChunkLoc> chunkLocations)
	{
		World myWorld = Bukkit.getWorld(worldName);
		ArrayList<Chunk> chunks = new ArrayList<>();
		for (ChunkLoc loc : chunkLocations)
		{
			if (myWorld.isChunkLoaded(loc.x, loc.z))
			{
				chunks.add(myWorld.getChunkAt(loc.x, loc.z));
			}
		}
		this.sendChunk(chunks);
	}
}
