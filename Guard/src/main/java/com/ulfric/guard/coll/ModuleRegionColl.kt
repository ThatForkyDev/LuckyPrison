package com.ulfric.guard.coll

import com.ulfric.guard.Guard
import com.ulfric.guard.data.DataValueBoolean
import com.ulfric.guard.data.DataValueInteger
import com.ulfric.guard.data.DataValueStringList
import com.ulfric.guard.event.PlayerEnterRegionEvent
import com.ulfric.guard.event.PlayerExitRegionEvent
import com.ulfric.guard.region.Flag
import com.ulfric.guard.region.Flags
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.locale.Locale
import com.ulfric.lib.api.location.LocationUtils
import com.ulfric.lib.api.module.SimpleModule
import com.ulfric.lib.api.player.PlayerDamageEvent
import com.ulfric.lib.api.player.PlayerMoveBlockEvent
import com.ulfric.lib.api.server.Events
import com.ulfric.lib.api.teleport.EntityTeleportTaskBuildEvent
import com.ulfric.uspigot.event.server.ExplodeEvent
import org.apache.commons.collections4.map.CaseInsensitiveMap
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerBucketFillEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import java.nio.file.Files

class ModuleRegionColl : SimpleModule("region-coll", "Region collection module", "Amaranth and Packet", "1.0.0")
{
	override fun postEnable()
	{
		val flagWeight = Flag("weight", 0, DataValueInteger("weight", 0))
		val flagBreak = Flag("break", 1, DataValueInteger("break", 1))
		val flagPlace = Flag("place", 2, DataValueInteger("place", 1))
		val flagInvincible = Flag("invincible", 3, DataValueBoolean("invincible", false))
		val flagFallDamage = Flag("fall-damage", 4, DataValueBoolean("fall-damage", true))
		val flagHunger = Flag("hunger", 5, DataValueBoolean("hunger", false))
		val flagInstaPort = Flag("instaport", 6, DataValueBoolean("instaport", true))
		val flagExplode = Flag("explode", 7, DataValueBoolean("explode", false))
		val flagBlockedCommands = Flag("blocked-commands", 8, DataValueStringList("blocked-commands", emptyList()))
		val flagFlight = Flag("flight", 9, DataValueBoolean("flight", true))
		val flagFire = Flag("fireSpread", 10, DataValueBoolean("fireSpread", false))

		Flags.registerFlag(flagWeight)
		Flags.registerFlag(flagBreak)
		Flags.registerFlag(flagPlace)
		Flags.registerFlag(flagInvincible)
		Flags.registerFlag(flagFallDamage)
		Flags.registerFlag(flagInstaPort)
		Flags.registerFlag(flagExplode)
		Flags.registerFlag(flagBlockedCommands)
		Flags.registerFlag(flagFlight)
		Flags.registerFlag(flagFire)

		addListener(object : Listener
					{
						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onMove(event: PlayerMoveBlockEvent)
						{
							val toRegions = RegionColl.at(LocationUtils.getExact(event.to))
							val fromRegions = RegionColl.at(LocationUtils.getExact(event.from))

							if (toRegions.isEmpty() && !fromRegions.isEmpty())
							{
								val exit = PlayerExitRegionEvent(event.player, event.from.block, fromRegions)
								if (Events.call(exit).isCancelled)
								{
									event.isCancelled = true
									return
								}
							}
							else if (fromRegions.isEmpty() && !toRegions.isEmpty())
							{
								val enter = PlayerEnterRegionEvent(event.player, event.to.block, toRegions)
								if (Events.call(enter).isCancelled)
								{
									event.isCancelled = true
									return
								}
							}

							if (!fromRegions.isEmpty())
							{
								val exit = PlayerExitRegionEvent(event.player, event.from.block, fromRegions)
								if (Events.call(exit).isCancelled)
								{
									event.isCancelled = true
									return
								}
							}

							if (!toRegions.isEmpty())
							{
								val enter = PlayerEnterRegionEvent(event.player, event.to.block, toRegions)
								if (Events.call(enter).isCancelled)
								{
									event.isCancelled = true
									return
								}
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onMoveBlock(event: PlayerMoveBlockEvent)
						{
							handleMovement(event)
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onTeleport(event: PlayerTeleportEvent)
						{
							handleMovement(event)
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onFly(event: PlayerToggleFlightEvent)
						{
							if (event.isFlying)
							{
								handleFlight(event.player, RegionColl.at(event.player.location))
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onCommand(event: PlayerCommandPreprocessEvent)
						{
							val player = event.player

							if (player.hasPermission("guard.bypass.commands")) return

							val data = RegionColl.flagsAt(flagBlockedCommands, player.location)
							if (data.isEmpty()) return

							var command = event.message.substring(1).split("\\s+")[0]
							command = Bukkit.getPluginCommand(command)?.let { it.name } ?: command
							command = command.toLowerCase()

							for (flagData in data)
							{
								if (flagData.data.contains(command))
								{
									Locale.sendError(player, "guard.flag_commandsblocked_denied")
									event.isCancelled = true

									return
								}
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onExplode(event: EntityExplodeEvent)
						{
							handleExplosion(event)
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onExplode(event: BlockExplodeEvent)
						{
							handleExplosion(event)
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onFutureTeleport(event: EntityTeleportTaskBuildEvent)
						{
							val flag = RegionColl.flagAt(flagInstaPort, event.task.startingLocation)
							if (flag.data)
							{
								event.setDelay(0)
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onFood(event: FoodLevelChangeEvent)
						{
							val entity = event.entity

							if (entity !is Player) return
							if (entity.foodLevel < event.foodLevel) return

							val regions = RegionColl.at(entity.location)
							if (handleInvincible(event, regions))
							{
								event.isCancelled = false
								event.foodLevel = 20
								return
							}

							val flag = RegionColl.lookupFlag(flagHunger, regions)
							if (!flag.data)
							{
								event.foodLevel = 20
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
						fun onDamage(event: PlayerDamageEvent)
						{
							val regions = RegionColl.at(event.player.location)

							val invincible = RegionColl.lookupFlag(flagInvincible, regions)
							if (invincible.data)
							{
								event.isCancelled = true
								return
							}

							if (event.cause == EntityDamageEvent.DamageCause.FALL)
							{
								val fallDamage = RegionColl.lookupFlag(flagFallDamage, regions)
								if (!fallDamage.data)
								{
									event.isCancelled = true
									return
								}
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onBreak(event: BlockBreakEvent)
						{
							if (handleBreak(event)) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onPlace(event: BlockPlaceEvent)
						{
							if (handlePlace(BlockBreakEvent(event.blockPlaced, event.player))) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onHangingPlace(event: HangingPlaceEvent)
						{
							if (handlePlace(BlockBreakEvent(event.entity.location.block, event.player))) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onHangingBreak(event: HangingBreakEvent)
						{
							if (event is HangingBreakByEntityEvent)
							{
								val remover = event.remover
								if (remover is Player && handleBreak(BlockBreakEvent(event.entity.location.block, remover)))
								{
									event.isCancelled = true
								}
								else
								{
									val flag = RegionColl.flagAt(flagExplode, event.entity.location)
									if (!flag.data)
									{
										event.isCancelled = true
									}
								}
							}
							else
							{
								val flag = RegionColl.flagAt(flagExplode, event.entity.location)
								if (!flag.data)
								{
									event.isCancelled = true
								}
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onInteractItemFrame(event: PlayerInteractEntityEvent)
						{
							if (event.rightClicked is ItemFrame && handleBreak(BlockBreakEvent(event.rightClicked.location.block, event.player)))
							{
								event.isCancelled = true
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onBucketFill(event: PlayerBucketFillEvent)
						{
							if (handleBreak(BlockBreakEvent(event.blockClicked, event.player))) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onBucketEmpty(event: PlayerBucketEmptyEvent)
						{
							if (handlePlace(BlockBreakEvent(event.blockClicked.getRelative(event.blockFace), event.player))) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onIgnite(event: BlockIgniteEvent)
						{
							val player = event.player
							if (player != null)
							{
								event.isCancelled = handlePlace(BlockBreakEvent(event.block, event.player))
							}
							else
							{
								event.isCancelled = !RegionColl.flagAt(flagFire, event.block.location).data
							}
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onBurn(event: BlockBurnEvent)
						{
							event.isCancelled = !RegionColl.flagAt(flagFire, event.block.location).data
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onChangeBlock(event: EntityChangeBlockEvent)
						{
							val entity = event.entity
							if (entity is Player && handleBreak(BlockBreakEvent(event.block, entity)))
							{
								event.isCancelled = true
							}
						}
						
						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onArmorStandManipulate(event: PlayerArmorStandManipulateEvent)
						{
							if (handlePlace(BlockBreakEvent(event.rightClicked.location.block, event.player))) event.isCancelled = true
						}

						@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
						fun onArmorStandDamage(event: EntityDamageEvent)
						{
							if (event.entity !is ArmorStand) return

							if (event is EntityDamageByEntityEvent)
							{
								val damager = event.damager
								if (damager is Player && handleBreak(BlockBreakEvent(event.entity.location.block, damager)))
								{
									event.isCancelled = true
								}
								else
								{
									val flag = RegionColl.flagAt(flagExplode, event.entity.location)
									if (!flag.data)
									{
										event.isCancelled = true
									}
								}
							}
							else
							{
								val flag = RegionColl.flagAt(flagExplode, event.entity.location)
								if (!flag.data)
								{
									event.isCancelled = true
								}
							}
						}

						private fun handleBreak(event: BlockBreakEvent): Boolean
						{
							val flag = RegionColl.flagAt(flagBreak, event.block.location)
							return flag.data == 1 && !event.player.hasPermission("guard.bypass.break")
						}

						private fun handlePlace(event: BlockBreakEvent): Boolean
						{
							val flag = RegionColl.flagAt(flagPlace, event.block.location)
							return flag.data == 1 && !event.player.hasPermission("guard.bypass.place")
						}

						private fun handleInvincible(event: Cancellable, regions: List<Region>): Boolean
						{
							val flag = RegionColl.lookupFlag(flagInvincible, regions)
							if (flag.data) {
								event.isCancelled = true
								return true
							}

							return false
						}

						private fun handleMovement(event: PlayerMoveEvent)
						{
							val regions = RegionColl.at(event.to)
							if (regions.isNotEmpty())
							{
								handleFlight(event.player, regions)
								//handleAction(event.player, event, regions)
							}
						}

						private fun handleFlight(player: Player, regions: List<Region>)
						{
							if (!player.allowFlight) return
							if (player.hasPermission("guard.bypass.flight")) return

							val flag = RegionColl.lookupFlag(flagFlight, regions)
							if (flag.data) return

							player.allowFlight = false
						}

						private fun handleExplosion(event: ExplodeEvent)
						{
							// TODO: Implement ExplodeEvent.getOrigin()
							/*
							var flag = RegionColl.flagAt(flagExplode, event.getOrigin())
							if (flag != null && !flag.getData())
							{
								event.blockList().clear()
								event.isCancelled = true

								return
							}
							*/

							event.blockList().removeAll { !RegionColl.flagAt(flagExplode, it.location).data }
							if (event.blockList().isEmpty())
							{
								event.isCancelled = true
							}
						}
					})

		RegionColl.IMPL = object : RegionColl.IRegionColl
		{
			val regionsMap = CaseInsensitiveMap<String, RegionContainer>()
			val regionsMapByName = CaseInsensitiveMap<String, Region>()

			init
			{
				var counter = 0

				Files.list(Guard.get().dataFolder.toPath().resolve("regions")).filter { Files.isDirectory(it) }
						.forEach {
							Files.list(it).filter { Files.isRegularFile(it) && it.toString().endsWith(".yml") }
							.forEach {
								val config = YamlConfiguration.loadConfiguration(it.toFile())
								if (config != null)
								{
									val region = config["region"] as Region?
									if (region != null)
									{
										registerRegion(region, false)
										counter++
									}
								}
							}
						}

				log("Loaded $counter regions")
			}

			override fun at(location: Location): List<Region>
			{
				val container = regionsMap[location.world.name] ?: return emptyList()
				return container.at(location)
			}

			override fun clear()
			{
				regionsMap.clear()
				regionsMapByName.clear()
			}

			override fun getRegionByName(name: String): Region?
			{
				return regionsMapByName[name]
			}

			override fun registerRegion(region: Region): Boolean
			{
				return registerRegion(region, true)
			}

			override fun removeRegion(region: Region)
			{
				val contained = regionsMapByName.remove(region.name)
				if (region !== contained) return

				for ((key, value) in regionsMap)
				{
					if (!value.remove(region)) continue
					break
				}

				FileUtils.deleteQuietly(Guard.get().getRegionFile(region))
			}

			override fun saveRegion(region: Region)
			{
				val config = YamlConfiguration()
				config["region"] = region
				config.save(Guard.get().getRegionFile(region))
			}

			private fun registerRegion(region: Region, save: Boolean): Boolean
			{
				if (regionsMapByName.containsKey(region.name)) return false
				regionsMapByName.put(region.name, region)

				regionsMap.getOrPut(region.world, { RegionContainer(region.world) }).put(region)

				if (save) saveRegion(region)

				return true
			}
		}
	}

	override fun postDisable()
	{
		RegionColl.IMPL.clear()
		RegionColl.IMPL = object : RegionColl.IRegionColl
		{}
	}
}
