package com.ulfric.prison.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.prison.modules.ModuleSell;

public class CommandProfile extends SimpleCommand {

	public CommandProfile()
	{
		this.withArgument("player", ArgStrategy.OFFLINE_PLAYER, this::getPlayer);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		Locale.send(sender, "system.data_lookup");

		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		if (!PlayerUtils.hasPlayed(player))
		{
			Locale.sendError(sender, "system.never_logged_in");

			return;
		}

		UUID senderUuid = this.isPlayer() ? this.getUniqueId() : null;

		String name = player.getName();

		UUID uuid = player.getUniqueId();

		Tasks.runAsync(() ->
		{
			boolean chatEnabled = Hooks.DATA.getPlayerDataAsBoolean(uuid, "chat.show");

			boolean ignoresYou = senderUuid == null ? false : Hooks.DATA.getPlayerDataAsStringList(uuid, "chat.ignore").contains(senderUuid);

			long playtime = Hooks.DATA.getPlayerDataAsLong(uuid, "data.playtime");
			if (player.isOnline()) playtime += Metadata.getValueAsTimestamp(player.getPlayer(), "data.playtime").timeSince();

			User user = Hooks.PERMISSIONS.user(uuid);

			Locale.send(sender, "prison.profile", name,
												  org.apache.commons.lang3.StringUtils.capitalize(user.getGroupName()),
												  StringUtils.formatMoneyFully(Hooks.ECON.getMoney(uuid), false),
												  StringUtils.formatNumber(Hooks.ECON.getTokens(uuid)),
												  StringUtils.formatNumber(ModuleSell.get().getMultiplier(user, uuid)),
												  Hooks.DATA.getPlayerDataAsInt(uuid, "data.join.number"), Hooks.DATA.getPlayerDataAsString(uuid, "data.join.date.first"),
												  Hooks.DATA.getPlayerDataAsString(uuid, "data.join.date.last"),
												  TimeUtils.millisecondsToString(playtime),
												  Booleans.fancify(chatEnabled),
												  Booleans.fancify(ignoresYou, "yes", "no"));
		});
	}

}