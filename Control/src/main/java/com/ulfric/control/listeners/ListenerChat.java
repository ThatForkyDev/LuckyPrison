package com.ulfric.control.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.ulfric.control.coll.IPCache;
import com.ulfric.control.coll.PunishmentCache;
import com.ulfric.control.commands.CommandLockdown;
import com.ulfric.control.commands.CommandSpamfilter;
import com.ulfric.control.entity.IPData;
import com.ulfric.control.entity.PunishmentHolder;
import com.ulfric.control.entity.enums.PunishmentType;
import com.ulfric.control.entity.enums.ViolationType;
import com.ulfric.control.lang.Meta;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.chat.ChatMessage;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Milliseconds;
import com.ulfric.lib.api.time.TimeUtils;
import com.ulfric.lib.api.time.Timestamp;
import com.ulfric.lib.api.tuple.Pair;
import com.ulfric.lib.api.tuple.Tuples;

public class ListenerChat implements Listener {

	private Pair<Player, ChatMessage> lastMessage;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();

		if (player.hasPermission("control.bypass.chat")) return;

		String currentMessage = event.getMessage();

		ChatMessage message = Chat.newChatMessage(Timestamp.now(), currentMessage);

		Pair<Player, ChatMessage> lastMessage = this.lastMessage;

		this.lastMessage = Tuples.newPair(player, message);

		IPData data = IPCache.INSTANCE.getData(player);

		if (data == null)
		{
			String ip = PlayerUtils.getIP(player);

			data = new IPData(player, ip);

			IPCache.INSTANCE.add(data);
		}

		if (lastMessage != null) spam:
		{
			if (!CommandSpamfilter.isEnabled()) break spam;

			ChatMessage dLastMessage = data.getLastMessage();

			if (dLastMessage != null && dLastMessage.getTime().timeSince() < Milliseconds.fromSeconds(0.9))
			{
				data.incrementViolation(ViolationType.FAST_CHAT_LOCAL);
			}

			if (StringUtils.isSimilar(currentMessage, lastMessage.getB().getMessage()))
			{
				data.incrementViolation(ViolationType.SAME_MESSAGE_GLOBAL);
			}

			if (lastMessage.getB().getTime().timeSince() < Milliseconds.fromSeconds(0.35))
			{
				data.incrementViolation(ViolationType.FAST_CHAT_GLOBAL);
			}

			if (player.hasMetadata(Meta.LAST_CHAT)) fast:
			{
				ChatMessage lastChat = Metadata.getValue(player, Meta.LAST_CHAT);

				if (StringUtils.isSimilar(currentMessage, lastChat.getMessage()))
				{
					data.incrementViolation(ViolationType.SAME_MESSAGE);
				}

				long since = lastChat.getTime().timeSince();

				if (since > Milliseconds.SECOND) break fast;

				Locale.sendError(player, "control.chat_delay", TimeUtils.millisecondsToString(since));

				event.setCancelled(true);

				data.incrementViolation(ViolationType.FAST_CHAT);
			}
		}

		data.setLastMessage(this.lastMessage.getB());

		Metadata.apply(player, Meta.LAST_CHAT, message);

		if (player.hasMetadata(Meta.HAS_NOT_MOVED))
		{
			if (currentMessage.toLowerCase().contains("minechat"))
			{
				Metadata.remove(player, Meta.HAS_NOT_MOVED);
			}
			else
			{
				data.incrementViolation(ViolationType.NO_MOVE_CHAT);
			}
		}

		if (CommandLockdown.lockdown)
		{
			Locale.sendError(player, "control.lockdown_current");

			event.setCancelled(true);

			data.incrementViolation(ViolationType.LOCKDOWN_CHAT);
		}

		if (this.cancel(player, PunishmentHolder.fromUUID(player.getUniqueId()), PunishmentType.MUTE))
		{
			data.incrementViolation(ViolationType.MUTED_CHAT);

			event.setCancelled(true);

			return;
		}

		if (!this.cancel(player, PunishmentHolder.fromIP(player), PunishmentType.MUTE)) return;

		/*if (!this.cancel(player, PunishmentHolder.fromIP(player), PunishmentType.MUTE))
		{
			if (event.isCancelled()) return;

			Set<String> ips = Sets.newHashSet();
			for (String ip : currentMessage.split("\\s+"))
			{
				String[] dparts = ip.replace(',', '.').replaceAll("[^a-zA-Z0-9(\\:)(-)(\\.)]|(http(s)?:)", Strings.BLANK).split("\\:");

				if (dparts.length == 0) continue;

				String domain = dparts[0];

				String[] parts = domain.split("\\:");

				int size = parts.length;

				if (size > 2) continue;

				domain = parts[0].toLowerCase();

				// TODO
				if (ConfigurationControl.INSTANCE.getSafeIps().contains(domain)) continue;
				// END TODO

				boolean cont = false;
				for (String string : ConfigurationControl.INSTANCE.getSafeIps())
				{
					if (domain.equals(string) || string.startsWith(".") && (domain.endsWith(string) || ('.' + domain).equals(string)))
					{
						cont = true;

						break;
					}
				}
				if (cont) continue;

				if (!DomainValidator.getInstance().isValid(domain) || InetAddressValidator.getInstance().isValidInet4Address(domain)) continue;

				if (size == 1)
				{
					ips.add(domain);

					continue;
				}

				ips.add(Strings.format("{0}:{1}", domain, parts[1]));
			}

			if (ips.isEmpty()) return;

			Locale.send(player, "control.ads_check");

			boolean valid = false;

			WebDriver driver = Drivers.make();

			for (String ip : ips) ips:
			{
				driver.get("https://dinnerbone.com/minecraft/tools/status/");

				WebElement element = driver.findElement(By.id("serverAddress"));

				element.sendKeys(ip);
				element.submit();

				try
				{
					Thread.sleep(Milliseconds.SECOND);
				}
				catch (InterruptedException e) { }

				while (true)
				{
					String source = driver.getPageSource();

					if (source.contains("Ping results from"))
					{
						valid = true;

						break ips;
					}

					if (source.contains("Could not check")) break;

					try
					{
						Thread.sleep(Milliseconds.SECOND);
					}
					catch (InterruptedException e) { }
				}
			}

			Drivers.release(driver);

			if (!valid) return;

			data.incrementViolation(ViolationType.ADVERTISING);

			event.setCancelled(true);

			Locale.sendMassPerm("control.bypass.ads", "control.ads_show", message);

			PunishmentExecutor.execute(new Punishment(PunishmentHolder.fromUUID(player.getUniqueId()), PunishmentSender.AGENT, PunishmentType.MUTE, "Automated detection of advertisement", TimeUtils.formatCurrentDay(), Timestamp.infinite(), -1));

			return;
		}*/

		event.setCancelled(true);
		data.incrementViolation(ViolationType.MUTED_CHAT);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();

		if (player.hasPermission("control.bypass.chat")) return;

		if (!this.cancel(player, PunishmentHolder.fromUUID(player.getUniqueId()), PunishmentType.CMUTE) &&
			!this.cancel(player, PunishmentHolder.fromIP(player), PunishmentType.CMUTE)) return;

		event.setCancelled(true);

		event.setMessage("/nullmessage");
	}

	private boolean cancel(Player player, PunishmentHolder holder, PunishmentType type)
	{
		if (CollectUtils.isEmpty(PunishmentCache.getValidPunishments(holder, type))) return false;

		Locale.sendError(player, type.getMessage());

		return true;
	}


}