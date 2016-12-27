package com.ulfric.tag;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.server.Events;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.tag.event.PlayerTagEvent;
import com.ulfric.tag.event.enums.CombatTagStatus;

public class CombatTag {

	public static boolean disabled;

	public static boolean tag(Player player, long delayInSeconds)
	{
		if (CombatTag.disabled) return true;

		boolean returnValue = false;

		if (player.hasMetadata("_ulf_combattag"))
		{
			Tasks.cancel(Metadata.getValueAsInt(player, "_ulf_combattag"));

			returnValue = true;
		}

		Events.call(new PlayerTagEvent(player, CombatTagStatus.ENABLED));

		Metadata.apply(player, "_ulf_combattag", Tasks.runLater(() ->
		{
			Metadata.remove(player, "_ulf_combattag");

			Events.call(new PlayerTagEvent(player, CombatTagStatus.DISABLED));
		}, Ticks.fromSeconds(delayInSeconds)).getTaskId());

		if (player.isFlying())
		{
			player.setAllowFlight(false);
		}

		Hooks.PETS.removePet(player, false, true);

		return returnValue;
	}

	public static boolean isTagged(Player player)
	{
		return !CombatTag.disabled && player.hasMetadata("_ulf_combattag") && !player.hasPermission("tag.bypass");
	}

}