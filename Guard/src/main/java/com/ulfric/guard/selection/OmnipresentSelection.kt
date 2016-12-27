package com.ulfric.guard.selection

import com.ulfric.guard.region.Omnipresent
import com.ulfric.guard.region.Shape
import org.bukkit.util.Vector

class OmnipresentSelection : Selection
{
	override val isComplete = true

	override fun getName(): String
	{
		return "omnipresent"
	}

	override fun pushLeft(vector: Vector)
	{
	}

	override fun pushRight(vector: Vector)
	{
	}

	override fun toShape(): Shape
	{
		return Omnipresent
	}
}
