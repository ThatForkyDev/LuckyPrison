package com.ulfric.lib.api.world;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.java.Uuids;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;
import org.bukkit.Bukkit;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class WorldModule extends SimpleModule {

	public WorldModule()
	{
		super("world", "World utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new EnvironmentModule());
	}

	@Override
	public void runTest(Object object)
	{
		CommandSender sender = (CommandSender) object;

		Bukkit.getWorlds().forEach(world -> sender.sendMessage(Strings.format("- {0}: {1}", world.getName(), world.getPlayers().size())));
	}

	@Override
	public void postEnable()
	{
		Worlds.impl = new Worlds.IWorlds() {
			private World main;
			private ExecutorService executor;
			private Map<String, WorldProxy> proxyCache;

			@Override
			public World parse(String string)
			{
				World world = Bukkit.getWorld(string);

				if (world != null) return world;

				UUID uuid = Uuids.parse(string);

				if (uuid != null) return Bukkit.getWorld(uuid);

				Integer number = Numbers.parseInteger(string);

				if (number == null) return null;

				return Bukkit.getWorlds().get(number);
			}

			@Override
			public WorldProxy proxy(String name)
			{
				if (name == null) return null;

				name = name.toLowerCase();

				if (this.proxyCache == null)
				{
					this.proxyCache = Maps.newHashMapWithExpectedSize(Bukkit.getWorlds().size());

					WorldProxy proxy = new WorldProxy(name);

					this.proxyCache.put(name, proxy);

					return proxy;
				}

				WorldProxy proxy = this.proxyCache.get(name);

				if (proxy != null) return proxy;

				proxy = new WorldProxy(name);

				this.proxyCache.put(name, proxy);

				return proxy;
			}

			@Override
			public Future<World> createAsync(WorldCreator creator)
			{
				if (this.executor == null)
				{
					synchronized (this)
					{
						if (this.executor == null)
						{
							this.executor = Executors.newCachedThreadPool();
						}
					}
				}

				return this.executor.submit(creator::createWorld);
			}

			@Override
			public boolean isMinigames(World world)
			{
				return "world_games".equals(world.getName());
			}

			@Override
			public World main()
			{
				if (this.main == null)
				{
					this.main = Bukkit.getWorld("world");
				}
				return this.main;
			}
		};
	}

	private static final class EnvironmentModule extends SimpleModule {
		private EnvironmentModule()
		{
			super("environment", "Environment utilities module", "Packet", "1.0.0-REL");

			this.withSubModule(new WeatherModule());
			this.withSubModule(new ClockModule());
		}
	}

	private static final class ClockModule extends SimpleModule {
		private ClockModule()
		{
			super("clock", "Clock utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Clock.impl = new Clock.IClock() {
				@Override
				public TimeType parse(long time)
				{
					// TODO  this can be a lot simpler.
					//       (really, most of the legacy
					//       code should be redone)
					time = Math.min(Math.max(0, time), 24000);

					if (time < 12000)
					{
						if (time > TimeType.SUNSET.getTime()) { return TimeType.SUNSET; }

						else if (time < TimeType.SUNRISE.getTime()) { return TimeType.SUNRISE; }

						else if (time > TimeType.MIDDAY.getTime() - 1500 && time < TimeType.MIDDAY.getTime() + 1500)
						{ return TimeType.MIDDAY; }

						return TimeType.DAY;
					}
					else if (time >= 12000)
					{
						if (time >= TimeType.EARLY.getTime()) return TimeType.EARLY;

						if (time < TimeType.MIDNIGHT.getTime() - 2000 || time > TimeType.MIDNIGHT.getTime() + 2000)
						{ return TimeType.NIGHT; }

						return TimeType.MIDNIGHT;
					}

					return null;
				}

				@Override
				public long parse(int hour)
				{
					if (hour > 24) { hour = 24; }
					else if (hour < 0) hour = 0;
					return hour * 1000L;
				}

				@Override
				public TimeType parse(String value)
				{
					TimeType type = null;
					try
					{
						type = TimeType.valueOf(value);
					}
					catch (IllegalArgumentException exception) { }
					return type;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Clock.impl = Clock.IClock.EMPTY;
		}
	}

	private static final class WeatherModule extends SimpleModule {
		private WeatherModule()
		{
			super("weather", "Weather utilities module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Weather.impl = new Weather.IWeather() {
				@Override
				public WeatherType parse(String value)
				{
					try
					{
						return WeatherType.valueOf(value.toUpperCase());
					}
					catch (Exception exception) { return null; }
				}

				@Override
				public WeatherType parse(int value)
				{
					return (value == 1 ? WeatherType.DOWNFALL : WeatherType.CLEAR);
				}

				@Override
				public WeatherType parse(boolean value)
				{
					return (value ? WeatherType.DOWNFALL : WeatherType.CLEAR);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Weather.impl = Weather.IWeather.EMPTY;
		}
	}	@Override
	public void postDisable()
	{
		Worlds.impl = Worlds.IWorlds.EMPTY;
	}




}
