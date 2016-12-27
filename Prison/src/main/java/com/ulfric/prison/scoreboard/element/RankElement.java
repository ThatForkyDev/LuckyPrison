package com.ulfric.prison.scoreboard.element;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.prison.events.PlayerRankupEvent;
import com.ulfric.prison.scoreboard.ScoreboardPanel;

public class RankElement extends ScoreboardElement implements Listener {

	public RankElement()
	{
		super("prison.sb_current_rank", 0, 0);
	}

	@Override
	public String get(Player player)
	{
		if (!Hooks.PERMISSIONS.isModuleEnabled()) return null;

		User user = Hooks.PERMISSIONS.user(player);

		if (user == null) return null;

		Group rank = user.getRankLadderGroup("mines");

		if (rank == null) return null;

		return Strings.formatF("&a{0} (/mine)", WordUtils.capitalize(rank.getName()));
	}

	@EventHandler
	public void onRankup(PlayerRankupEvent event)
	{
		ScoreboardPanel.INSTANCE.updateAll(event.getPlayer());
	}

}