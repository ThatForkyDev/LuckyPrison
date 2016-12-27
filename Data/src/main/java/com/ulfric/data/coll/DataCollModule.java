package com.ulfric.data.coll;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ulfric.data.Data;
import com.ulfric.data.coll.DataColl.IDataColl;
import com.ulfric.data.lang.Meta;
import com.ulfric.data.persist.PlayerData;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;

@SuppressWarnings("unchecked")
public class DataCollModule extends SimpleModule {

	public DataCollModule()
	{
		super("datacoll", "Data caching, reading, and writing", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler(priority = EventPriority.LOWEST)
			public void onJoin(PlayerLoginEvent event)
			{
				Player player = event.getPlayer();

				UUID uuid = player.getUniqueId();

				IDataColl impl = DataColl.impl();

				if (!player.hasPlayedBefore())
				{
					impl.registerPlayer(uuid);

					impl.setPlayerData(uuid, Meta.JOIN_NUMBER, impl.getCacheSize());

					impl.setPlayerData(uuid, Meta.JOIN_DATE, TimeUtils.formatCurrentDay());
				}

				impl.setPlayerData(uuid, Meta.NAME, player.getName());

				impl.setPlayerData(uuid, Meta.JOIN_LAST, TimeUtils.formatCurrentDateFully());

				Metadata.apply(player, Meta.PLAY_TIME, Timestamp.now());
			}

			@EventHandler
			public void onLeave(PlayerQuitEvent event)
			{
				Player player = event.getPlayer();

				UUID uuid = player.getUniqueId();

				IDataColl impl = DataColl.impl();

				impl.setPlayerData(uuid, Meta.PLAY_TIME, impl.getPlayerDataAsLong(uuid, Meta.PLAY_TIME) + Metadata.getValueAsTimestamp(player, Meta.PLAY_TIME).timeSince());

				Metadata.remove(player, Meta.PLAY_TIME);

				impl.setPlayerData(uuid, Meta.JOIN_LAST, TimeUtils.formatCurrentDateFully());

				impl.setPlayerData(uuid, Meta.LAST_LOCATION, LocationUtils.toString(player.getLocation(), true));
			}
		});
	}

	@Override
	public void postEnable()
	{
		DataColl.impl = new IDataColl()
		{
			private Map<UUID, PlayerData> cache;

			private final Lock lock = new ReentrantLock();

			private File folder;

			{
				this.init();
			}
			private final void init()
			{
				if (!this.acquire())
				{
					DataCollModule.this.disable();

					return;
				}

				if ((this.folder = new File(Data.get().getDataFolder(), "store")).mkdirs())
				{
					this.cache = Maps.newConcurrentMap();

					this.lock.unlock();

					return;
				}

				File[] files = this.folder.listFiles();

				this.cache = new ConcurrentHashMap<>(files.length);

				if (files.length == 0)
				{
					this.lock.unlock();

					return;
				}

				long start = System.currentTimeMillis();
				Arrays.stream(files).parallel().forEach(file -> 
				{
					PlayerData data = this.registerPlayer(file);

					this.cache.put(data.getUniqueId(), data);
				});
				/*for (File file : files)
				{
					PlayerData data = this.registerPlayer(file);

					this.cache.put(data.getUniqueId(), data);
				}*/

				long took = System.currentTimeMillis()-start;

				DataCollModule.this.log("Loaded data for {0} users in {1} ({2}ms)", files.length, TimeUtils.millisecondsToString(took), took);

				this.lock.unlock();
			}

			@Override
			public boolean acquire()
			{
				if (!this.lock.tryLock())
				{
					DataCollModule.this.log("DataColl acquire failed!");

					return false;
				}

				return true;
			}

			@Override
			public int getCacheSize()
			{
				return this.cache.size();
			}

			@Override
			public Map<UUID, Object> getAllData(String path)
			{
				path = path.toLowerCase();

				Map<UUID, Object> map = Maps.newHashMap();

				for (Entry<UUID, PlayerData> data : this.cache.entrySet())
				{
					Object value = data.getValue().get(path);

					if (value == null) continue;

					map.put(data.getKey(), value);
				}

				return map;
			}

			@Override
			public void registerPlayer(UUID uuid)
			{
				this.cache.putIfAbsent(uuid, this.registerPlayer(new File(this.folder, uuid + ".yml")));
			}
			private PlayerData registerPlayer(File file)
			{
				FileConfiguration userFile = YamlConfiguration.loadConfiguration(file);

				Map<String, Object> data = Maps.newHashMap();

				for (String path : userFile.getKeys(true))
				{
					Object value = userFile.get(path);

					if (value.getClass() == MemorySection.class) continue;

					data.put(path, value);
				}

				String name = file.getName();

				return new PlayerData(file, userFile, UUID.fromString(name.substring(0, name.length()-4)), data);
			}

			@Override
			public PlayerData getPlayerData(UUID uuid)
			{
				return this.cache.get(uuid);
			}

			@Override
			public Map<String, Object> getPlayerDataRecursively(UUID uuid, String path)
			{
				return this.getPlayerData(uuid).getRecur(path.toLowerCase());
			}

			@Override
			public <T> T getPlayerData(UUID uuid, String path)
			{
				PlayerData data = this.getPlayerData(uuid);

				if (data == null) return null;

				return (T) data.get(path.toLowerCase());
			}

			@Override
			public <T> T getPlayerData(UUID uuid, String path, T defaultValue)
			{
				return (T) Optional.ofNullable(this.getPlayerData(uuid, path)).orElse(defaultValue);
			}

			@Override
			public Number getPlayerDataAsNumber(UUID uuid, String path, Number defaultValue)
			{
				return this.getPlayerData(uuid, path, defaultValue);
			}
			@Override
			public boolean getPlayerDataAsBoolean(UUID uuid, String path)
			{
				return this.getPlayerData(uuid, path, false);
			}
			@Override
			public byte getPlayerDataAsByte(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, (byte) 0).byteValue();
			}
			@Override
			public short getPlayerDataAsShort(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, (short) 0).shortValue();
			}
			@Override
			public int getPlayerDataAsInt(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, 0).intValue();
			}
			@Override
			public long getPlayerDataAsLong(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, 0L).longValue();
			}
			@Override
			public float getPlayerDataAsFloat(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, 0F).floatValue();
			}
			@Override
			public double getPlayerDataAsDouble(UUID uuid, String path)
			{
				return this.getPlayerDataAsNumber(uuid, path, 0D).doubleValue();
			}
			@Override
			public String getPlayerDataAsString(UUID uuid, String path)
			{
				return this.getPlayerData(uuid, path);
			}
			@Override
			public <T> List<T> getPlayerDataAsList(UUID uuid, String path)
			{
				List<T> data = this.getPlayerData(uuid, path);

				return (data == null ? Lists.newArrayList() : (List<T>) data);
			}
			@Override
			public List<String> getPlayerDataAsStringList(UUID uuid, String path)
			{
				return this.getPlayerDataAsList(uuid, path);
			}

			@Override
			public void setPlayerData(UUID uuid, String path, Object value)
			{
				path = path.toLowerCase();

				PlayerData data = this.getPlayerData(uuid);

				if (data == null) return;

				data.set(path, value);
			}

			@Override
			public void addToPlayerDataCollection(UUID uuid, String path, Object value)
			{
				List<Object> list = this.getPlayerDataAsList(uuid, path);

				boolean flag = list.isEmpty();

				list.add(value);

				if (!flag) return;

				this.setPlayerData(uuid, path, list);
			}

			@Override
			public boolean addToPlayerDataCollectionIfAbsent(UUID uuid, String path, Object value)
			{
				List<Object> values = this.getPlayerDataAsList(uuid, path);

				if (values.contains(value)) return false;

				boolean flag = values.isEmpty();

				values.add(value);

				if (!flag) return true;

				this.setPlayerData(uuid, path, values);

				return true;
			}

			@Override
			public boolean addOrRemovePlayerDataCollection(UUID uuid, String path, Object value)
			{
				List<Object> values = this.getPlayerDataAsList(uuid, path);

				if (!values.contains(value))
				{
					values.add(value);

					if (values.isEmpty())
					{
						this.setPlayerData(uuid, path, values);
					}

					return true;
				}

				values.remove(value);

				return false;
			}

			@Override
			public void removePlayerData(UUID uuid, String path)
			{
				path = path.toLowerCase();
				PlayerData data = this.getPlayerData(uuid);

				if (data == null) return;

				data.remove(path);
			}

			@Override
			public void setAllData(String path, Object value)
			{
				path = path.toLowerCase();

				for (PlayerData data : this.cache.values())
				{
					data.set(path, value);
				}
			}

			@Override
			public Set<UUID> uuidSet()
			{
				return this.cache.keySet();
			}

			@Override
			public void annihilate()
			{
				DataCollModule.this.log("Annihilate called...");

				if (this.cache == null) return;

				DataCollModule.this.log("Cache is present.");

				if (!this.acquire()) return;

				DataCollModule.this.log("Acquire was a success.");

				Collection<PlayerData> data = this.cache.values();

				DataCollModule.this.log("Player data collected.");

				Stream<PlayerData> stream = data.parallelStream();

				DataCollModule.this.log("Parallel stream created.");

				stream.forEach(PlayerData::write);

				this.lock.unlock();
			}
		};
	}

	@Override
	public void postDisable()
	{
		DataColl.impl.annihilate();

		DataColl.impl = IDataColl.EMPTY;
	}

	@Override
	public void runTest(Object data)
	{
		CommandSender sender = (CommandSender) data;

		int x = 1;
		for (UUID uuid : DataColl.impl().uuidSet())
		{
			sender.sendMessage(Strings.format("{0}) {1} [{2}]", x++, uuid, Bukkit.getOfflinePlayer(uuid).getName()));
		}
	}

}