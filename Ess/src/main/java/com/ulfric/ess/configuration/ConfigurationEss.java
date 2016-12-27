package com.ulfric.ess.configuration;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import com.ulfric.ess.Ess;
import com.ulfric.lib.api.java.Annihilate;

public class ConfigurationEss implements Annihilate {

	private static final ConfigurationEss INSTANCE = new ConfigurationEss();
	public static ConfigurationEss get() { return ConfigurationEss.INSTANCE; }

	private final FileConfiguration config;
	public FileConfiguration conf() { return this.config; }

	private ConfigurationEss()
	{
		this.config = Ess.get().getConfig();

		this.config.options().copyDefaults(true);

		Ess.get().saveConfig();

		this.populate();
	}

	public void populate()
	{
		for (String string : this.config.getStringList("extraWorlds"))
		{
			Bukkit.createWorld(new WorldCreator(string));
		}
	}

	@Override
	public void annihilate()
	{
		Ess.get().saveConfig();
	}

}