package com.ulfric.guard.region

import org.bukkit.util.Vector

import java.util.Comparator
import java.util.TreeSet

class Cuboid(min: Vector, max: Vector) : Shape
{
	private val maxX: Int
	private val maxY: Int
	private val maxZ: Int
	private val minX: Int
	private val minY: Int
	private val minZ: Int

	override val maxPoint: Vector
	override val minPoint: Vector
	private var vectors: MutableSet<Vector>? = null
	private var hash = Integer.MIN_VALUE

	init
	{
		minX = Math.min(min.blockX, max.blockX)
		minY = Math.min(min.blockY, max.blockY)
		minZ = Math.min(min.blockZ, max.blockZ)
		maxX = Math.max(min.blockX, max.blockX)
		maxY = Math.max(min.blockY, max.blockY)
		maxZ = Math.max(min.blockZ, max.blockZ)

		maxPoint = Vector(maxX, maxY, maxZ)
		minPoint = Vector(minX, minY, minZ)
	}

	override fun getName(): String
	{
		return "cuboid"
	}

	override fun hashCode(): Int
	{
		if (hash == Int.MIN_VALUE)
		{
			var result = maxX
			result = 31 * result + maxY
			result = 31 * result + maxZ
			result = 31 * result + minX
			result = 31 * result + minY
			result = 31 * result + minZ
			hash = result
		}

		return hash
	}

	override fun equals(other: Any?): Boolean
	{
		if (this === other) return true
		if (other !is Cuboid) return false

		if (maxX != other.maxX) return false
		if (maxY != other.maxY) return false
		if (maxZ != other.maxZ) return false

		return minZ == other.minZ
	}

	override fun containsPoint(point: Vector): Boolean
	{
		val x = point.blockX
		val y = point.blockY
		val z = point.blockZ
		return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ
	}

	override fun iterator(): Iterator<Vector>
	{
		if (vectors == null)
		{
			val middle = Vector((maxX + minX) / 2, (maxY + minY) / 2, (maxZ + minZ) / 2)
			vectors = TreeSet(VectorComparator(middle))

			for (x in minX..maxX)
			{
				for (y in minY..maxY)
				{
					for (z in minZ..maxZ)
					{
						vectors!!.add(Vector(x, y, z))
					}
				}
			}
		}

		return vectors!!.iterator()
	}

	override fun serialize(): Map<String, Any>
	{
		return mapOf("type" to name, "max" to maxPoint, "min" to minPoint)
	}

	companion object
	{
		@JvmStatic
		fun deserialize(serialized: Map<String, Any>): Cuboid
		{
			val max = serialized["max"] as Vector
			val min = serialized["min"] as Vector
			return Cuboid(min, max)
		}
	}

	private class VectorComparator(private val neutral: Vector) : Comparator<Vector>
	{
		override fun compare(v1: Vector, v2: Vector): Int
		{
			val compare = java.lang.Double.compare(v1.distanceSquared(neutral), v2.distanceSquared(neutral))

			return if (compare == 0) 1 else compare
		}
	}

}
