package com.ulfric.guard.event

import com.ulfric.guard.region.Region
import com.ulfric.uspigot.event.player.CancellablePlayerEvent
import org.bukkit.block.Block
import org.bukkit.entity.Player

abstract class RegionEvent(player: Player, val block: Block, val regions: List<Region>) : CancellablePlayerEvent(player)
