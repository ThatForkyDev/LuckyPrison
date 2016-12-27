package com.ulfric.guard.command

import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.data.DataValueBoolean
import com.ulfric.guard.region.Flag
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

class SubCommandFlag(command: Command) : SimpleSubCommand(command, "flag", "meta", "flags")
{
	init
	{
		withIndexUnusedArgs()
		withArgument("region", RegionStrategy, "guard.region_not_found")
		withArgument("flag", FlagStrategy)
	}

	override fun run()
	{
		val region = getObject("region") as Region
		val flag = getObject("flag") as Flag<*>?
		if (flag == null)
		{
			val message = ComponentBuilder(Locale.getMessage(sender, "guard.flags"))
			var first = true
			for (available in Flags.getFlags())
			{
				if (!first)
				{
					message.append(", ").color(ChatColor.YELLOW)
				}
				else
				{
					first = false
				}

				if (available.defaultData is DataValueBoolean)
				{
					val data = (region.getFlag(available)?.data ?: available.defaultData.data) as Boolean
					val hover = Locale.getMessage(sender, if (data) "guard.flag_disable" else "guard.flag_enable")

					message.append(available.name.toLowerCase()).color(if (data) ChatColor.GREEN else ChatColor.RED)
					message.event(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)))
					message.event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guard flag ${region.name} ${available.name} ${!data}"))
				}
				else
				{
					val hover = Locale.getMessage(sender, "guard.flag_not_boolean")
					message.append(available.name.toLowerCase()).color(ChatColor.GRAY)
					message.event(HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover)))
					message.event(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/guard flag ${region.name} ${available.name} "))
				}
			}

			sender.sendMessage(*message.create())
			return
		}

		val data = unusedArgs
		val flagData = region.newFlag(flag)

		if (!flagData.setData(data))
		{
			if (region.removeFlag(flag))
			{
				RegionColl.saveRegion(region)
			}

			Locale.sendSuccess(sender, "guard.flag_cleared", flag.name.toLowerCase())
		}
		else
		{
			RegionColl.saveRegion(region)
			Locale.sendSuccess(sender, "guard.flag_set", flag.name.toLowerCase(), data)
		}
	}
}
