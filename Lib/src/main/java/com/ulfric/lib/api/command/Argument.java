package com.ulfric.lib.api.command;

import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Assert;

import java.util.function.Supplier;

public final class Argument {

	public static final Argument REQUIRED_PLAYER = builder().withPath("player").withStrategy(ArgStrategy.PLAYER).withUsage("system.specify_player").build();
	public static final Argument REQUIRED_OFFLINE_PLAYER = builder().withPath("player").withStrategy(ArgStrategy.OFFLINE_PLAYER).withUsage("system.specify_player").build();
	public static final Argument REQUIRED_INET = builder().withPath("inet").withStrategy(ArgStrategy.INET).withUsage("system.specify_address").build();
	private final String path;
	private final boolean required;
	private final int cap;
	private final boolean hasCap;
	private final boolean removalExclusion;
	private final String node;
	private final String nodeErr;
	private final ArgStrategy<?> strategy;
	private final Object defaultValue;
	private final boolean defaultCallable;
	private final String usage;

	private Argument(String path, ArgStrategy<?> strategy, Object defaultValue, boolean defaultCallable, String usage, String node, String nodeErr, int cap, boolean removalExclusion)
	{
		Assert.isNotEmpty(path);
		this.path = path;

		this.required = usage != null;

		this.strategy = Assert.notNull(strategy);

		this.defaultValue = defaultValue;

		this.defaultCallable = defaultCallable;

		if (this.required && defaultValue == null)
		{
			Assert.isNotEmpty(usage, "Usage must not be null for required args!", "Usage must not be empty for required args!");
		}
		this.usage = usage;

		this.node = node;

		this.nodeErr = nodeErr;

		this.cap = cap;
		this.hasCap = cap > 0;

		this.removalExclusion = removalExclusion;
	}

	public static Builder builder()
	{
		return new Builder();
	}

	public String getPath()
	{
		return this.path;
	}

	public boolean isRequired()
	{
		return this.required;
	}

	public int getCap()
	{
		return this.cap;
	}

	public boolean hasCap()
	{
		return this.hasCap;
	}

	public boolean hasRemovalExclusion()
	{
		return this.removalExclusion;
	}

	public String getNode()
	{
		return this.node;
	}

	public boolean hasNode()
	{
		return this.node != null;
	}

	public String getNodeErr()
	{
		return this.nodeErr;
	}

	public ArgStrategy<?> getStrategy()
	{
		return this.strategy;
	}

	public Object getDefault()
	{
		return this.defaultValue;
	}

	public boolean hasDefault()
	{
		return this.defaultValue != null;
	}

	public boolean isDefaultCallable()
	{
		return this.defaultCallable;
	}

	public String getUsage()
	{
		return this.usage;
	}

	public static final class Builder implements com.ulfric.lib.api.java.Builder<Argument> {
		private String path;
		private ArgStrategy<?> strategy;
		private Object defaultValue;
		private boolean defaultCallable;
		private String usage;
		private String node;
		private String nodeErr;
		private int cap;
		private boolean removalExclusion;

		private Builder()
		{
		}

		@Override
		public Argument build()
		{
			return new Argument(this.path, this.strategy, this.defaultValue, this.defaultCallable, this.usage, this.node, this.nodeErr, this.cap, this.removalExclusion);
		}

		public Builder withPath(String path)
		{
			Assert.isNotEmpty(path, "The path must not be null!", "The path must not be empty!");

			this.path = path;

			return this;
		}

		public Builder withStrategy(ArgStrategy<?> strategy)
		{
			Assert.notNull(strategy, "The argument strategy must not be null!");

			this.strategy = strategy;

			return this;
		}

		public Builder withDefault(Object defaultValue)
		{
			Assert.notNull(defaultValue, "The default value must not be null!");

			this.defaultValue = defaultValue;

			return this;
		}

		public Builder withDefaultCallable(Supplier<Object> defaultValue)
		{
			this.withDefault(defaultValue);

			this.defaultCallable = true;

			return this;
		}

		public Builder withUsage(String usage)
		{
			Assert.isNotEmpty(usage);

			this.usage = usage;

			return this;
		}

		public Builder withNode(String node)
		{
			this.withNode(node, "system.no_permission");

			return this;
		}

		public Builder withNode(String node, String err)
		{
			Assert.isNotEmpty(node, "The node must not be null!", "The node must not be empty!");

			Assert.isNotEmpty(err, "The node error must not be null!", "The node error must not be empty!");

			this.node = node;

			this.nodeErr = err;

			return this;
		}

		public Builder withCap(int cap)
		{
			Assert.isTrue(cap > 0);

			this.cap = cap;

			return this;
		}

		public Builder withRemovalExclusion()
		{
			this.removalExclusion = true;

			return this;
		}
	}

}
