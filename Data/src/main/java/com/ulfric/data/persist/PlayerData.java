package com.ulfric.data.persist;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;
import com.ulfric.lib.api.persist.Persist;

public class PlayerData implements IPlayerData {

	public PlayerData(File file, FileConfiguration conf, UUID uuid, Map<String, Object> data)
	{
		this.file = file;

		this.conf = conf;

		this.uuid = uuid;

		this.data = data;
	}

	private final File file;

	private final FileConfiguration conf;
	public FileConfiguration getConf() { return this.conf; }

	private final UUID uuid;
	@Override
	public UUID getUniqueId() { return this.uuid; }

	private boolean flag = true;
	@Override
	public boolean isDirty() { return this.flag; }

	private final Map<String, Object> data;

	@Override
	public Object get(String path)
	{
		return this.data.get(path);
	}

	@Override
	public Map<String, Object> getRecur(String path)
	{
		Map<String, Object> objects = Maps.newHashMap();

		for (Entry<String, Object> value : this.data.entrySet())
		{
			if (!value.getKey().startsWith(path)) continue;

			objects.put(value.getKey(), value.getValue());
		}

		return objects;
	}

	@Override
	public void set(String path, Object value)
	{
		this.flag = false;

		this.data.put(path, value);
	}

	@Override
	public void remove(String path)
	{
		this.set(path, null);
	}

	@Override
	public void write()
	{
		if (this.isDirty()) return;

		FileConfiguration conf = this.conf;

		for (Entry<String, Object> entry : this.data.entrySet())
		{
			conf.set(entry.getKey(), entry.getValue());
		}

		Persist.save(conf, this.file);
	}

}