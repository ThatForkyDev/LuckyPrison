package com.ulfric.data.hook;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ulfric.data.coll.DataColl;
import com.ulfric.lib.api.hook.DataHook.IDataHook;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;

public enum DataHookImpl implements IDataHook {

	INSTANCE;

	@Override
	public boolean acquire()
	{
		return DataColl.impl().acquire();
	}

	@Override
	public int getCacheSize()
	{
		return DataColl.impl().getCacheSize();
	}

	@Override
	public void registerPlayer(UUID uuid)
	{
		DataColl.impl().registerPlayer(uuid);
	}

	@Override
	public Map<UUID, Object> getAllData(String path)
	{
		return DataColl.impl().getAllData(path);
	}

	@Override
	public IPlayerData getPlayerData(UUID uuid)
	{
		return DataColl.impl().getPlayerData(uuid);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <V> Map<String, V> getPlayerDataRecursively(UUID uuid, String path)
	{
		return (Map<String, V>) DataColl.impl().getPlayerDataRecursively(uuid, path);
	}

	@Override
	public <T> T getPlayerData(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerData(uuid, path);
	}

	@Override
	public <T> T getPlayerData(UUID uuid, String path, T defaultValue)
	{
		return DataColl.impl().getPlayerData(uuid, path, defaultValue);
	}

	@Override
	public Number getPlayerDataAsNumber(UUID uuid, String path, Number defaultValue)
	{
		return DataColl.impl().getPlayerDataAsNumber(uuid, path, defaultValue);
	}

	@Override
	public boolean getPlayerDataAsBoolean(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsBoolean(uuid, path);
	}

	@Override
	public byte getPlayerDataAsByte(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsByte(uuid, path);
	}

	@Override
	public short getPlayerDataAsShort(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsShort(uuid, path);
	}

	@Override
	public int getPlayerDataAsInt(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsInt(uuid, path);
	}

	@Override
	public long getPlayerDataAsLong(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsLong(uuid, path);
	}

	@Override
	public float getPlayerDataAsFloat(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsFloat(uuid, path);
	}

	@Override
	public double getPlayerDataAsDouble(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsDouble(uuid, path);
	}

	@Override
	public String getPlayerDataAsString(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsString(uuid, path);
	}

	@Override
	public <T> List<T> getPlayerDataAsList(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsList(uuid, path);
	}

	@Override
	public List<String> getPlayerDataAsStringList(UUID uuid, String path)
	{
		return DataColl.impl().getPlayerDataAsStringList(uuid, path);
	}

	@Override
	public void setPlayerData(UUID uuid, String path, Object value)
	{
		DataColl.impl().setPlayerData(uuid, path, value);
	}

	@Override
	public void addToPlayerDataCollection(UUID uuid, String path, Object value)
	{
		DataColl.impl().addToPlayerDataCollection(uuid, path, value);
	}

	@Override
	public boolean addToPlayerDataCollectionIfAbsent(UUID uuid, String path, Object value)
	{
		return DataColl.impl().addToPlayerDataCollectionIfAbsent(uuid, path, value);
	}

	@Override
	public boolean addOrRemovePlayerDataCollection(UUID uuid, String path, Object value)
	{
		return DataColl.impl().addOrRemovePlayerDataCollection(uuid, path, value);
	}

	@Override
	public void removePlayerData(UUID uuid, String path)
	{
		DataColl.impl().removePlayerData(uuid, path);
	}

	@Override
	public void setAllData(String path, Object value)
	{
		DataColl.impl().setAllData(path, value);
	}

	@Override
	public void annihilate()
	{
		DataColl.impl().annihilate();
	}

}