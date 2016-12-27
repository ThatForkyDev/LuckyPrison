package com.ulfric.prison.scoreboard.element;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.prison.events.PlayerRankupEvent;
import com.ulfric.prison.modules.ModuleRankup;
import com.ulfric.prison.scoreboard.ScoreboardPanel;

public class NextRankElement extends ScoreboardElement implements Listener {

	public NextRankElement()
	{
		super("prison.sb_next_rank", 10, 0);
	}

	@Override
	public String get(Player player)
	{
		if (!Hooks.PERMISSIONS.isModuleEnabled() || !Hooks.ECON.isModuleEnabled()) return null;

		User user = Hooks.PERMISSIONS.user(player);

		if (user == null) return null;

		Group rank = user.getRankLadderGroup("mines");

		if (rank == null) return null;

		rank = rank.getNext();

		if (rank == null) return null;

		Price price = ModuleRankup.get().getPrice(rank);

		if (price == null) return null;

		return Chat.linePercentage(price.isToken() ? Hooks.ECON.getTokens(player.getUniqueId()) : (long) Hooks.ECON.getMoney(player.getUniqueId()), price.getAmount().longValue(), '|', 20, ChatColor.GREEN, ChatColor.GRAY, ChatColor.LIGHT_PURPLE, true);
	}

	@EventHandler
	public void onRankup(PlayerRankupEvent event)
	{
		ScoreboardPanel.INSTANCE.updateAll(event.getPlayer());
	}

}