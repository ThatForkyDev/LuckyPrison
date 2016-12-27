package com.ulfric.lib.api.command;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;
import org.bukkit.entity.Player;

public final class CooldownState {

	public static final CooldownState ALLOWED = new CooldownState(0, CooldownNode.CooldownType.NO_DELAY);
	private final boolean allowed;
	private final long delay;
	private final CooldownNode.CooldownType type;

	CooldownState(long delay, CooldownNode.CooldownType type)
	{
		this.allowed = delay == 0;

		this.delay = delay;

		this.type = type;
	}

	public boolean isAllowed()
	{
		return this.allowed;
	}

	public long getDelay()
	{
		return this.delay;
	}

	public CooldownNode.CooldownType getType()
	{
		return this.type;
	}

	public boolean error(Player player)
	{
		if (this.allowed) return false;

		switch (this.type)
		{
			case DELAY:
				Locale.sendError(player, "lib.cooldown_delay", TimeUtils.millisecondsToString(this.delay));
				break;

			case LOCK:
				Locale.sendError(player, "lib.cooldown_lock");
				break;

			case ONE_TIME:
				Locale.sendError(player, "lib.cooldown_once");
				break;

			default:
				return false;
		}

		return true;
	}

}
