package com.ulfric.lib;

import org.bukkit.configuration.file.FileConfiguration;

public enum LibConfiguration {

	INSTANCE;

	private final FileConfiguration conf;

	LibConfiguration()
	{
		this.conf = Lib.get().getConfig().options().copyDefaults(true).configuration();
		Lib.get().saveConfig();
	}

	public FileConfiguration getConf()
	{
		return this.conf;
	}
}
