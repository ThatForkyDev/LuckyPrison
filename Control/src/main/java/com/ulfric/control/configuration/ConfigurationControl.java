package com.ulfric.control.configuration;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import com.ulfric.control.Control;

public enum ConfigurationControl {

	INSTANCE;

	private FileConfiguration config;
	ConfigurationControl()
	{
		this.config = Control.get().getConfig();

		this.config.options().copyDefaults(true);

		Control.get().saveConfig();

		this.safeIps = this.config.getStringList("safeIps");
	}

	private final List<String> safeIps;
	public List<String> getSafeIps() { return this.safeIps; }

}