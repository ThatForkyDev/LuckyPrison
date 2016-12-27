package com.plotsquared.bukkit.uuid;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.object.OfflinePlotPlayer;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.object.StringWrapper;
import com.intellectualcrafters.plot.util.StringMan;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.UUIDHandlerImplementation;
import com.intellectualcrafters.plot.util.expiry.ExpireManager;
import com.intellectualcrafters.plot.uuid.UUIDWrapper;
import com.plotsquared.bukkit.util.NbtFactory;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class FileUUIDHandler extends UUIDHandlerImplementation {

	public FileUUIDHandler(UUIDWrapper wrapper)
	{
		super(wrapper);
	}

	@Override
	public boolean startCaching(Runnable whenDone)
	{
		return super.startCaching(whenDone) && this.cache(whenDone);
	}

	public boolean cache(Runnable whenDone)
	{
		File container = Bukkit.getWorldContainer();
		List<World> worlds = Bukkit.getWorlds();
		String world = worlds.isEmpty() ? "world" : worlds.get(0).getName();
		TaskManager.runTaskAsync(() ->
								 {
									 PS.debug(C.PREFIX + "&6Starting player data caching for: " + world);
									 File uuidFile = new File(PS.get().IMP.getDirectory(), "uuids.txt");
									 if (uuidFile.exists())
									 {
										 try
										 {
											 List<String> lines = Files.readAllLines(uuidFile.toPath(), StandardCharsets.UTF_8);
											 for (String line : lines)
											 {
												 try
												 {
													 line = line.trim();
													 if (line.isEmpty())
													 {
														 continue;
													 }
													 line = line.replaceAll("[\\|][0-9]+[\\|][0-9]+[\\|]", "");
													 String[] split = line.split("\\|");
													 String name = split[0];
													 if (name.isEmpty() || (name.length() > 16) || !StringMan.isAlphanumericUnd(name))
													 {
														 continue;
													 }
													 UUID uuid = this.uuidWrapper.getUUID(name);
													 if (uuid == null)
													 {
														 continue;
													 }
													 UUIDHandler.add(new StringWrapper(name), uuid);
												 }
												 catch (Exception e2)
												 {
													 e2.printStackTrace();
												 }
											 }
										 }
										 catch (IOException e)
										 {
											 e.printStackTrace();
										 }
									 }
									 HashBiMap<StringWrapper, UUID> toAdd = HashBiMap.create(new HashMap<StringWrapper, UUID>());
									 if (Settings.UUID.NATIVE_UUID_PROVIDER)
									 {
										 HashSet<UUID> all = UUIDHandler.getAllUUIDS();
										 PS.debug("&aFast mode UUID caching enabled!");
										 File playerDataFolder = new File(container, world + File.separator + "playerdata");
										 String[] dat = playerDataFolder.list(new DatFileFilter());
										 boolean check = all.isEmpty();
										 if (dat != null)
										 {
											 for (String current : dat)
											 {
												 String s = current.replaceAll(".dat$", "");
												 try
												 {
													 UUID uuid = UUID.fromString(s);
													 if (check || all.remove(uuid))
													 {
														 File file = new File(playerDataFolder, current);
														 ByteSource is = com.google.common.io.Files.asByteSource(file);
														 NbtFactory.NbtCompound compound = NbtFactory.fromStream(is, NbtFactory.StreamOptions.GZIP_COMPRESSION);
														 if (!compound.containsKey("bukkit"))
														 {
															 PS.debug("ERROR: Player data (" + uuid + ".dat) does not contain the the key \"bukkit\"");
														 }
														 else
														 {
															 NbtFactory.NbtCompound bukkit = (NbtFactory.NbtCompound) compound.get("bukkit");
															 String name = (String) bukkit.get("lastKnownName");
															 long last = (long) bukkit.get("lastPlayed");
															 if (ExpireManager.IMP != null)
															 {
																 ExpireManager.IMP.storeDate(uuid, last);
															 }
															 toAdd.put(new StringWrapper(name), uuid);
														 }
													 }
												 }
												 catch (Exception e)
												 {
													 e.printStackTrace();
													 PS.debug(C.PREFIX + "Invalid playerdata: " + current);
												 }
											 }
										 }
										 this.add(toAdd);
										 if (all.isEmpty())
										 {
											 if (whenDone != null)
											 {
												 whenDone.run();
											 }
											 return;
										 }
										 else
										 {
											 PS.debug("Failed to cache: " + all.size() + " uuids - slowly processing all files");
										 }
									 }
									 HashSet<String> worlds1 = Sets.newHashSet(world, "world");
									 HashSet<UUID> uuids = new HashSet<>();
									 HashSet<String> names = new HashSet<>();
									 File playerDataFolder = null;
									 for (String worldName : worlds1)
									 {
										 // Getting UUIDs
										 playerDataFolder = new File(container, worldName + File.separator + "playerdata");
										 String[] dat = playerDataFolder.list(new DatFileFilter());
										 if ((dat != null) && (dat.length != 0))
										 {
											 for (String current : dat)
											 {
												 String s = current.replaceAll(".dat$", "");
												 try
												 {
													 UUID uuid = UUID.fromString(s);
													 uuids.add(uuid);
												 }
												 catch (Exception ignored)
												 {
													 PS.debug(C.PREFIX + "Invalid PlayerData: " + current);
												 }
											 }
											 break;
										 }
										 // Getting names
										 File playersFolder = new File(worldName + File.separator + "players");
										 dat = playersFolder.list(new DatFileFilter());
										 if ((dat != null) && (dat.length != 0))
										 {
											 for (String current : dat)
											 {
												 names.add(current.replaceAll(".dat$", ""));
											 }
											 break;
										 }
									 }
									 for (UUID uuid : uuids)
									 {
										 try
										 {
											 File file = new File(playerDataFolder + File.separator + uuid + ".dat");
											 if (!file.exists())
											 {
												 continue;
											 }
											 ByteSource is = com.google.common.io.Files.asByteSource(file);
											 NbtFactory.NbtCompound compound = NbtFactory.fromStream(is, NbtFactory.StreamOptions.GZIP_COMPRESSION);
											 if (!compound.containsKey("bukkit"))
											 {
												 PS.debug("ERROR: Player data (" + uuid + ".dat) does not contain the the key \"bukkit\"");
											 }
											 else
											 {
												 NbtFactory.NbtCompound bukkit = (NbtFactory.NbtCompound) compound.get("bukkit");
												 String name = (String) bukkit.get("lastKnownName");
												 StringWrapper wrap = new StringWrapper(name);
												 if (!toAdd.containsKey(wrap))
												 {
													 long last = (long) bukkit.get("lastPlayed");
													 if (Settings.UUID.OFFLINE)
													 {
														 if (Settings.UUID.FORCE_LOWERCASE && !name.toLowerCase().equals(name))
														 {
															 uuid = this.uuidWrapper.getUUID(name);
														 }
														 else
														 {
															 long most = (long) compound.get("UUIDMost");
															 long least = (long) compound.get("UUIDLeast");
															 uuid = new UUID(most, least);
														 }
													 }
													 if (ExpireManager.IMP != null)
													 {
														 ExpireManager.IMP.storeDate(uuid, last);
													 }
													 toAdd.put(wrap, uuid);
												 }
											 }
										 }
										 catch (Exception ignored)
										 {
											 PS.debug(C.PREFIX + "&6Invalid PlayerData: " + uuid + ".dat");
										 }
									 }
									 for (String name : names)
									 {
										 UUID uuid = this.uuidWrapper.getUUID(name);
										 StringWrapper nameWrap = new StringWrapper(name);
										 toAdd.put(nameWrap, uuid);
									 }

									 if (this.getUUIDMap().isEmpty())
									 {
										 for (OfflinePlotPlayer op : this.uuidWrapper.getOfflinePlayers())
										 {
											 long last = op.getLastPlayed();
											 if (last != 0)
											 {
												 String name = op.getName();
												 StringWrapper wrap = new StringWrapper(name);
												 if (!toAdd.containsKey(wrap))
												 {
													 UUID uuid = this.uuidWrapper.getUUID(op);
													 toAdd.put(wrap, uuid);
													 if (ExpireManager.IMP != null)
													 {
														 ExpireManager.IMP.storeDate(uuid, last);
													 }
												 }
											 }
										 }
									 }
									 this.add(toAdd);
									 if (whenDone != null)
									 {
										 whenDone.run();
									 }
								 });
		return true;
	}

	@Override
	public void fetchUUID(String name, RunnableVal<UUID> ifFetch)
	{
		TaskManager.runTaskAsync(() ->
								 {
									 ifFetch.value = this.uuidWrapper.getUUID(name);
									 TaskManager.runTask(ifFetch);
								 });
	}
}
