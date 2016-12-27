package com.ulfric.guard.event

import com.ulfric.guard.region.Region
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList

class PlayerEnterRegionEvent(player: Player, block: Block, regions: List<Region>) : RegionEvent(player, block, regions)
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
