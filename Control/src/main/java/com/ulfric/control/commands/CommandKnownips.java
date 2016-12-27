package com.ulfric.control.commands;

import com.ulfric.lib.api.command.Argument;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.task.Tasks;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class CommandKnownips extends SimpleCommand {

	public CommandKnownips()
	{
		this.withArgument(Argument.REQUIRED_OFFLINE_PLAYER);
	}

	@Override
	public void run()
	{
		CommandSender sender = this.getSender();

		OfflinePlayer target = (OfflinePlayer) this.getObject("player");
		UUID uuid = target.getUniqueId();

		Locale.send(sender, "control.ip_scan", target.getName());

		Tasks.runAsync(() ->
		{
			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(sender, "control.ips"));

			String click = Locale.getMessage(sender, "control.ips_tooltip");
			for (String ip : Hooks.DATA.getPlayerDataAsStringList(uuid, "control.ips"))
			{
				message.append(ip);
				message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(Strings.format(click, ip))));
				message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/seenip " + ip));
				message.append(", ");
			}

			this.getSender().sendMessage(message.create());
		});
	}

}
