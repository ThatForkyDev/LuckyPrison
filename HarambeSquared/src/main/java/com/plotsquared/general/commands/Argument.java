package com.plotsquared.general.commands;

import com.intellectualcrafters.plot.object.PlotId;

public abstract class Argument<T> {

	public static final Argument<Integer> Integer = new Argument<Integer>("int", 16) {
		@Override
		public Integer parse(String in)
		{
			Integer value = null;
			try
			{
				value = java.lang.Integer.parseInt(in);
			}
			catch (Exception ignored) {}
			return value;
		}
	};
	public static final Argument<Boolean> Boolean = new Argument<Boolean>("boolean", true) {
		@Override
		public Boolean parse(String in)
		{
			Boolean value = null;
			if ("true".equalsIgnoreCase(in) || "Yes".equalsIgnoreCase(in) || "1".equalsIgnoreCase(in))
			{
				value = true;
			}
			else if ("false".equalsIgnoreCase(in) || "No".equalsIgnoreCase(in) || "0".equalsIgnoreCase(in))
			{
				value = false;
			}
			return value;
		}
	};
	public static final Argument<String> String = new Argument<String>("String", "Example") {
		@Override
		public String parse(String in)
		{
			return in;
		}
	};
	public static final Argument<String> PlayerName = new Argument<String>("PlayerName", "Dinnerbone") {
		@Override
		public String parse(String in)
		{
			return in.length() <= 16 ? in : null;
		}
	};
	public static final Argument<PlotId> PlotID = new Argument<PlotId>("PlotID", new PlotId(-6, 3)) {
		@Override
		public PlotId parse(String in)
		{
			return PlotId.fromString(in);
		}
	};
	private final String name;
	private final T example;

	protected Argument(String name, T example)
	{
		this.name = name;
		this.example = example;
	}

	public abstract T parse(String in);

	@Override
	public final String toString()
	{
		return this.name;
	}

	public final String getName()
	{
		return this.name;
	}

	public final T getExample()
	{
		return this.example;
	}
}
