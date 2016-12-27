package com.ulfric.chat.modules;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Uuids;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.persist.ConfigFile;
import com.ulfric.lib.api.persist.Persist;

class ModuleTrends extends SimpleModule {

	protected static boolean enabled; // TEMP

	public ModuleTrends()
	{
		super("trends", "Hashtag trends", "Packet", "1.0.0-SNAPSHOT");

		this.withConf();
	}

	private static final Map<String, Trend> TRENDS = Maps.newConcurrentMap();

	@Override
	public void postEnable()
	{
		ModuleTrends.enabled = true;
	}

	@Override
	public void onFirstEnable()
	{
		ConfigFile conf = this.getConf();

		for (String key : conf.getKeys(false))
		{
			ConfigurationSection section = conf.getSection(key);

			Trend t = new Trend(key.toLowerCase(), section.getInt("count"), section.getStringList("used").stream().map(Uuids::parse).filter(Objects::nonNull).collect(Collectors.toList()), section.getStringList("reach").stream().map(Uuids::parse).filter(Objects::nonNull).collect(Collectors.toList()));
			t.starter = Uuids.parse(section.getString("starter"));
			ModuleTrends.TRENDS.put(key, t);
		}

		this.addCommand("trends", new SimpleCommand()
		{
			{
				this.withArgument("trend", new ArgStrategy<Trend>()
				{
					@Override
					public Trend match(String string)
					{
						return Trend.fetch(string);
					}
				}, "chat.trend_needed");
			}

			@Override
			public void run()
			{
				Trend trend = (Trend) this.getObject("trend");

				Locale.send(this.getSender(), "chat.trend_info", trend.getName(), StringUtils.formatNumber(trend.getCount()), StringUtils.formatNumber(trend.getUnique()), StringUtils.formatNumber(trend.getReach()), Bukkit.getOfflinePlayer(trend.starter).getName());
			}
		});
	}

	@Override
	public void postDisable()
	{
		ModuleTrends.enabled = false;

		FileConfiguration conf = this.getConf().getConf();

		Stream<Entry<String, Trend>> stream = ModuleTrends.TRENDS.entrySet().parallelStream().filter(entry -> entry.getValue().mod);

		stream.forEach(entry ->
		{
			Trend trend = entry.getValue();
			String name = entry.getKey();
			conf.set(name + ".count", trend.count.get());
			conf.set(name + ".used", trend.uuids.stream().map(UUID::toString).collect(Collectors.toList()));
			conf.set(name + ".reach", trend.reach.stream().map(UUID::toString).collect(Collectors.toList()));
			conf.set(name + ".starter", String.valueOf(trend.starter));
		});

		Persist.save(conf, this.getConf().getFile());
	}

	public static class Trend implements Named
	{
		public static Trend of(String name)
		{
			name = Trend.fixName(name);

			Trend trend = Trend.fetchInternal(name);

			if (trend != null) return trend;

			trend = new Trend(name);

			ModuleTrends.TRENDS.put(name, trend);

			return trend;
		}

		public static Trend fetch(String name)
		{
			return Trend.fetchInternal(Trend.fixName(name));
		}

		private static Trend fetchInternal(String name)
		{
			return ModuleTrends.TRENDS.get(name);
		}

		private static String fixName(String name)
		{
			return name.toLowerCase().replace("#", Strings.BLANK);
		}

		private Trend(String name, int used, Collection<UUID> uuids, Collection<UUID> reach)
		{
			this.name = name;
			this.count = new AtomicInteger(used);
			this.uuids = Collections.synchronizedSet(Sets.newHashSet(uuids));
			this.reach = Collections.synchronizedSet(Sets.newHashSet(reach));
		}

		private Trend(String name)
		{
			this.name = name;
			this.count = new AtomicInteger();
			this.uuids = Collections.synchronizedSet(Sets.newHashSet());
			this.reach = Collections.synchronizedSet(Sets.newHashSet());
		}

		private final String name;
		private UUID starter;
		private final Set<UUID> uuids;
		private final AtomicInteger count;
		private final Set<UUID> reach;
		private boolean mod;

		@Override
		public String getName()
		{
			return this.name;
		}

		public void add(UUID uuid)
		{
			this.mod = true;

			this.uuids.add(uuid);

			this.count.incrementAndGet();

			if (this.starter != null) return;

			this.starter = uuid;
		}

		public void see(UUID uuid)
		{
			this.mod = true;

			this.reach.add(uuid);
		}

		public int getUnique()
		{
			return this.uuids.size();
		}

		public int getCount()
		{
			return this.count.get();
		}

		public int getReach()
		{
			return this.reach.size();
		}
	}

}