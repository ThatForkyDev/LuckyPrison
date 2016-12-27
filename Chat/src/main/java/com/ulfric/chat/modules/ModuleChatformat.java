package com.ulfric.chat.modules;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.ulfric.chat.channel.enums.ChatChannel;
import com.ulfric.chat.lang.Meta;
import com.ulfric.chat.modules.ModuleTags.ChatTag;
import com.ulfric.chat.modules.ModuleTrends.Trend;
import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.collect.Sets;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.DataHook.IPlayerData;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.hook.PermissionsExHook.Group;
import com.ulfric.lib.api.hook.PermissionsExHook.User;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.player.PlayerUtils;

public class ModuleChatformat extends SimpleModule {

	private final Pattern trendPattern = Pattern.compile("#[a-zA-Z0-9]+");

	public ModuleChatformat()
	{
		super("chatformat", "Chat formatting module", "Packet", "1.0.0-REL");

		this.withSubModule(ModuleTags.INSTANCE);
		this.withSubModule(new ModuleTrends());

		this.withConf();

		this.addListener(new Listener()
		{
			@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
			public void onChat(AsyncPlayerChatEvent event)
			{
				event.setCancelled(true);

				Player player = event.getPlayer();

				IPlayerData data = Hooks.DATA.getPlayerData(player.getUniqueId());

				if (data.getBoolean(Meta.SHOW_CHAT))
				{
					Locale.sendError(player, "chat.disabled");

					return;
				}

				String message = event.getMessage();
				// TODO Move to Control's Agent and act like other violations
				if (!player.hasPermission("control.bypass.chat"))
				{
					int capitalCount = 0;
					for (int i = 0; i < message.length(); i++)
					{
						if (!Character.isUpperCase(message.charAt(i))) continue;

						capitalCount++;
					}
					if (capitalCount > 6)
					{
						message = message.toLowerCase();
						event.setMessage(message);
					}
				}
				// TODO End

				User user = Hooks.PERMISSIONS.user(event.getPlayer());

				String mainGroup;

				List<String> parents = user == null ? ImmutableList.of() : user.getParentIdentifiers();
				if (parents.isEmpty())
				{
					mainGroup = "default";
				}
				else
				{
					mainGroup = parents.get(0).trim();

					if (mainGroup.isEmpty())
					{
						mainGroup = "default";
					}
				}

				String group = Strings.BLANK;

				Group mine = user == null ? null : user.getRankLadderGroup("mines");
				Group premium = user == null ? null : user.getRankLadderGroup("premium");

				if (mine != null) group = mine.getPrefix();
				if (premium != null) group = Strings.format("{0} {1}", group, premium.getPrefix());

				if (group.isEmpty()) group = mainGroup;

				else group = Chat.color(group.trim());

				if (player.hasPermission("chat.color"))
				{
					if (!player.hasPermission("chat.color.bypass")) message = Chat.stripBadColor(message);

					message = Chat.color(message);
				}

				if (player.hasPermission("chat.extras"))
				{
					message = message.replace(Strings.FAKE_LINEBREAK, "\n");
				}

				char colorChar = data.getChar("chat.color");
				ChatColor pcolor = ChatColor.WHITE;
				if (colorChar != '\0')
				{
					pcolor = ChatColor.getByChar(colorChar);
					message = pcolor + message;
				}

				Set<Trend> trends = ImmutableSet.of();

				UUID uuid = player.getUniqueId();

				if (ModuleTrends.enabled && message.contains("#")) trends:
				{
					// TODO
					Matcher matcher = ModuleChatformat.this.trendPattern.matcher(message);

					if (!matcher.find()) break trends;

					trends = Sets.newHashSet();

					StringBuffer buffer = new StringBuffer();

					do
					{
						String trendName = matcher.group();

						Trend trend = ModuleTrends.Trend.of(trendName);

						trends.add(trend);

						matcher.appendReplacement(buffer, ChatColor.YELLOW + trendName + pcolor);
					}
					while (matcher.find());

					matcher.appendTail(buffer);

					message = buffer.toString();
				}

				trends.forEach(trend ->
				{
					trend.add(uuid);

					trend.see(uuid);
				});

				String result = ModuleChatformat.this.getFormat(mainGroup).replace("<group>", group).replace("<message>", message);

				if (result.contains("<tag>")) tag:
				{
					String tagId = data.getString(Meta.TAG);

					if (tagId == null || tagId.isEmpty() || tagId.equals("none"))
					{
						result = result.replace("<tag>", Strings.BLANK);

						break tag;
					}

					ChatTag tag = ModuleTags.INSTANCE.getTag(tagId);

					if (tag == null)
					{
						result = result.replace("<tag>", Strings.BLANK);

						break tag;
					}

					result = result.replace("<tag>", ' ' + tag.getText());
				}

				String name = player.getName();

				if (player.hasMetadata(Meta.CHAT_CHANNEL))
				{
					ChatChannel channel = Metadata.getValue(player, Meta.CHAT_CHANNEL);

					String output = Strings.formatF("{0}[{1}] {2}: {3}", channel.equals(ChatChannel.OPERATOR) ? ChatColor.LIGHT_PURPLE : ChatColor.AQUA, channel, name, message);

					for (Player target : PlayerUtils.getOnlinePlayersWithPermission("channel." + channel.name().toLowerCase(), true))
					{
						target.sendMessage(output);
					}

					com.ulfric.chat.Chat.get().log(Strings.format("[CHANNEL-{0}] {1}", channel, output));

					return;
				}

				Set<Player> recipients = event.getRecipients();

				recipients.remove(player);

				player.sendMessage(Chat.color(result.replace(Strings.PLAYER, (data.getBoolean(Meta.SHOW_SELF_AS_BOLD) ? ChatColor.BOLD : Strings.BLANK) + (!data.getBoolean(Meta.SHOW_NICKNAMES) ? player.getDisplayName() : name) + ChatColor.getLastColors(result.split("<player>")[0]))));

				if (!player.hasPermission("chat.immutable"))
				{
					Iterator<Player> iterator = recipients.iterator();

					while (iterator.hasNext())
					{
						if (!Hooks.DATA.getPlayerDataAsStringList(iterator.next().getUniqueId(), Meta.IGNORE).contains(uuid.toString())) continue;

						iterator.remove();
					}
				}

				for (Player recipient : recipients)
				{
					UUID recipientUuid = recipient.getUniqueId();
					String tempresult = result;
					if (!Hooks.DATA.getPlayerDataAsBoolean(recipientUuid, Meta.NOTIFICATIONS)) notifications: 
					{
						String tag = Strings.format(Locale.getMessage(recipient, "chat.at"), recipient.getName());

						if (!tempresult.toLowerCase().contains(tag.toLowerCase())) break notifications;

						String[] resultSplit = tempresult.split("(?i)" + tag);

						StringBuilder builder = new StringBuilder();

						String red = ChatColor.RED + tag.substring(1, tag.length());

						String lastColor = ChatColor.getLastColors(resultSplit[0]);
						int length = resultSplit.length;

						for (int x = 0; x < length; x++)
						{
							String string = resultSplit[x];
							builder.append(string);
							if (x != length-1)
							{
								builder.append(red);
							}
							String color = ChatColor.getLastColors(string);
							if (color.isEmpty()) color = lastColor;
							builder.append(color);
						}

						if (length == 1)
						{
							builder.append(red);
						}

						tempresult = builder.toString();

						recipient.playSound(recipient.getEyeLocation(), Sound.NOTE_PLING, 5F, 5.5F);
					}

					recipient.sendMessage(tempresult.replace("<player>", !Hooks.DATA.getPlayerDataAsBoolean(recipientUuid, Meta.SHOW_NICKNAMES) ? player.getDisplayName() : name));

					trends.forEach(trend -> trend.see(recipientUuid));
				}

				ModuleChatformat.this.getOwningPlugin().log(Strings.format("[{0}] {1}: {2}", mainGroup, name, event.getMessage()));
			}
		});
	}

	private Map<String, String> formats;
	public String getFormat(String format)
	{
		format = this.formats.get(format.toLowerCase());

		if (format == null) return Strings.formatF("&c***FORMAT NOT FOUND*** &7<player> &8{0}&7 <message>", Chat.seperator());

		return format;
	}

	@Override
	public void postEnable()
	{
		this.formats = Maps.newHashMap();

		FileConfiguration conf = this.getConf().getConf();

		for (String key : conf.getKeys(false))
		{
			this.formats.put(key, Chat.color(conf.getString(key)));
		}
	}

}