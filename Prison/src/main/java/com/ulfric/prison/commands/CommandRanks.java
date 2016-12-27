package com.ulfric.prison.commands;

import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.EconHook.Price;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.modules.ModuleRankup;

public class CommandRanks extends SimpleCommand {

	public CommandRanks()
	{
		this.withEnforcePlayer();
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		Group rank = Hooks.PERMISSIONS.user(player).getRankLadderGroup("mines");

		for (Entry<Group, Price> entry : ModuleRankup.get().getPrices())
		{
			Group group = entry.getKey();

			String message = Strings.formatF("&9{0} &7- &b{1} ", StringUtils.capitalize(group.getName()), entry.getValue().toString(true));

			if (rank != null && rank.equals(group))
			{
				message = message + Locale.getMessage(player, "prison.rank_here");
			}

			player.sendMessage(message);
		}
	}

}