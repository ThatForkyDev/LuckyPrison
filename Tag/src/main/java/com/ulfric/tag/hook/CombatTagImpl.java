package com.ulfric.tag.hook;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.hook.CombatTagHook.ICombatTagHook;
import com.ulfric.tag.CombatTag;

public enum CombatTagImpl implements ICombatTagHook {

	INSTANCE;

	@Override
	public boolean isTagged(Player player)
	{
		return player.hasMetadata("Combat Tag");
	}

	@Override
	public void tag(Player player, int seconds)
	{
		CombatTag.tag(player, seconds);
	}

}