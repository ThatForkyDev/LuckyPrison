package com.ulfric.lib.api.time;

import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public final class TimeModule extends SimpleModule {

	public TimeModule()
	{
		super("time", "Time utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new TimestampModule());
		this.withSubModule(new MillisecondsModule());
		this.withSubModule(new SecondsModule());
		this.withSubModule(new TicksModule());
	}

	@Override
	public void postEnable()
	{
		TimeUtils.impl = new TimeUtils.ITimeUtils() {
			private final String month = new SimpleDateFormat("YY-MM").format(Calendar.getInstance().getTime());

			@Override
			public String formatCurrentDay()
			{
				return new SimpleDateFormat("MM/dd/YY").format(Calendar.getInstance().getTime());
			}

			@Override
			public String formatCurrentTime()
			{
				return new SimpleDateFormat("MM/dd hh:mm:ss").format(Calendar.getInstance().getTime());
			}

			@Override
			public String formatCurrentDateFully()
			{
				return new SimpleDateFormat("MM/dd/YY hh:mm").format(Calendar.getInstance().getTime());
			}

			@Override
			public String millisecondsToString(long time)
			{
				return this.millisecondsToString(time, false);
			}

			@Override
			public String millisecondsToString(long time, boolean small)
			{
				return this.millisecondsToString(time, small, false);
			}

			@Override
			public String millisecondsToString(long time, boolean small, boolean abs)
			{
				return this.secondsToString(Math.floorDiv(time, Milliseconds.SECOND), small, abs);
			}

			@Override
			public String secondsToString(long time)
			{
				return this.secondsToString(time, false, false);
			}

			@Override
			public String secondsToString(long time, boolean small, boolean abs)
			{
				if (time == 0) { return "1 second"; }

				else if (time < 0)
				{
					if (!abs) return "NEVER";

					time = Math.abs(time);
				}

				StringBuilder builder = new StringBuilder();

				time = this.append(builder, small, time, Seconds.YEAR, small ? "y" : "year");
				time = this.append(builder, small, time, Seconds.MONTH, small ? "mo" : "month");
				time = this.append(builder, small, time, Seconds.WEEK, small ? "w" : "week");
				time = this.append(builder, small, time, Seconds.DAY, small ? "d" : "day");
				time = this.append(builder, small, time, Seconds.HOUR, small ? "h" : "hour");
				time = this.append(builder, small, time, Seconds.MINUTE, small ? "m" : "minute");
				time = this.append(builder, small, time, Seconds.SECOND, small ? "s" : "second");

				return builder.toString().trim();
			}

			@Override
			public long millisecondsTill(long time)
			{
				if (time == -1) return -1;

				return (time - System.currentTimeMillis());
			}

			@Override
			public long millisecondsSince(long time)
			{
				if (time == -1) return -1;

				return (System.currentTimeMillis() - time);
			}

			@Override
			public long secondsSince(long time)
			{
				return (Math.floorDiv(this.millisecondsSince(time), Milliseconds.SECOND));
			}

			@Override
			public long parseSeconds(String time)
			{
				return this.parseSeconds(time, -1);
			}

			@Override
			public long parseSeconds(String time, long badValue)
			{

				if (time.contains(":"))
				{
					time = time.split(":")[1];
				}

				time = time.trim().toLowerCase().replace("mm", "n");

				StringBuilder builder = new StringBuilder();

				long key = 0;
				for (int x = 0; x < time.length(); x++)
				{
					char character = time.charAt(x);

					long multiplier;
					switch (character)
					{
						case 's':
							time = time.replaceFirst(Pattern.quote("s"), " ");
							multiplier = 1;
							break;

						case 'm':
							time = time.replaceFirst(Pattern.quote("m"), " ");
							multiplier = Seconds.MINUTE;
							break;

						case 'h':
							time = time.replaceFirst(Pattern.quote("h"), " ");
							multiplier = Seconds.HOUR;
							break;

						case 'd':
							time = time.replaceFirst(Pattern.quote("d"), " ");
							multiplier = Seconds.DAY;
							break;

						case 'w':
							time = time.replaceFirst(Pattern.quote("w"), " ");
							multiplier = Seconds.WEEK;
							break;

						case 'n':
							time = time.replaceFirst(Pattern.quote("n"), " ");
							multiplier = Seconds.MONTH;
							break;

						case 'y':
							time = time.replaceFirst(Pattern.quote("y"), " ");
							multiplier = Seconds.YEAR;
							break;

						default:
							builder.append(character);
							continue;
					}

					Integer value = Numbers.parseInteger(builder.toString());
					if (value == null) return badValue;

					key += (value * multiplier);
					builder.setLength(0);
				}

				if (builder.length() != 0)
				{
					Integer number = Numbers.parseInteger(builder.toString());
					if (number != null) key += number;
				}

				return Math.abs(key);
			}

			@Override
			public String month()
			{
				return this.month;
			}

			private long append(StringBuilder builder, boolean small, long time, long timespan, String word)
			{
				long total = time / timespan;
				if (total <= 0) return time;

				builder.append(total);

				if (!small) builder.append(' ');

				builder.append(word);

				if (!small)
				{
					if (total > 1) builder.append('s');

					builder.append(' ');
				}

				time -= (total * timespan);
				return time;
			}
		};
	}

	private static final class TimestampModule extends SimpleModule {
		TimestampModule()
		{
			super("timestamp", "Timestamp class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Timestamp.TimestampAPI.impl = new Timestamp.TimestampAPI.ITimestamp() {
				private Timestamp infinite;

				@Override
				public Timestamp now()
				{
					return new MutableTimestamp();
				}

				@Override
				public Timestamp of(long time)
				{
					if (time == -1) return this.infinite();

					return new MutableTimestamp(time);
				}

				@Override
				public Timestamp future(long future)
				{
					if (future == -1) return this.infinite();

					return new MutableTimestamp(System.currentTimeMillis() + future);
				}

				@Override
				public Timestamp infinite()
				{
					if (this.infinite != null) return this.infinite;

					synchronized (this)
					{
						if (this.infinite == null)
						{
							this.infinite = new ImmutableTimestamp(-1);
						}

						return this.infinite;
					}
				}
			};
		}
	}

	private static final class MillisecondsModule extends SimpleModule {
		MillisecondsModule()
		{
			super("milliseconds", "Milliseconds class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Milliseconds.impl = new Milliseconds.IMilliseconds() {
				@Override
				public long fromTicks(long ticks)
				{
					return ((ticks / 20) * 1000);
				}

				@Override
				public long fromSeconds(double seconds)
				{
					return (long) (seconds * Milliseconds.SECOND);
				}

				@Override
				public long fromSeconds(long seconds)
				{
					return seconds * Milliseconds.SECOND;
				}

				@Override
				public long fromMinutes(double minutes)
				{
					return (long) (minutes * Milliseconds.MINUTE);
				}

				@Override
				public long fromHours(double hours)
				{
					return (long) (hours * Milliseconds.HOUR);
				}

				@Override
				public long toMinutes(long milliseconds)
				{
					return milliseconds / Milliseconds.MINUTE;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Milliseconds.impl = Milliseconds.IMilliseconds.EMPTY;
		}
	}

	private static final class SecondsModule extends SimpleModule {
		SecondsModule()
		{
			super("seconds", "Seconds class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Seconds.impl = new Seconds.ISeconds() {
				@Override
				public long fromMinutes(double minutes)
				{
					return (long) (minutes * 60);
				}

				@Override
				public long fromTicks(long ticks)
				{
					return ticks / Ticks.SECOND;
				}
			};
		}

		@Override
		public void postDisable()
		{
			Seconds.impl = Seconds.ISeconds.EMPTY;
		}
	}	@Override
	public void postDisable()
	{
		TimeUtils.impl = TimeUtils.ITimeUtils.EMPTY;
	}

	private static final class TicksModule extends SimpleModule {
		TicksModule()
		{
			super("ticks", "Ticks class module", "Packet", "1.0.0-REL");
		}

		@Override
		public void postEnable()
		{
			Ticks.impl = new Ticks.ITicks() {
				@Override
				public long fromMilliseconds(long milliseconds)
				{
					return milliseconds / 50;
				}

				@Override
				public long fromSeconds(double seconds)
				{
					return (long) (seconds * 20);
				}

				@Override
				public long fromSeconds(long seconds)
				{
					return seconds * 20;
				}

				@Override
				public long fromMinutes(double minutes)
				{
					return Ticks.fromSeconds(minutes * 60);
				}
			};
		}

		@Override
		public void postDisable()
		{
			Ticks.impl = Ticks.ITicks.EMPTY;
		}
	}




}
