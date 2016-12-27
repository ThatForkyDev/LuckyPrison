package com.ulfric.lib.api.player;

import com.ulfric.lib.api.server.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class PlayerDamagePlayerEvent extends PlayerDamageEntityEvent {

	private static final HandlerList HANDLERS = Events.newHandlerList();

	public PlayerDamagePlayerEvent(Player attacker, Player damaged)
	{
		super(attacker, damaged);
	}

	public static HandlerList getHandlerList()
	{
		return HANDLERS;
	}

	@Override
	public Player getDamaged()
	{
		return (Player) super.getDamaged();
	}

	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS;
	}

}
