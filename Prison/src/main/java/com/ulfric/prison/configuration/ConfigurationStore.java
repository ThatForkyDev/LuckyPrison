package com.ulfric.prison.configuration;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;
import com.ulfric.prison.Prison;

public enum ConfigurationStore {

	INSTANCE;

	public final void poke() { }

	private File folder;
	private ConfigurationStore()
	{
		this.folder = new File(Prison.get().getDataFolder(), "store");

		this.folder.mkdirs();

		this.populate();
	}

	public void populate()
	{
		this.permissibleBlocks = Maps.newHashMap();

		for (File file : this.getFilesFromFolder("permissibles"))
		{
			String name = file.getName();

			Block block = LocationUtils.fromString(name.substring(0, name.length()-4)).getBlock();

			FileConfiguration config = YamlConfiguration.loadConfiguration(file);

			this.permissibleBlocks.put(block, Tuples.newPair(config.getString("node"), Chat.color(config.getString("message"))));
		}
	}

	public Set<File> getFilesFromFolder(String path)
	{
		File folder = new File(this.folder, path);
		folder.mkdir();

		Set<File> set = Sets.newHashSet();

		for (File file : folder.listFiles())
		{
			if (file.getName().startsWith("-")) continue;

			set.add(file);
		}

		return set;
	}

	private Map<Block, Pair<String, String>> permissibleBlocks;
	public Pair<String, String> getPermissible(Block block) { return this.permissibleBlocks.get(block); }

}