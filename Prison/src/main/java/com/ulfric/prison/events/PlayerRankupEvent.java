package com.ulfric.prison.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.server.Events;

public class PlayerRankupEvent extends PlayerEvent {

	public PlayerRankupEvent(Player who, Group oldGroup, Group newGroup)
	{
		super(who);

		this.oldGroup = oldGroup;

		this.newGroup = newGroup;
	}

	private final Group newGroup;
	public Group getNewGroup() { return this.newGroup; }

	private final Group oldGroup;
	public Group getOldGroup() { return this.oldGroup; }

	private static final HandlerList HANDLERS = Events.newHandlerList();
	@Override
	public HandlerList getHandlers() { return PlayerRankupEvent.HANDLERS; }
	public static HandlerList getHandlerList() { return PlayerRankupEvent.HANDLERS; }

}