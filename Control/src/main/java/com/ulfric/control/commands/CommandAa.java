package com.ulfric.control.commands;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.RegexArg;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class CommandAa extends SimpleCommand {

	public CommandAa()
	{
		this.withEnforcePlayer();

		this.withArgument("type", RegexArg.of("(interact|damage|pickup)"));
	}

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		if (!this.hasObjects())
		{
			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(player, "control.actions"));

			this.then(message, player, "interact", false);
			this.then(message, player, "damage", false);
			this.then(message, player, "pickup", true);

			player.sendMessage(message.create());

			return;
		}

		String type = (String) this.getObject("type");
		String path = "aa." + type;

		boolean value = Metadata.removeIfPresent(player, path);

		if (!value)
		{
			Metadata.applyNull(player, path);
		}

		Locale.sendSuccess(player, "control.action_change", type, Booleans.fancify(value, player));
	}

	private void then(ComponentBuilder message, Player player, String label, boolean last)
	{
		message.append(label);
		message.color(player.hasMetadata("aa." + label) ? ChatColor.GREEN : ChatColor.RED);
		message.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/aa " + label));
		message.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									 TextComponent.fromLegacyText(Locale.getMessage(player, "control.action_click"))));
		if (last) return;
		message.append(", ");
		message.color(ChatColor.GRAY);
	}

}
