package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PlayerUseSignEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final Sign sign;
	private final ClickType type;

	public PlayerUseSignEvent(Player who, Sign sign, ClickType type)
	{
		super(who);

		this.sign = sign;

		this.type = type;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public Sign getSign()
	{
		return this.sign;
	}

	public ClickType getType()
	{
		return this.type;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

	public enum ClickType {
		RIGHT,
		LEFT
	}

}
