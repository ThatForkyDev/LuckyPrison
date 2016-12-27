package com.ulfric.lib.api.hook;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.ulfric.lib.api.hook.DataHook.IDataHook;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Unique;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DataHook extends Hook<IDataHook> implements Annihilate {

	DataHook()
	{
		super(IDataHook.EMPTY, "Data", "Data hook module", "Packet", "1.0.0-REL");
	}

	public boolean acquire()
	{
		return this.impl.acquire();
	}

	public int getCacheSize()
	{
		return this.impl.getCacheSize();
	}

	public void registerPlayer(UUID uuid)
	{
		this.impl.registerPlayer(uuid);
	}

	public Map<UUID, Object> getAllData(String path)
	{
		return this.impl.getAllData(path);
	}

	public IPlayerData getPlayerData(UUID uuid)
	{
		return this.impl.getPlayerData(uuid);
	}

	public <V> Map<String, V> getPlayerDataRecursively(UUID uuid, String path)
	{
		return this.impl.getPlayerDataRecursively(uuid, path);
	}

	public <T> T getPlayerData(UUID uuid, String path)
	{
		return this.impl.getPlayerData(uuid, path);
	}

	public <T> T getPlayerData(UUID uuid, String path, T defaultValue)
	{
		return this.impl.getPlayerData(uuid, path, defaultValue);
	}

	public Number getPlayerDataAsNumber(UUID uuid, String path, Number defaultValue)
	{
		return this.impl.getPlayerDataAsNumber(uuid, path, defaultValue);
	}

	public boolean getPlayerDataAsBoolean(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsBoolean(uuid, path);
	}

	public byte getPlayerDataAsByte(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsByte(uuid, path);
	}

	public short getPlayerDataAsShort(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsShort(uuid, path);
	}

	public int getPlayerDataAsInt(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsInt(uuid, path);
	}

	public long getPlayerDataAsLong(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsLong(uuid, path);
	}

	public float getPlayerDataAsFloat(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsFloat(uuid, path);
	}

	public double getPlayerDataAsDouble(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsDouble(uuid, path);
	}

	public String getPlayerDataAsString(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsString(uuid, path);
	}

	public <T> List<T> getPlayerDataAsList(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsList(uuid, path);
	}

	public List<String> getPlayerDataAsStringList(UUID uuid, String path)
	{
		return this.impl.getPlayerDataAsStringList(uuid, path);
	}

	public void setPlayerData(UUID uuid, String path, Object value)
	{
		this.impl.setPlayerData(uuid, path, value);
	}

	public void addToPlayerDataCollection(UUID uuid, String path, Object value)
	{
		this.impl.addToPlayerDataCollection(uuid, path, value);
	}

	public boolean addToPlayerDataCollectionIfAbsent(UUID uuid, String path, Object value)
	{
		return this.impl.addToPlayerDataCollectionIfAbsent(uuid, path, value);
	}

	public boolean addOrRemovePlayerDataCollection(UUID uuid, String path, Object value)
	{
		return this.impl.addOrRemovePlayerDataCollection(uuid, path, value);
	}

	public void removePlayerData(UUID uuid, String path)
	{
		this.impl.removePlayerData(uuid, path);
	}

	public void setAllData(String path, Object value)
	{
		this.impl.setAllData(path, value);
	}

	public interface IPlayerData extends Unique {
		boolean isDirty();

		Object get(String path);

		Map<String, Object> getRecur(String path);

		void set(String path, Object value);

		void remove(String path);

		void write();

		@SuppressWarnings("unchecked")
		default <T> T get(String path, T defaultValue)
		{
			Object value = this.get(path);

			if (value == null) return defaultValue;

			if (defaultValue != null && !defaultValue.getClass().isAssignableFrom(value.getClass()))
			{ return defaultValue; }

			return (T) value;
		}

		default boolean getBoolean(String path)
		{
			return this.get(path, false);
		}

		default char getChar(String path)
		{
			return this.get(path, '\0');
		}

		default byte getByte(String path)
		{
			return this.get(path, (byte) 0);
		}

		default short getShort(String path)
		{
			return this.get(path, (short) 0);
		}

		default int getInt(String path)
		{
			return this.get(path, 0);
		}

		default long getLong(String path)
		{
			return this.get(path, 0L);
		}

		default float getFloat(String path)
		{
			return this.get(path, 0.0F);
		}

		default double getDouble(String path)
		{
			return this.get(path, 0.0D);
		}

		default String getString(String path)
		{
			return this.get(path, null);
		}

		default List<String> getStringList(String path)
		{
			return this.get(path, null);
		}
	}

	public interface IDataHook extends HookImpl, Annihilate {
		IDataHook EMPTY = new IDataHook() {
		};

		default boolean acquire()
		{
			return false;
		}

		default int getCacheSize()
		{
			return 0;
		}

		default void registerPlayer(UUID uuid)
		{
		}

		default Map<UUID, Object> getAllData(String path)
		{
			return ImmutableMap.of();
		}

		default IPlayerData getPlayerData(UUID uuid)
		{
			return null;
		}

		default <V> Map<String, V> getPlayerDataRecursively(UUID uuid, String path)
		{
			return null;
		}

		default <T> T getPlayerData(UUID uuid, String path)
		{
			return null;
		}

		default <T> T getPlayerData(UUID uuid, String path, T defaultValue)
		{
			return defaultValue;
		}

		default Number getPlayerDataAsNumber(UUID uuid, String path, Number defaultValue)
		{
			return defaultValue;
		}

		default boolean getPlayerDataAsBoolean(UUID uuid, String path)
		{
			return false;
		}

		default byte getPlayerDataAsByte(UUID uuid, String path)
		{
			return 0;
		}

		default short getPlayerDataAsShort(UUID uuid, String path)
		{
			return 0;
		}

		default int getPlayerDataAsInt(UUID uuid, String path)
		{
			return 0;
		}

		default long getPlayerDataAsLong(UUID uuid, String path)
		{
			return 0;
		}

		default float getPlayerDataAsFloat(UUID uuid, String path)
		{
			return 0;
		}

		default double getPlayerDataAsDouble(UUID uuid, String path)
		{
			return 0;
		}

		default String getPlayerDataAsString(UUID uuid, String path)
		{
			return Strings.BLANK;
		}

		default <T> List<T> getPlayerDataAsList(UUID uuid, String path)
		{
			return ImmutableList.of();
		}

		default List<String> getPlayerDataAsStringList(UUID uuid, String path)
		{
			return ImmutableList.of();
		}

		default void setPlayerData(UUID uuid, String path, Object value)
		{
		}

		default void addToPlayerDataCollection(UUID uuid, String path, Object value)
		{
		}

		default boolean addToPlayerDataCollectionIfAbsent(UUID uuid, String path, Object value)
		{
			return false;
		}

		default boolean addOrRemovePlayerDataCollection(UUID uuid, String path, Object value)
		{
			return false;
		}

		default void removePlayerData(UUID uuid, String path)
		{
		}

		default void setAllData(String path, Object value)
		{
		}

		@Override
		default void annihilate()
		{
		}
	}

	@Override
	public void annihilate()
	{
		this.impl.annihilate();
	}

}
