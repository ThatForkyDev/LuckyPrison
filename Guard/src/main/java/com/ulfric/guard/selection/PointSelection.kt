package com.ulfric.guard.selection

import com.ulfric.guard.region.Point
import com.ulfric.guard.region.Shape
import org.bukkit.util.Vector

class PointSelection : Selection
{
	override val isComplete: Boolean
		get() = point != null
	private var point: Vector? = null

	override fun getName(): String
	{
		return "point"
	}

	override fun pushLeft(vector: Vector)
	{
		point = vector
	}

	override fun pushRight(vector: Vector)
	{
		point = vector
	}

	override fun toShape(): Shape
	{
		return Point(point!!)
	}
}
