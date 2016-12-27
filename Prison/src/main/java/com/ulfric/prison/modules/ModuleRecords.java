package com.ulfric.prison.modules;

import java.io.File;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.persist.Persist;
import com.ulfric.lib.api.server.Events;
import com.ulfric.prison.Prison;
import com.ulfric.prison.configuration.ConfigurationStore;

public class ModuleRecords extends SimpleModule {

	private static final ModuleRecords INSTANCE = new ModuleRecords();
	public static final ModuleRecords get() { return ModuleRecords.INSTANCE; }

	private int dailyRecord;

	private ModuleRecords()
	{
		super("records", "Record keeping module", "Packet", "1.0.0-REL");

		this.addListener(new Listener()
		{
			/*private final AtomicInteger connected = new AtomicInteger();

			@EventHandler
			public void onJoin(PlayerJoinEvent event)
			{
				this.connected.incrementAndGet();
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent event)
			{
				this.connected.decrementAndGet();
			}*/

			@EventHandler
			public void onJoin(PlayerJoinEvent event)
			{
				int current = Bukkit.getOnlinePlayers().size();

				ConfigFile conf = ModuleRecords.this.getRecord("players");

				int value = conf.getConf().getInt("record");

				if (current > value)
				{
					Events.call(new PlayerBreakRecordEvent(event.getPlayer()));

					conf.getConf().set("record", current);

					conf.save();

					Locale.sendMass("prison.record_players", event.getPlayer().getName(), value);

					return;
				}

				if (current > ModuleRecords.this.dailyRecord)
				{
					Events.call(new PlayerBreakRecordEvent(event.getPlayer()));

					Locale.sendMass("prison.record_players_daily", event.getPlayer().getName(), ModuleRecords.this.dailyRecord++);
				}
			}
		});

		this.addCommand("dailyrecord", (sender, command, label, args) -> {
			Locale.send(sender, "prison.record_daily", ModuleRecords.this.dailyRecord);
			return true;
		});
	}

	@Override
	public void postEnable()
	{
		this.records = Maps.newConcurrentMap();

		for (File file : ConfigurationStore.INSTANCE.getFilesFromFolder("records"))
		{
			String name = file.getName();

			name = name.substring(0, name.length()-4).toLowerCase();

			this.records.put(name, Persist.newConfig(file));
		}
	}

	@Override
	public void postDisable()
	{
		for (ConfigFile file : this.records.values())
		{
			if (file.save()) continue;

			this.log("Unable to save records in file {0}", file.getName());
		}
	}

	private Map<String, ConfigFile> records;
	public ConfigFile getRecord(String name)
	{
		ConfigFile conf = this.records.get(name = name.toLowerCase());

		if (conf != null) return conf;

		File file = new File(new File(Prison.get().getDataFolder(), "records"), name + ".yml");

		conf = Persist.newConfig(file);

		if (!file.exists())
		{
			this.records.put(name, conf);
		}

		return conf;
	}

	public static class PlayerBreakRecordEvent extends PlayerEvent
	{
		public PlayerBreakRecordEvent(Player who)
		{
			super(who);
		}

		private static final HandlerList HANDLERS = Events.newHandlerList();
		@Override
		public HandlerList getHandlers()
		{
			return PlayerBreakRecordEvent.HANDLERS;
		}
		public static HandlerList getHandlerList()
		{
			return PlayerBreakRecordEvent.HANDLERS;
		}
	}

}
