package com.ulfric.lib.api.chat;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.collect.CollectUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.lib.api.time.Timestamp;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.List;

public final class ChatModule extends SimpleModule {


	public ChatModule()
	{
		super("chat", "Chat utilities module", "Packet", "1.0.0-REL");

		this.withSubModule(new ChatCommandModule());
	}

	@Override
	public void postEnable()
	{
		Chat.impl = new Chat.IChat() {
			@Override
			public String color(String message)
			{
				return ChatColor.translateAlternateColorCodes('&', message);
			}

			@Override
			public String linePercentage(Number number, Number total, char bar, int bars, ChatColor used, ChatColor available, ChatColor full, boolean showPercentile)
			{
				int opercent = (int) Numbers.percentage(number, total);
				int percent = opercent;
				if (percent < 100) percent /= (100 / bars);

				StringBuilder builder = new StringBuilder();

				if (opercent >= 100)
				{
					builder.append(full);

					for (int x = 0; x < bars; x++)
					{
						builder.append(bar);
					}
				}
				else
				{
					bars -= percent;

					builder.append(used);

					for (int x = 0; x < percent; x++)
					{
						builder.append(bar);
					}

					builder.append(available);

					for (int x = 0; x < bars; x++)
					{
						builder.append(bar);
					}
				}

				if (showPercentile)
				{
					builder.append(' ');

					builder.append(opercent);

					builder.append('%');
				}

				return builder.toString();
			}

			@Override
			public char seperator()
			{
				return '\u00BB';
			}

			@Override
			public Collection<String> color(Collection<String> messages)
			{
				if (CollectUtils.isEmpty(messages)) return messages;

				List<String> translated = Lists.newArrayListWithExpectedSize(messages.size());

				for (String message : messages)
				{
					translated.add(this.color(message));
				}

				return translated;
			}

			@Override
			public String serializeColor(String message)
			{
				return message.replace(ChatColor.COLOR_CHAR, '&');
			}

			@Override
			public String stripBadColor(String message)
			{
				return message.replaceAll("(?i)(&)(0|4|k|l|m|n|o)", Strings.BLANK);
			}

			@Override
			public ChatMessage newChatMessage(Timestamp time, String message)
			{
				return new ChatMessage(time, message);
			}

			@Override
			public IChatBaseComponent asMinecraftText(String message)
			{
				return IChatBaseComponent.ChatSerializer.a(Strings.format("{\"text\": \"{0}\"}", message));
			}

			@Override
			public ChatBook newChatBook(String title, String command, int pageSize)
			{
				return new ChatBook(title, command, pageSize);
			}

			@Override
			public Message newStringMessage(String message)
			{
				return new StringMessage(message);
			}

			@Override
			public Message newLocaleMessage(String message)
			{
				return new LocaleMessage(message);
			}

			@Override
			public String invisibleSpace(int amount)
			{
				StringBuilder builder = new StringBuilder();

				for (int x = 0; x < amount; x++)
				{
					builder.append(ChatColor.RESET);
				}

				return builder.toString();
			}
		};
	}

	@Override
	public void postDisable()
	{
		Chat.impl = Chat.IChat.EMPTY;
	}


}
