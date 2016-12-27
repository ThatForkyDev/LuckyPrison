package com.ulfric.control.modules;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ulfric.control.coll.IPCache;
import com.ulfric.control.entity.IPData;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.control.entity.enums.ViolationType;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.time.Milliseconds;

public class ModuleIPLimit extends SimpleModule {

	public ModuleIPLimit()
	{
		super("ip-limit", "IP Limiter & caching module", "Packet", "1.0.0-REL");
	}

	@Override
	public void onFirstEnable()
	{
		this.addListener(new Listener()
		{
			@EventHandler(priority = EventPriority.LOW)
			public void onLogin(PlayerLoginEvent event)
			{
				Player player = event.getPlayer();

				UUID uuid = player.getUniqueId();

				String ip = StringUtils.formatIP(event.getAddress().getHostAddress());

				IPData data = IPCache.INSTANCE.getData(ip);
				if (data == null)
				{
					data = new IPData(player, ip);

					IPCache.INSTANCE.add(data);
				}
				else
				{
					data.addPlayer(player);
				}

				Hooks.DATA.addToPlayerDataCollectionIfAbsent(uuid, "control.ips", ip);

				if (data.getLastLogin() != null && data.getLastLogin().timeSince() < Milliseconds.fromSeconds(6.3))
				{
					data.incrementViolation(ViolationType.RAPID_LOGIN, PunishmentType.BAN);
				}

				data.getLastLogin().setTimeNow();

				if (data.count() < 5) return;

				if (data.secureCount() < 5) return; // TODO remove after this is working 100%

				StringBuilder builder = new StringBuilder('\n');
				for (Player entry : data)
				{
					builder.append(entry.getName());
					builder.append(", ");
				}

				String players = builder.toString().trim();
				players = players.substring(0, players.length() - 1);

				event.setKickMessage(Strings.format(Locale.getMessage(player, "control.already_logged_in"), players));
				event.setResult(Result.KICK_OTHER);
				data.incrementViolation(ViolationType.MAX_LOGIN);
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent event)
			{
				Player player = event.getPlayer();

				IPData data = IPCache.INSTANCE.getData(player);

				if (data == null) return;

				data.removePlayer(player);
			}
		});
	}

}