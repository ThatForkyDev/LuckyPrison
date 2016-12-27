package com.ulfric.guard.command

import com.ulfric.guard.Guard
import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.region.Flags
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.command.Command
import com.ulfric.lib.api.command.SimpleSubCommand
import com.ulfric.lib.api.locale.Locale

class SubCommandCreate(command: Command) : SimpleSubCommand(command, "create", "add", "new")
{
	init
	{
		this.withEnforcePlayer()

		this.withArgument("weight", PositiveIntegerStrategy, 0)
		this.withArgument("name", StringStrategy, "guard.region_name_err")
	}

	override fun run()
	{
		val selection = Guard.get().getSelection(player)
		if (!selection.isComplete)
		{
			Locale.sendError(player, "guard.no_selection")
			return
		}

		val name = this.getObject("name") as String

		val existing = RegionColl.getRegionByName(name)
		if (existing != null)
		{
			Locale.sendError(player, "guard.region_exists")
			return
		}

		val weight = this.getObject("weight") as Int
		val region = Region(name, player.world.name, selection.toShape(), null)
		if (weight != 0)
		{
			region.newFlag(Flags.getFlag("weight")!!).setData(weight)
		}

		RegionColl.registerRegion(region)
		Locale.sendSuccess(player, "guard.region_made", name)
	}
}
