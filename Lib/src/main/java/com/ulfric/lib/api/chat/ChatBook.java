package com.ulfric.lib.api.chat;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class ChatBook {

	private final String title;
	private final String command;
	private final int pageSize;
	private List<ChatPage> pages;

	ChatBook(String title, String command, int pageSize)
	{
		this.title = title;
		this.command = command;
		this.pageSize = pageSize;
	}

	public void addMessage(Message message)
	{
		if (this.pages == null)
		{
			this.pages = Lists.newArrayList();
		}

		ChatPage page = this.pages.isEmpty() ? null : this.pages.get(this.pages.size() - 1);
		if (page == null || page.isFull())
		{
			page = this.newPage();
			this.addPage(page);
		}

		page.addLine(message);
	}

	public void send(CommandSender sender, int page)
	{
		Assert.isFalse(page < 0, "The page is negative!");

		if (this.pages == null || page >= this.pages.size())
		{
			Locale.sendError(sender, "system.page_cannot_turn");
			return;
		}

		this.pages.get(page).send(sender);

		BaseComponent[] first = TextComponent.fromLegacyText(Locale.getMessage(sender, "system.page_first"));
		BaseComponent[] turn = TextComponent.fromLegacyText(Locale.getMessage(sender, "system.page_turn"));
		BaseComponent[] last = TextComponent.fromLegacyText(Locale.getMessage(sender, "system.page_last"));

		ComponentBuilder message = new ComponentBuilder("");

		if (page == 0)
		{
			message.append("<---").color(ChatColor.GRAY);
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, first));
		}
		else
		{
			message.append("<---").color(ChatColor.GREEN);
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, turn));
			message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										 Strings.format("{0} {1}", this.command, page - 1)));
		}

		message.append(Strings.format(Locale.getMessage(sender, this.title), page + 1));

		if (page + 1 == this.pages.size())
		{
			message.append("--->").color(ChatColor.GRAY);
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, last));
		}
		else
		{
			message.append("--->").color(ChatColor.GREEN);
			message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, turn));
			message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										 Strings.format("{0} {1}", this.command, page + 2)));
		}

		sender.sendMessage(message.create());
	}

	private void addPage(ChatPage page)
	{
		if (this.pages == null)
		{
			this.pages = Lists.newArrayList();
		}

		this.pages.add(page);
	}

	private ChatPage newPage()
	{
		return new ChatPage(this.pageSize);
	}
}
