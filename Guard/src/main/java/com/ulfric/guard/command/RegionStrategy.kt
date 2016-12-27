package com.ulfric.guard.command

import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.command.arg.ArgStrategy

object RegionStrategy : ArgStrategy<Region>
{
	override fun match(string: String): Region?
	{
		return RegionColl.getRegionByName(string)
	}
}
