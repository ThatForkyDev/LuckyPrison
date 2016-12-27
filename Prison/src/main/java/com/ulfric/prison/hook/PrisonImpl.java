package com.ulfric.prison.hook;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.hook.PrisonHook.IPrisonHook;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.prison.scoreboard.ScoreboardPanel;

public enum PrisonImpl implements IPrisonHook {

	INSTANCE;

	@Override
	public void updateScoreboard(Player player)
	{
		Assert.notNull(player);

		ScoreboardPanel.INSTANCE.updateAll(player);
	}

}