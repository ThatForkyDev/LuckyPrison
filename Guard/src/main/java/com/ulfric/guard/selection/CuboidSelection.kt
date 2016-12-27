package com.ulfric.guard.selection

import com.ulfric.guard.region.Cuboid
import com.ulfric.guard.region.Point
import com.ulfric.guard.region.Shape
import org.bukkit.util.Vector

internal class CuboidSelection : Selection
{
	override val isComplete: Boolean
		get() = left != null
	private var left: Vector? = null
	private var right: Vector? = null

	override fun getName(): String
	{
		return "cuboid"
	}

	override fun pushLeft(vector: Vector)
	{
		left = vector
	}

	override fun pushRight(vector: Vector)
	{
		right = vector
	}

	override fun toShape(): Shape
	{
		if ((left != null && right == null) || left == right)
		{
			return Point(left!!)
		}

		return Cuboid(left!!, right!!)
	}
}
