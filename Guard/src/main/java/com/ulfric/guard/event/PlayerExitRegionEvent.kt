package com.ulfric.guard.event

import com.ulfric.guard.region.Region
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class PlayerExitRegionEvent(player: Player, block: Block, region: List<Region>) : RegionEvent(player, block, region)
{
	override fun getHandlers(): HandlerList
	{
		return handlerList
	}

	companion object
	{
		@JvmStatic
		val handlerList = HandlerList()
	}
}
