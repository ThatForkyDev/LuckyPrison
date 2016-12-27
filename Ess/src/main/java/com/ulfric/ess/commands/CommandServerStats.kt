package com.ulfric.ess.commands

import com.ulfric.lib.api.chat.Chat
import com.ulfric.lib.api.command.SimpleCommand
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.lang.management.ManagementFactory

class CommandServerStats : SimpleCommand()
{
	override fun run()
	{
		val green = ChatColor.GREEN
		val red = ChatColor.RED
		val gray = ChatColor.GRAY

		val gc = ManagementFactory.getGarbageCollectorMXBeans()
				.filter { !it.name.contains("Scavenge") }
				.map { it.name.trimStart('P', 'S', ' ') }
				.joinToString(", ")
		val max = Runtime.getRuntime().maxMemory() / 1024 / 1024
		val used = max - (Runtime.getRuntime().freeMemory() / 1024 / 1024)
		val percent = used.toDouble() / max
		val percentStr = "${if (percent < 0.8) green else red}${(percent * 100).toInt()}%"
		val uptime = ManagementFactory.getRuntimeMXBean().uptime.milliSecondsToTimespan()

		this.sender.apply {
			sendMessage(Chat.color("$UPTIME$uptime"))
			sendMessage(Chat.color("$TPS${formatTPS()}"))
			sendMessage(Chat.color("$GC$gc"))
			sendMessage(Chat.color("$MEMORY$percentStr ${progress(percent, 25)} [$red${used}MB$gray/$green${max}MB$gray]"))
		}
	}

	companion object
	{
		// TODO: Put these in locale
		private const val UPTIME = "&6&lUptime:&7 "
		private const val TPS = "&6&lTPS (1m, 5m, 15m):&7 "
		private const val GC = "&6&lGC:&7 "
		private const val MEMORY = "&6&lMemory:&7 "

		fun formatTPS(): String
		{
			val green = ChatColor.GREEN
			val yellow = ChatColor.YELLOW
			val red = ChatColor.RED
			val gray = ChatColor.GRAY

			return Bukkit.getServer().spigot().tps.joinToString(separator = "$gray, ") {
				val color = if (it < 10.0) red else if (it < 15.0) yellow else green
				String.format("$color%.2f", it.coerceAtMost(20.0))
			}
		}

		fun progress(percent: Double, size: Int): String
		{
			val green = ChatColor.GREEN
			val red = ChatColor.RED
			val gray = ChatColor.GRAY

			val filledSize = (size * percent).toInt()
			val filledChars = "|".repeat(filledSize)
			val emptySize = size - filledSize
			val emptyChars = "|".repeat(emptySize)

			return "$gray[$red$filledChars$green$emptyChars$gray]"
		}

		fun Long.milliSecondsToTimespan(): String
		{
			val sb = StringBuffer()
			val diffInSeconds = this / 1000
			val seconds = if (diffInSeconds >= 60) diffInSeconds % 60 else diffInSeconds
			val minutes = if ((diffInSeconds / 60) >= 60) (diffInSeconds / 60) % (60) else diffInSeconds / 60
			val hours = if ((diffInSeconds / 3600) >= 24) (diffInSeconds / 3600) % (24) else diffInSeconds / 3600
			val days = diffInSeconds / 60 / 60 / 24

			if (days > 0)
			{
				sb.append(days)
				sb.append(if (days > 1) " days" else " day")
				sb.append(" and ")
			}

			if (hours > 0 || days > 0)
			{
				sb.append(hours)
				sb.append(if (hours > 1) " hours" else " hour")
				sb.append(" and ")
			}

			if (minutes > 0 || hours > 0 || days > 0)
			{
				sb.append(minutes)
				sb.append(if (minutes > 1) " minutes" else " minute")
				sb.append(" and ")
			}

			sb.append(seconds)
			sb.append(if (seconds > 1) " seconds" else " second")

			return sb.toString()
		}
	}
}
