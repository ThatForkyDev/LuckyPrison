package com.ulfric.control.listeners;

import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.entity.Punishment;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.control.lang.Meta;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.time.TimeUtils;

public class ListenerConnection implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();

		UUID uuid = player.getUniqueId();

		if (Hooks.DATA.getPlayerDataAsBoolean(uuid, "control.slay"))
		{
			EntityUtils.kill(player);

			Hooks.DATA.setPlayerData(uuid, "control.slay", false);
		}

		if (this.banMsg(event, PunishmentCache.getValidPunishments(player, PunishmentType.BAN))) return;

		Metadata.applyNull(player, Meta.HAS_NOT_MOVED);
	}

	private boolean banMsg(PlayerLoginEvent event, Set<Punishment> punishments)
	{
		if (CollectUtils.isEmpty(punishments)) return false;

		Player player = event.getPlayer();

		for (Punishment punishment : punishments)
		{
			event.setKickMessage(Strings.format(Locale.getMessage(player, punishment.getType().getMessage()), punishment.getId(), punishment.getReason().replace(Strings.PLAYER, player.getName()), TimeUtils.millisecondsToString(punishment.timeTillExpiry()), punishment.getSender().getName(), punishment.getDate()));

			event.setResult(Result.KICK_BANNED);

			return true;
		}

		return false;
	}

}