package com.ulfric.prison.scoreboard.element;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.locale.Locale;

public class GodmodeElement extends ScoreboardElement {

	public GodmodeElement()
	{
		super(null, 0, 0);
	}

	@Override
	public String get(Player player)
	{
		if (!player.hasMetadata("_ulf_godmode")) return null;

		return Locale.getMessage(player, "prison.sb_godmode");
	}

}