package com.ulfric.lib.api.locale;

import com.google.common.collect.Maps;
import com.ulfric.lib.Lib;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.chat.ChatMessage;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;
import com.ulfric.lib.api.time.Timestamp;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class LocaleModule extends SimpleModule {

	public LocaleModule()
	{
		super("locale", "Server localization module", "Packet", "1.0.1-REL");
	}

	@Override
	public void postEnable()
	{
		Locale.impl = new Locale.ILocale() {
			private static final String englishUS = "en_US";
			private final Pattern pattern = Pattern.compile(Strings.format("(i?){0}[a-z\\_\\.]+", Pattern.quote("locale.")));
			private final Set<String> unfound = Sets.newHashSet();
			private Map<String, Map<String, String>> messages;
			private Map<String, String> defaultLocale;

			@Override
			public String englishUS()
			{
				return englishUS;
			}

			@Override
			public void sendAction(Player player, String message, Object... objects)
			{
				PlayerUtils.sendActionBar(player, Strings.format(this.getMessage(player, message), objects).replace(Strings.PLAYER, player.getName()));
			}

			@Override
			public String getLanguage(Player player)
			{
				String locale = player.spigot().getLocale();

				if (locale.equals(englishUS)) return locale;

				return this.messages.containsKey(locale) ? locale : englishUS;
			}

			@Override
			public Map<String, String> getLocale(Player player)
			{
				return this.getLocale(player.spigot().getLocale());
			}

			@Override
			public Map<String, String> getLocale(String locale)
			{
				if (locale.equals(englishUS)) return this.defaultLocale;

				Map<String, String> messages = this.messages.get(locale);

				if (messages == null) return this.defaultLocale;

				return messages;
			}

			@Override
			public String getMessage(String message)
			{
				return this.getMessage(this.defaultLocale, message);
			}

			@Override
			public String getMessage(CommandSender sender, String message)
			{
				if (sender instanceof Player)
				{
					return this.getMessage(this.getLocale((Player) sender), message);
				}

				return this.getMessage(this.defaultLocale, message);
			}

			@Override
			public String getMessage(Player player, String message)
			{
				return this.getMessage(this.getLocale(player), message);
			}

			@Override
			public void sendMass(String message, Object... objects)
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					Locale.send(player, message, objects);
				}
			}

			@Override
			public void sendMassMeta(String meta, String message, Object... objects)
			{
				for (Player player : Bukkit.getOnlinePlayers())
				{
					if (!player.hasMetadata(meta)) continue;

					Locale.send(player, message, objects);
				}
			}

			@Override
			public void sendMassPerm(String permission, String message, Object... objects)
			{
				Locale.sendMassPerm(permission, true, message, objects);
			}

			@Override
			public void sendMassPerm(String permission, boolean has, String message, Object... objects)
			{
				for (Player player : PlayerUtils.getOnlinePlayersWithPermission(permission, has))
				{
					Locale.send(player, message, objects);
				}
			}

			@Override
			public void send(CommandSender sender, String message, Object... objects)
			{
				if (sender instanceof Player)
				{
					Locale.send((Player) sender, message, objects);

					return;
				}

				sender.sendMessage(Strings.format(this.getMessage(message), objects));
			}

			@Override
			public void send(Player player, String message, Object... objects)
			{
				player.sendMessage(Strings.format(this.getMessage(player, message), objects).replace(Strings.PLAYER, player.getName()));
			}

			@Override
			public void sendWarning(CommandSender sender, String message, Object... objects)
			{
				Locale.sendSpecial(sender, "system.danger", message, objects);
			}

			@Override
			public void sendWarning(Player player, String message, Object... objects)
			{
				Locale.sendSpecial(player, "system.danger", message, objects);
			}

			@Override
			public void sendError(CommandSender sender, String message, Object... objects)
			{
				Locale.sendSpecial(sender, "system.error", message, objects);
			}

			@Override
			public void sendError(Player player, String message, Object... objects)
			{
				Locale.sendSpecial(player, "system.error", message, objects);
			}

			@Override
			public String asError(CommandSender sender, String message, Object... objects)
			{
				return this.getSpecial(sender, "system.error", message, objects);
			}

			@Override
			public String asError(Player player, String message, Object... objects)
			{
				return this.getSpecial(player, "system.error", message, objects);
			}

			@Override
			public void sendSuccess(CommandSender sender, String message, Object... objects)
			{
				Locale.sendSpecial(sender, "system.success", message, objects);
			}

			@Override
			public void sendSuccess(Player player, String message, Object... objects)
			{
				Locale.sendSpecial(player, "system.success", message, objects);
			}

			@Override
			public String asSuccess(CommandSender sender, String message, Object... objects)
			{
				return this.getSpecial(sender, "system.success", message, objects);
			}

			@Override
			public String asSuccess(Player player, String message, Object... objects)
			{
				return this.getSpecial(player, "system.success", message, objects);
			}

			@Override
			public String getSpecial(CommandSender sender, String special, String message, Object... objects)
			{
				if (sender instanceof Player)
				{
					return (this.getSpecial((Player) sender, special, message, objects));
				}

				return (this.getSpecial(this.defaultLocale, sender.getName(), special, message, objects));
			}

			@Override
			public String getSpecial(Player player, String special, String message, Object... objects)
			{
				return this.getSpecial(this.getLocale(player), player.getName(), special, message, objects);
			}

			@Override
			public String getSpecial(Map<String, String> locale, String name, String special, String message, Object... objects)
			{
				String prefix = this.getMessage(locale, special);

				String send = this.getMessage(locale, message);

				if (send == null) send = message;

				send = Strings.format(send, objects).replace(Strings.PLAYER, name);

				return Strings.format(prefix, send);
			}

			@Override
			public void sendSpecial(CommandSender sender, String special, String message, Object... objects)
			{
				sender.sendMessage(this.getSpecial(sender, special, message, objects));
			}

			@Override
			public void sendSpecial(Player player, String special, String message, Object... objects)
			{
				player.sendMessage(this.getSpecial(player, special, message, objects));
			}

			@Override
			public void sendTimelock(Player player, String message, long timelock)
			{
				Set<ChatMessage> set = Metadata.getValueAsSet(player, "_ulf_timelockmsg");

				if (set == null)
				{
					set = Sets.newHashSet(Chat.newChatMessage(Timestamp.future(timelock), message));

					Metadata.apply(player, "_ulf_timelockmsg", set);

					player.sendMessage(message);

					return;
				}

				if (set.isEmpty())
				{
					set.add(Chat.newChatMessage(Timestamp.future(timelock), message));

					player.sendMessage(message);

					return;
				}

				Iterator<ChatMessage> iter = set.iterator();
				while (iter.hasNext())
				{
					ChatMessage entry = iter.next();

					if (entry.getTime().hasPassed())
					{
						iter.remove();

						continue;
					}

					if (entry.getMessage().equals(message)) return;

					break;
				}

				player.sendMessage(message);

				set.add(Chat.newChatMessage(Timestamp.future(timelock), message));
			}

			@Override
			public Pattern pattern()
			{
				return this.pattern;
			}

			private Locale.ILocale load()
			{
				File dataFolder = new File(Lib.get().getDataFolder(), "locale");

				if (dataFolder.mkdirs()) return this;

				File[] files = dataFolder.listFiles();

				this.defaultLocale = Maps.newHashMap();

				this.messages = Maps.newHashMapWithExpectedSize(files.length);

				for (File file : files)
				{
					String name = file.getName();

					name = name.substring(0, name.length() - 4);

					FileConfiguration config = YamlConfiguration.loadConfiguration(file);

					Map<String, String> locale = name.equals(englishUS) ? this.defaultLocale : Maps.newHashMap();

					for (String path : config.getKeys(true))
					{
						locale.put(path, Chat.color(config.getString(path)).replace(Strings.FAKE_LINEBREAK, "\n"));
					}

					this.messages.put(name, locale);
				}

				return this;
			}

			private String getMessage(Map<String, String> locale, String message)
			{
				String string = locale.get(message);

				if (string != null) return string;

				string = this.defaultLocale.get(message);

				if (string != null) return string;

				if (this.unfound.add(message))
				{
					LocaleModule.this.log("Invalid locale message '{0}'", message);
				}

				return message;
			}
		}.load();
	}

	@Override
	public void postDisable()
	{
		Locale.impl = Locale.ILocale.EMPTY;
	}

}
