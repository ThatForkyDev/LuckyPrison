package com.ulfric.guard.coll

import com.ulfric.guard.data.DataValue
import com.ulfric.guard.region.Flag
import com.ulfric.guard.region.Region
import org.bukkit.Location

class RegionColl private constructor()
{
	internal interface IRegionColl
	{
		fun at(location: Location): List<Region>
		{
			return emptyList()
		}

		fun clear()
		{

		}

		fun getRegionByName(name: String): Region?
		{
			return null
		}

		fun registerRegion(region: Region): Boolean
		{
			return false
		}

		fun removeRegion(region: Region)
		{

		}

		fun saveRegion(region: Region)
		{

		}
	}

	companion object
	{
		internal var IMPL = object : IRegionColl
		{}

		@JvmStatic
		fun at(location: Location): List<Region>
		{
			return IMPL.at(location)
		}

		@Suppress("UNCHECKED_CAST")
		@JvmStatic
		fun <U, T : DataValue<U>> flagAt(flag: Flag<U>, location: Location): T
		{
			val regions = at(location)
			if (regions.isEmpty()) return flag.defaultData as T

			for (region in regions)
			{
				val data = region.getFlag(flag) ?: continue
				return data as T
			}

			return flag.defaultData as T
		}

		@JvmStatic
		fun <T> flagsAt(flag: Flag<T>, location: Location): List<DataValue<T>>
		{
			val regions = at(location)
			if (regions.isEmpty()) return emptyList()

			val dataList = mutableListOf<DataValue<T>>()
			for (region in regions)
			{
				val data = region.getFlag(flag) ?: continue
				dataList.add(data)
			}

			return dataList
		}

		@Suppress("UNCHECKED_CAST")
		@JvmStatic
		fun <U, T: DataValue<U>> lookupFlag(flag: Flag<U>, regions: List<Region>): T
		{
			for (region in regions)
			{
				return region.getFlag(flag) as T? ?: continue
			}

			return flag.defaultData as T
		}

		@JvmStatic
		fun getRegionByName(name: String): Region?
		{
			return IMPL.getRegionByName(name)
		}

		@JvmStatic
		fun registerRegion(region: Region): Boolean
		{
			return IMPL.registerRegion(region)
		}

		@JvmStatic
		fun removeRegion(region: Region)
		{
			IMPL.removeRegion(region)
		}

		@JvmStatic
		fun saveRegion(region: Region)
		{
			IMPL.saveRegion(region)
		}
	}
}
