package com.ulfric.guard

import com.ulfric.guard.coll.ModuleRegionColl
import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.command.CommandGuard
import com.ulfric.guard.data.DataValueBoolean
import com.ulfric.guard.data.DataValueInteger
import com.ulfric.guard.data.DataValueStringList
import com.ulfric.guard.region.Cuboid
import com.ulfric.guard.region.Cylinder
import com.ulfric.guard.region.Flags
import com.ulfric.guard.region.Omnipresent
import com.ulfric.guard.region.Point
import com.ulfric.guard.region.Polygon2D
import com.ulfric.guard.region.Region
import com.ulfric.guard.region.Sphere
import com.ulfric.guard.selection.CuboidSelection
import com.ulfric.guard.selection.Selection
import com.ulfric.lib.api.hook.Hooks
import com.ulfric.lib.api.java.Strings
import com.ulfric.lib.api.locale.Locale
import com.ulfric.lib.api.module.Plugin
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.io.File
import java.util.UUID

class Guard : Plugin()
{
	val selections = mutableMapOf<UUID, Selection>()

	fun getRegionFile(region: Region): File
	{
		return File(Guard.get().dataFolder, "regions/${region.world}/${region.name}.yml")
	}

	fun getSelection(player: Player): Selection
	{
		return selections.getOrPut(player.uniqueId) { CuboidSelection() }
	}

	fun setSelection(player: Player, selection: Selection)
	{
		selections[player.uniqueId] = selection
	}

	override fun load()
	{
		ConfigurationSerialization.registerClass(Cuboid::class.java)
		ConfigurationSerialization.registerClass(Cylinder::class.java)
		ConfigurationSerialization.registerClass(Omnipresent::class.java)
		ConfigurationSerialization.registerClass(Point::class.java)
		ConfigurationSerialization.registerClass(Polygon2D::class.java)
		ConfigurationSerialization.registerClass(Sphere::class.java)
		ConfigurationSerialization.registerClass(Region::class.java)
		ConfigurationSerialization.registerClass(DataValueBoolean::class.java)
		ConfigurationSerialization.registerClass(DataValueInteger::class.java)
		ConfigurationSerialization.registerClass(DataValueStringList::class.java)

		withSubModule(ModuleRegionColl())
		registerHook(Hooks.REGIONS, GuardHook)

		addCommand("guard", CommandGuard())
		addListener(object : Listener
					{
						@EventHandler
						fun onQuit(event: PlayerQuitEvent)
						{
							selections.remove(event.player.uniqueId)
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onSelect(event: PlayerInteractEvent)
						{
							val location = event.clickedBlock?.location ?: return
							if (event.player.itemInHand?.type != Material.STICK) return
							if (!event.player.hasPermission("guard.create")) return

							if (event.action == Action.LEFT_CLICK_BLOCK)
							{
								getSelection(event.player).pushLeft(location)
								event.player.sendMessage("First point set to ${location.world.name}(${location.blockX}, ${location.blockY}, ${location.blockZ})")
								event.isCancelled = true
							}
							else if (event.action == Action.RIGHT_CLICK_BLOCK)
							{
								getSelection(event.player).pushRight(location)
								event.player.sendMessage("Second point set to ${location.world.name}(${location.blockX}, ${location.blockY}, ${location.blockZ})")
								event.isCancelled = true
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onList(event: PlayerInteractEvent)
						{
							if (event.action != Action.RIGHT_CLICK_BLOCK) return
							val location = event.clickedBlock?.location ?: return

							if (event.player.itemInHand?.type != Material.LEATHER) return
							if (!event.player.hasPermission("guard.list")) return

							event.isCancelled = true

							val regions = RegionColl.at(location)
							val message = ComponentBuilder(Locale.getMessage(event.player, "guard.regions"))

							if (regions.size < 2)
							{
								regions.forEach { message.appendRegion(it) }
							}
							else
							{
								var count = 0
								for (region in regions.asSequence().take(regions.size - 1)) {
									if (++count > 1) message.append(", ").reset().color(ChatColor.YELLOW)

									message.appendRegion(region)
								}

								message.append(" and ").reset().color(ChatColor.YELLOW)
								message.appendRegion(regions.last())
							}

							message.append("\n${ChatColor.GOLD}${ChatColor.BOLD}Effective Flags: ${ChatColor.GRAY}\n").reset()

							val allFlags = Flags.getFlags()
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

								val data = RegionColl.lookupFlag(flag, regions)
								message.append(" - ").color(ChatColor.YELLOW)
								message.append(flag.name.toLowerCase()).color(ChatColor.GRAY)
								message.append(": ").color(ChatColor.YELLOW)

								val msg = data.dataAsString
								if (data is DataValueBoolean)
								{
									message.append(msg).color(if (data.data) ChatColor.GREEN else ChatColor.RED)
								}
								else if (msg.isBlank())
								{
									message.append("empty").color(ChatColor.ITALIC)
								}
								else
								{
									message.append(data.dataAsString).color(ChatColor.RESET)
								}
							}

							event.player.sendMessage(*message.create())
						}
					})
	}

	companion object
	{
		private val REGION_HOVER = "${ChatColor.LIGHT_PURPLE}Click to view info for {0}!"

		@JvmStatic
		fun get(): Guard
		{
			return Bukkit.getPluginManager().getPlugin("Guard") as Guard
		}

		private fun ComponentBuilder.appendRegion(region: Region): ComponentBuilder
		{
			append(region.name).color(ChatColor.GRAY)
			event(HoverEvent(HoverEvent.Action.SHOW_TEXT,
							 TextComponent.fromLegacyText(Strings.format(REGION_HOVER, region.name))))
			event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/guard info ${region.name}"))

			return this
		}
	}
}
