package com.ulfric.ess.configuration;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.ess.Ess;
import com.ulfric.ess.entity.StatusSign;
import com.ulfric.ess.entity.Warp;
import com.ulfric.lib.api.block.SignUtils;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.persist.Persist;
import com.ulfric.lib.api.time.Timestamp;

public class ConfigurationStore implements Annihilate {

	private static final ConfigurationStore INSTANCE = new ConfigurationStore();
	public static ConfigurationStore get() { return ConfigurationStore.INSTANCE; }

	private File folder;
	private ConfigurationStore()
	{
		this.folder = new File(Ess.get().getDataFolder(), "store");
		this.folder.mkdirs();

		this.populate();
	}


	public void populate()
	{
		this.signs = Sets.newHashSet();
		this.warps = Sets.newHashSet();
		this.folder.mkdirs();
		this.signCommands = Maps.newHashMap();

		for (File file : this.getFilesFromFolder("status_signs"))
		{
			String name = file.getName();

			UUID uuid = UUID.fromString(YamlConfiguration.loadConfiguration(file).getString("uuid"));

			StatusSign s = StatusSign.create(LocationUtils.fromString(name.substring(0, name.length()-4)), uuid, Hooks.PERMISSIONS.user(uuid).getGroupName(), Timestamp.of(Bukkit.getOfflinePlayer(uuid).getLastPlayed()));

			if (s == null) continue;

			this.signs.add(s);
		}

		for (File file : this.getFilesFromFolder("warps"))
		{
			String name = file.getName();

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			this.warps.add(new Warp(name.substring(0, name.length()-4), LocationUtils.proxyFromString(config.getString("location")), config.getInt("warmup")));
		}

		for (File file : this.getFilesFromFolder("command_signs"))
		{
			String name = file.getName();

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			Block block = LocationUtils.fromString(name.substring(0, name.length()-4)).getBlock();

			if (!SignUtils.isSign(block)) continue;

			List<String> commands = config.getStringList("commands");

			if (CollectUtils.isEmpty(commands)) continue;

			this.signCommands.put(SignUtils.asSign(block), commands);
		}
	}

	public Set<File> getFilesFromFolder(String path)
	{
		File folder = new File(this.folder, path);

		if (folder.mkdir()) return Sets.newHashSetWithExpectedSize(0);

		File[] files = folder.listFiles();

		Set<File> set = Sets.newHashSetWithExpectedSize(files.length);

		for (File file : files)
		{
			if (file.getName().startsWith("-")) continue;

			set.add(file);
		}

		return set;
	}

	private Set<Warp> warps;
	public Set<Warp> getWarps() { return this.warps; }
	public Warp getWarp(String name)
	{
		name = name.toLowerCase();
		for (Warp warp : this.getWarps())
		{
			if (!warp.getName().toLowerCase().equals(name)) continue;

			return warp;
		}

		return null;
	}
	public void addWarp(Warp warp)
	{
		this.getWarps().remove(warp);
		this.getWarps().add(warp);
	}
	public boolean removeWarp(String name)
	{
		return this.warps.removeIf(warp -> warp.getName().equalsIgnoreCase(name));
	}

	private Set<StatusSign> signs;
	public Set<StatusSign> getSigns() { return this.signs; }
	public void removeSign(Sign sign)
	{
		this.signCommands.remove(sign);

		Iterator<StatusSign> iter = this.signs.iterator();
		while (iter.hasNext())
		{
			if (!iter.next().equals(sign)) continue;

			iter.remove();

			break;
		}
	}

	private Map<Sign, List<String>> signCommands;
	public List<String> getCommands(Sign sign) { return this.signCommands.get(sign); }
	public void addCommand(Sign sign, String command)
	{
		List<String> commands = this.signCommands.get(sign);

		if (commands == null)
		{
			commands = Lists.newArrayList();

			this.signCommands.put(sign, commands);
		}

		commands.add(command);
	}


	@Override
	public void annihilate()
	{
		File statusSigns = new File(this.folder, "status_signs");
		if (this.finalizeDir(statusSigns))
		{
			for (StatusSign sign : this.getSigns())
			{
				File file = new File(statusSigns, LocationUtils.toString(sign.getSign().getLocation(), true) + ".yml");

				FileConfiguration config = YamlConfiguration.loadConfiguration(file);

				config.set("uuid", sign.getUniqueId().toString());

				Persist.save(config, file);
			}
		}

		File warps = new File(this.folder, "warps");
		if (this.finalizeDir(warps))
		{
			for (Warp warp : this.getWarps())
			{
				File file = new File(warps, warp.getName() + ".yml");

				FileConfiguration config = YamlConfiguration.loadConfiguration(file);

				config.set("location", LocationUtils.toString(warp.getLocation()));

				config.set("warmup", warp.getWarmup());

				Persist.save(config, file);
			}
		}

		File commandSigns = new File(this.folder, "command_signs");

		if (!this.finalizeDir(commandSigns)) return;

		for (Entry<Sign, List<String>> entry : this.signCommands.entrySet())
		{
			File file = new File(commandSigns, LocationUtils.toString(entry.getKey().getLocation(), true) + ".yml");

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			config.set("commands", entry.getValue());

			Persist.save(config, file);
		}
	}

	private boolean finalizeDir(File file)
	{
		if (!file.exists() || !file.isDirectory()) return false;

		for (File child : file.listFiles())
		{
			child.delete();
		}

		return true;
	}

}