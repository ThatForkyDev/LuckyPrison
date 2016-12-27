package com.ulfric.control.commands;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;

public class CommandUuidof extends SimpleCommand {

	public CommandUuidof()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");
		String uuid = player.getUniqueId().toString();

		String text = Strings.format(Locale.getMessage(this.getSender(), "control.uuid"), player.getName());
		String tooltip = Locale.getMessage(this.getSender(), "control.click_to_copy");

		ComponentBuilder message = new ComponentBuilder(text);

		message.append(uuid).color(ChatColor.GRAY);
		message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(tooltip)));
		message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, uuid));

		this.getSender().sendMessage(message.create());
	}
}
