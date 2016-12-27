package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.CombatTagHook.ICombatTagHook;
import org.bukkit.entity.Player;

public final class CombatTagHook extends Hook<ICombatTagHook> {

	CombatTagHook()
	{
		super(ICombatTagHook.EMPTY, "Tag", "CombatTag hook module", "Packet", "1.0.0-REL");
	}

	public boolean isTagged(Player player)
	{
		return this.impl.isTagged(player);
	}

	public void tag(Player player, int seconds)
	{
		this.impl.tag(player, seconds);
	}

	public interface ICombatTagHook extends HookImpl {
		ICombatTagHook EMPTY = new ICombatTagHook() {
		};

		default boolean isTagged(Player player)
		{
			return false;
		}

		default void tag(Player player, int seconds)
		{
		}
	}

}
