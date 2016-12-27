package com.ulfric.guard.coll

import com.ulfric.guard.region.Shape
import org.bukkit.util.Vector
import java.util.HashMap
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Spatial hashing is a technique that allows for efficient collision detection between points and shapes
 * while also allowing for efficient addition and removal. It achieves this by splitting the world up in to
 * a grid of cubes with a size specified at construction time. To allow limitless grids and save on memory
 * usage the entire grid is not stored in memory, instead each cube is represented as a particular hashed value
 * and storage is a HashMap of these values to a list of entries that lie within the cube.
 */
class SpatialHash<T: Any>(gridSize: Int)
{
	val gridSize = gridSize.coerceAtLeast(1)
	private val map = HashMap<Long, MutableList<Entry<T>>>()

	fun put(shape: Shape, value: T)
	{
		val min = shape.minPoint
		val max = shape.maxPoint

		for (x in min.blockX / gridSize..max.blockX / gridSize)
		{
			for (y in min.blockY / gridSize..max.blockY / gridSize)
			{
				for (z in min.blockZ / gridSize..max.blockZ / gridSize)
				{
					if (DEBUG)
					{
						val coords = Triple(x, y, z)
						val packed = pack(x, y, z)
						val unpacked = unpack(packed)

						System.out.println("Adding $value to SpatialHash cell $coords")
						System.out.println("  - Packed cell value is $packed")
						System.out.println("  - Unpacks to coords $unpacked")
					}

					map.computeIfAbsent(pack(x, y, z), { key -> mutableListOf<Entry<T>>() }).add(Entry(shape, value))
				}
			}
		}
	}

	@JvmOverloads
	fun remove(shape: Shape, value: T? = null): Boolean
	{
		val min = shape.minPoint
		val max = shape.maxPoint
		var modified = false

		for (x in min.blockX / gridSize..max.blockX / gridSize)
		{
			for (y in min.blockY / gridSize..max.blockY / gridSize)
			{
				for (z in min.blockZ / gridSize..max.blockZ / gridSize)
				{
					val entries = map[pack(x, y, z)] ?: continue
					val iterator = entries.iterator()
					while (iterator.hasNext())
					{
						val entry = iterator.next()
						if (entry.key == shape && (value == null || entry.value == value))
						{
							iterator.remove()
							modified = true
						}
					}
				}
			}
		}

		return modified
	}

	/**
	 * Check for collisions between a given point and any shapes stored.
	 * Calls the given callback with the data about any shapes that this point collides with.

	 * @param point    The point we want to test
	 * *
	 * @param callback Callback to run against any shapes the point collides with
	 */
	fun hitTest(point: Vector, callback: BiConsumer<Shape, T>)
	{
		val entries = map[pack(point.blockX / gridSize, point.blockY / gridSize, point.blockZ / gridSize)] ?: return

		for ((key, value) in entries)
		{
			if (!key.containsPoint(point)) continue

			callback.accept(key, value)
		}
	}

	fun hitTest(point: Vector, callback: Consumer<T>)
	{
		val entries = map[pack(point.blockX / gridSize, point.blockY / gridSize, point.blockZ / gridSize)] ?: return

		for ((key, value) in entries)
		{
			if (!key.containsPoint(point)) continue

			callback.accept(value)
		}
	}

	private fun pack(x: Int, y: Int, z: Int): Long
	{
		return z.toLong() shl 32 and 0x00FFFFFF00000000L or (x.toLong() shl 8 and 0x00000000FFFFFF00L) or y.toLong()
	}

	private fun unpack(packed: Long): Triple<Int, Int, Int>
	{
		val x = packed and 0x00000000FFFFFF00L ushr 8
		val y = packed and 0x00000000000000FFL
		val z = packed and 0x00FFFFFF00000000L ushr 32
		return Triple(x.toInt(), y.toInt(), z.toInt())
	}

	private data class Entry<out T>(override val key: Shape, override val value: T) : Map.Entry<Shape, T>

	companion object
	{
		const val DEBUG = false
	}
}
