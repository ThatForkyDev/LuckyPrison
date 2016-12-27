package com.ulfric.data.coll;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.ulfric.data.persist.PlayerData;
import com.ulfric.lib.api.java.Annihilate;
import com.ulfric.lib.api.java.Strings;

public class DataColl {

	protected static IDataColl impl = IDataColl.EMPTY;

	public static IDataColl impl() { return DataColl.impl; }

	public interface IDataColl extends Annihilate
	{
		IDataColl EMPTY = new IDataColl() { };

		default boolean acquire() { return false; }

		default int getCacheSize() { return 0; }

		default void registerPlayer(UUID uuid) { }

		default Map<UUID, Object> getAllData(String path) { return null; }

		default PlayerData getPlayerData(UUID uuid) { return null; }

		default Map<String, Object> getPlayerDataRecursively(UUID uuid, String path) { return null; }

		default <T> T getPlayerData(UUID uuid, String path) { return null; }

		default <T> T getPlayerData(UUID uuid, String path, T defaultValue) { return null; }

		default Number getPlayerDataAsNumber(UUID uuid, String path, Number defaultValue) { return null; }

		default boolean getPlayerDataAsBoolean(UUID uuid, String path) { return false; }

		default byte getPlayerDataAsByte(UUID uuid, String path) { return 0; }

		default short getPlayerDataAsShort(UUID uuid, String path) { return 0; }

		default int getPlayerDataAsInt(UUID uuid, String path) { return 0; }

		default long getPlayerDataAsLong(UUID uuid, String path) { return 0L; }

		default float getPlayerDataAsFloat(UUID uuid, String path) { return 0F; }

		default double getPlayerDataAsDouble(UUID uuid, String path) { return 0D; }

		default String getPlayerDataAsString(UUID uuid, String path) { return Strings.BLANK; }

		default <T> List<T> getPlayerDataAsList(UUID uuid, String path) { return null; }

		default List<String> getPlayerDataAsStringList(UUID uuid, String path) { return null; }

		default void setPlayerData(UUID uuid, String path, Object value) { }

		default void addToPlayerDataCollection(UUID uuid, String path, Object value) { }

		default boolean addToPlayerDataCollectionIfAbsent(UUID uuid, String path, Object value) { return false; }

		default boolean addOrRemovePlayerDataCollection(UUID uuid, String path, Object value) { return false; }

		default void removePlayerData(UUID uuid, String path) { }

		default void setAllData(String path, Object value) { }

		default Set<UUID> uuidSet() { return null; }

		@Override
		default void annihilate() { }
	}

}