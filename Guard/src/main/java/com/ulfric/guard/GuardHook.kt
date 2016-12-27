package com.ulfric.guard

import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.hook.CachingEngine
import com.ulfric.lib.api.hook.RegionHook
import com.ulfric.lib.api.hook.WorldEditHook
import org.bukkit.Location
import org.bukkit.util.Vector

object GuardHook : RegionHook.IRegionHook
{
	private var engine: RegionHook.RegionEngine? = null

	override fun buildEngine(cache: Boolean)
	{
		engine = HookEngine(cache)
	}

	override fun clearEngine()
	{
		engine = null
	}

	override fun region(name: String): RegionHook.Region?
	{
		return engine!!.getRegion(name)
	}

	override fun at(location: Location): List<RegionHook.Region>
	{
		return RegionColl.at(location).map { region(it.name) }.filterNotNull()
	}

	class HookEngine(caching: Boolean) : CachingEngine<String, RegionHook.Region>(caching), RegionHook.RegionEngine
	{
		override fun getRegion(name: String): RegionHook.Region?
		{
			val lower = name.toLowerCase()

			if (!isCaching)
			{
				return RegionColl.getRegionByName(lower)?.let { HookRegion(it) }
			}

			var region: RegionHook.Region? = getCached(name)
			if (region != null) return region

			val sregion = RegionColl.getRegionByName(name) ?: return null

			region = HookRegion(sregion)

			cache(name, region)

			return region
		}

	}

	private class HookRegion(private val region: Region) : RegionHook.Region
	{
		override fun getName(): String
		{
			return region.name
		}

		override fun contains(vector: Vector): Boolean
		{
			return region.shape.containsPoint(vector)
		}

		override fun getMin(): Vector
		{
			return region.shape.maxPoint
		}

		override fun getMax(): Vector
		{
			return region.shape.minPoint
		}

		override fun isGlobal(): Boolean
		{
			return region.shape.name.equals("omnipresent")
		}

		override fun iterator(): MutableIterator<Vector>
		{
			return region.shape.iterator() as MutableIterator
		}

		override fun equals(other: Any?): Boolean
		{
			if (this === other) return true
			if (other !is HookRegion) return false

			return other.region == region
		}

		override fun hashCode(): Int
		{
			return region.hashCode()
		}
	}
}
