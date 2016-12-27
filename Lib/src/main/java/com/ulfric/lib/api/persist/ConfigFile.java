package com.ulfric.lib.api.persist;

import com.google.common.io.Files;
import com.ulfric.lib.api.java.Named;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unchecked")
public final class ConfigFile implements Named {

	private final boolean dirty;
	private final File file;
	private final FileConfiguration conf;

	ConfigFile(File file)
	{
		this(file, false);
	}

	ConfigFile(File file, boolean dirty)
	{
		this.dirty = dirty;

		this.file = file;

		this.conf = YamlConfiguration.loadConfiguration(file);
	}

	public final boolean isDirty()
	{
		return this.dirty;
	}

	public final File getFile()
	{
		return this.file;
	}

	public final FileConfiguration getConf()
	{
		return this.conf;
	}

	public final <T> T getValue(String path)
	{
		return (T) this.conf.get(path);
	}

	public final <T> T getValue(String path, T defaultValue)
	{
		return (T) Optional.ofNullable(this.getValue(path)).orElse(defaultValue);
	}

	public final List<String> getValueAsStringList(String path)
	{
		return this.getValue(path, Collections.emptyList());
	}

	public Set<String> getKeys(boolean recursive)
	{
		return this.conf.getKeys(recursive);
	}

	public Set<String> getKeys(String path, boolean recursive)
	{
		ConfigurationSection section = this.getSection(path);

		if (section == null) return Collections.emptySet();

		return section.getKeys(recursive);
	}

	public ConfigurationSection getSection(String path)
	{
		return this.conf.getConfigurationSection(path);
	}

	public final void setValue(String path, Object data)
	{
		this.conf.set(path, data);

		if (this.dirty) return;

		this.save();
	}

	@Override
	public final String getName()
	{
		return this.file.getName();
	}

	public final boolean save()
	{
		return Persist.save(this.conf, this.file);
	}

	public boolean makeFile()
	{
		if (this.file.exists()) return false;

		try
		{
			Files.createParentDirs(this.file);

			Persist.save(this.conf, this.file);

			return true;
		}
		catch (IOException exception) { exception.printStackTrace(); }

		return false;
	}

}
