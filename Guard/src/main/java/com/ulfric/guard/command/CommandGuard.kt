package com.ulfric.guard.command

import com.ulfric.lib.api.command.SubCommandParent

class CommandGuard : SubCommandParent()
{
	init
	{
		withSubcommand(SubCommandCreate(this))
		withSubcommand(SubCommandDelete(this))
		withSubcommand(SubCommandFlag(this))
		withSubcommand(SubCommandInfo(this))
	}
}
