package com.ulfric.control.commands;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;

public class CommandNameof extends SimpleCommand {

	public CommandNameof()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		OfflinePlayer player = (OfflinePlayer) this.getObject("player");

		String uuid = player.getUniqueId().toString();

		String name = String.format(Locale.getMessage(this.getSender(), "control.name"), uuid);
		BaseComponent[] hover = TextComponent.fromLegacyText(Locale.getMessage(this.getSender(),
																			   "control.click_to_copy"));

		ComponentBuilder message = new ComponentBuilder(name);

		message.append(player.getName()).color(ChatColor.GRAY);
		message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
		message.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, player.getName()));

		this.getSender().sendMessage(message.create());
	}

}
