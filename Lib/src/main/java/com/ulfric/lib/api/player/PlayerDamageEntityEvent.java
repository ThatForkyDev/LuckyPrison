package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.event.player.CancellablePlayerEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlayerDamageEntityEvent extends CancellablePlayerEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();
	private final Entity damaged;

	public PlayerDamageEntityEvent(Player attacker, Entity damaged)
	{
		super(attacker);

		this.damaged = damaged;
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	public Entity getDamaged()
	{
		return this.damaged;
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
