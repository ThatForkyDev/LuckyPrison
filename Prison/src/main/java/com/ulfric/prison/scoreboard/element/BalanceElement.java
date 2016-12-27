package com.ulfric.prison.scoreboard.element;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ulfric.lib.api.hook.EconHook.BalanceChangeEvent;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.prison.scoreboard.ScoreboardPanel;

public class BalanceElement extends ScoreboardElement implements Listener {

	public BalanceElement()
	{
		super("prison.sb_balance", 0, 0);
	}

	@Override
	public String get(Player player)
	{
		if (!Hooks.ECON.isModuleEnabled()) return null;

		return ChatColor.GREEN + StringUtils.formatMoneyFully(Hooks.ECON.getMoney(player.getUniqueId()), true);
	}

	@EventHandler
	public void onBalanceChange(BalanceChangeEvent event)
	{
		Player player = Bukkit.getPlayer(event.getUniqueId());

		if (player == null) return;

		ScoreboardPanel.INSTANCE.updateAll(player);
	}

}