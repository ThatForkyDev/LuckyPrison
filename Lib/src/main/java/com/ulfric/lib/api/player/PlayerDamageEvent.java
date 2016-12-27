package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public final class PlayerDamageEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final EntityDamageEvent.DamageCause cause;

	public PlayerDamageEvent(Player player, EntityDamageEvent.DamageCause cause)
	{
		super(player);

		this.cause = cause;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public EntityDamageEvent.DamageCause getCause()
	{
		return this.cause;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
