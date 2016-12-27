package com.ulfric.guard.command

import com.ulfric.guard.coll.RegionColl
import com.ulfric.guard.region.Region
import com.ulfric.lib.api.command.Command
import com.ulfric.lib.api.command.SimpleSubCommand
import com.ulfric.lib.api.locale.Locale

class SubCommandDelete(command: Command) : SimpleSubCommand(command, "delete", "remove", "trash")
{
	init
	{
		withArgument("region", RegionStrategy, "guard.region_not_found")
	}

	override fun run()
	{
		val region = getObject("region") as Region
		RegionColl.removeRegion(region)

		Locale.sendSuccess(sender, "guard.region_del", region.name)
	}
}
