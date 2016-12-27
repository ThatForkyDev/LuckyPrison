package com.ulfric.lib.api.command;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.math.strategy.MathStrategy;

import java.util.UUID;

public final class CooldownNode {

	private final long delay;
	private final CooldownType type;
	private Cooldown owner;

	CooldownNode(long delay)
	{
		this.delay = delay;

		this.type = Assert.notNull(CooldownType.fromLong(delay));
	}

	CooldownNode()
	{
		this(0);
	}

	void setOwner(Cooldown owner)
	{
		Assert.isNull(this.owner);

		this.owner = Assert.notNull(owner);
	}

	public long getDelay()
	{
		return this.delay;
	}

	public CooldownType getType()
	{
		return this.type;
	}

	public CooldownState getDelay(UUID uuid)
	{
		if (this.type == CooldownType.NO_DELAY) return CooldownState.ALLOWED;

		long value = Hooks.DATA.getPlayerDataAsLong(uuid, "cooldown.values." + this.owner.getName());

		if (value == 0)
		{
			return this.use(uuid);
		}

		else if (value > 0)
		{
			long current = System.currentTimeMillis();

			long del = Math.max(value - current, 0);

			if (del == 0)
			{
				return this.use(uuid);
			}

			return new CooldownState(del, CooldownType.DELAY);
		}

		else if (value == -1)
		{
			if (this.type != CooldownType.ONE_TIME) return this.use(uuid);

			return new CooldownState(-1, CooldownType.ONE_TIME);
		}

		else if (value <= -2)
		{
			if (this.type != CooldownType.LOCK) return this.use(uuid);

			return new CooldownState(value, CooldownType.LOCK);
		}

		return this.use(uuid);
	}

	private CooldownState use(UUID uuid)
	{
		Hooks.DATA.setPlayerData(uuid, "cooldown.values." + this.owner.getName(), this.type.time(this.delay));

		return CooldownState.ALLOWED;
	}

	public CooldownNode getLower(CooldownNode other)
	{
		if (this.equals(other)) return this;

		if (this.type == CooldownType.NO_DELAY) { return this; }

		else if (other.type == CooldownType.NO_DELAY) return other;

		if (other.type == CooldownType.ONE_TIME) { return this; }

		else if (this.type == CooldownType.ONE_TIME) return other;

		if (other.type == CooldownType.LOCK) { return this; }

		else if (this.type == CooldownType.LOCK) return other;

		return this.delay > other.delay ? other : this;
	}

	protected enum CooldownType {
		DELAY(1, MathStrategy.GREATER_THAN_OR_EQUAL)
				{
					@Override
					public long time(long add)
					{
						return System.currentTimeMillis() + add;
					}
				},
		ONE_TIME(-1, MathStrategy.EQUAL),
		NO_DELAY(0, MathStrategy.EQUAL),
		LOCK(-2, MathStrategy.LESS_THAN_OR_EQUAL);

		private final long acceptableValue;
		private final MathStrategy strategy;

		CooldownType(long acceptableValue, MathStrategy strategy)
		{
			this.acceptableValue = acceptableValue;

			this.strategy = strategy;
		}

		public static CooldownType fromLong(long delay)
		{
			for (CooldownType type : CooldownType.values())
			{
				if (!type.matches(delay)) continue;

				return type;
			}

			return NO_DELAY;
		}

		public long time(long add)
		{
			return this.acceptableValue;
		}

		public boolean matches(long value)
		{
			return this.strategy.matches(value, this.acceptableValue);
		}
	}

}
