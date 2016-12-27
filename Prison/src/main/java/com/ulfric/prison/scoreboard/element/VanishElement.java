package com.ulfric.prison.scoreboard.element;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.player.PlayerVanishEvent;
import com.ulfric.prison.scoreboard.ScoreboardPanel;

public class VanishElement extends ScoreboardElement implements Listener {

	public VanishElement()
	{
		super(null, 11, 0);
	}

	@Override
	public String get(Player player)
	{
		if (!PlayerUtils.isVanished(player)) return null;

		return Locale.getMessage(player, "prison.sb_vanish");
	}

	@EventHandler
	public void onVanish(PlayerVanishEvent event)
	{
		ScoreboardPanel.INSTANCE.updateAll(event.getPlayer());
	}

}