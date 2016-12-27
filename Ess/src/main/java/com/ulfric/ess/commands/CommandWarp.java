package com.ulfric.ess.commands;

import com.ulfric.ess.commands.arg.WarpStrategy;
import com.ulfric.ess.configuration.ConfigurationStore;
import com.ulfric.ess.entity.Warp;
import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PermissionUtils;
import com.ulfric.lib.api.teleport.TeleportUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class CommandWarp extends SimpleCommand {

	public CommandWarp()
	{
		this.withArgument("warp", WarpStrategy.INSTANCE);
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, Permissions.WARP_OTHERS);
	}

	@Override
	public void run()
	{
		if (!this.hasArgs())
		{
			ComponentBuilder message = new ComponentBuilder(Locale.getMessage(this.getSender(), "ess.warps"));

			String click = Locale.getMessage(this.getSender(), "ess.warp_click");
			boolean first = true;
			for (Warp lwarp : ConfigurationStore.get().getWarps())
			{
				String name = lwarp.getName();

				if (!PermissionUtils.hasNestedAccess(this.getSender(), "ess.warp", name)) continue;

				if (!first)
				{
					message.append(", ").color(ChatColor.YELLOW);
				}
				else
				{
					first = false;
				}

				message.append(name)
					   .color(ChatColor.GRAY)
					   .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
											 TextComponent.fromLegacyText(Strings.format(click, name))))
					   .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp " + name));
			}

			this.getSender().sendMessage(message.create());

			return;
		}

		Warp warp = (Warp) this.getObject("warp");

		if (warp == null)
		{
			Locale.sendError(this.getPlayer(), "ess.warp_not_found");

			return;
		}

		Player target = (Player) this.getObject("player");

		if (this.isPlayer() && !target.equals(this.getPlayer()) && !this.hasPermission(Permissions.WARP_OTHERS))
		{
			Locale.sendError(this.getPlayer(), "ess.teleport_others_err");

			return;
		}

		if (!PermissionUtils.hasNestedAccess(this.getSender(), "ess.warp", warp.getName().toLowerCase()))
		{
			Locale.sendError(this.getPlayer(), "ess.warp_no_access");

			return;
		}

		TeleportUtils.teleport(target, warp.getLocation(), warp.getWarmup());
	}

}
