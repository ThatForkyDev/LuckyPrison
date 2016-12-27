package com.ulfric.guard.command

import com.ulfric.guard.data.DataValueBoolean
import com.ulfric.guard.region.Flags
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.command.Command
import com.ulfric.lib.api.command.SimpleSubCommand
import com.ulfric.lib.api.locale.Locale
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent

class SubCommandInfo(command: Command) : SimpleSubCommand(command, "info")
{
	init
	{
		this.withIndexUnusedArgs()
		this.withArgument("region", RegionStrategy, "guard.region_not_found")
	}

	override fun run()
	{
		val region = this.getObject("region") as Region
		val allFlags = Flags.getFlags()

		val message = ComponentBuilder("${ChatColor.GOLD}${ChatColor.BOLD}Region: ${ChatColor.GRAY}${region.name}\n")
		var first = true

		for (flag in allFlags)
		{
			if (!first)
			{
				message.append("\n")
			}
			else
			{
				first = false
			}

			val data = region.getFlag(flag) ?: flag.defaultData
			message.reset()
			message.append(" - ").color(ChatColor.YELLOW)
			message.append(flag.name.toLowerCase()).color(ChatColor.GRAY)
			message.append(": ").color(ChatColor.YELLOW)

			val msg = data.dataAsString
			if (data is DataValueBoolean)
			{
				val hover = Locale.getMessage(sender, if (data.data) "guard.flag_disable" else "guard.flag_enable")
				message.append(msg).color(if (data.data) ChatColor.GREEN else ChatColor.RED)
				message.event(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)))
				message.event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guard flag ${region.name} ${flag.name} ${!data.data}"))
			}
			else
			{
				if (msg.isBlank())
				{
					message.append("empty").color(ChatColor.ITALIC)
				}
				else
				{
					message.append(data.dataAsString).color(ChatColor.RESET)
				}

				val hover = Locale.getMessage(sender, "guard.flag_not_boolean")
				message.event(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)))
				message.event(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/guard flag ${region.name} ${flag.name} "))
			}
		}

		sender.sendMessage(*message.create())
	}
}
