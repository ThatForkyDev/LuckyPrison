package com.ulfric.prison.modules;

import java.util.Collection;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;

public class ModuleListperks extends SimpleModule {

	public ModuleListperks()
	{
		super("listperks", "Adds the /perks command", "Packet", "1.0.0-REL");

		this.withConf();

		this.addCommand("perks", new CommandPerks());
	}

	@Override
	public void postEnable()
	{
		this.perks = Maps.newHashMap();

		FileConfiguration conf = this.getConf().getConf();

		for (String key : conf.getKeys(false))
		{
			this.perks.put(Hooks.PERMISSIONS.group(key), Chat.color(conf.getStringList(key)));
		}
	}

	private class CommandPerks extends SimpleCommand
	{
		public CommandPerks()
		{
			this.withEnforcePlayer();
		}

		@Override
		public void run()
		{
			Player player = this.getPlayer();

			Group rank = Hooks.PERMISSIONS.user(player).getRankLadderGroup("mines");

			if (rank == null)
			{
				rank = Hooks.PERMISSIONS.group("a");

				if (rank == null)
				{
					Locale.sendError(player, "prison.rank_none");

					return;
				}
			}

			Locale.send(player, "prison.rank_perks");

			Collection<String> perks = ModuleListperks.this.perks.get(rank);

			if (CollectUtils.isEmpty(perks))
			{
				Locale.sendError(player, "prison.perks_not_found");

				return;
			}

			player.sendMessage(StringUtils.merge(perks, '\n'));
		}
	}

	private Map<Group, Collection<String>> perks;

}