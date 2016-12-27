package com.ulfric.guard.coll

import com.ulfric.guard.region.Omnipresent
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.java.Named
import org.bukkit.util.Vector
import java.util.function.Consumer

class RegionContainer(private val name: String) : Named
{
	val spatialHash = SpatialHash<Region>(100)
	val globalRegions = hashSetOf<Region>()

	override fun getName(): String
	{
		return name
	}

	fun at(vector: Vector): List<Region>
	{
		val regions = globalRegions.toMutableList()
		spatialHash.hitTest(vector, Consumer { regions.add(it) })

		regions.sort()
		return regions
	}

	fun put(region: Region)
	{
		when (region.shape)
		{
			is Omnipresent -> globalRegions.add(region)
			else -> spatialHash.put(region.shape, region)
		}
	}

	fun remove(region: Region): Boolean
	{
		if (globalRegions.remove(region))
		{
			return true
		}
		else
		{
			return spatialHash.remove(region.shape, region)
		}
	}
}
