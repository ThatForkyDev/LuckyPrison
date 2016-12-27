package com.ulfric.chat.commands

import com.ulfric.chat.channel.enums.ChatChannel
import com.ulfric.chat.lang.Meta
import com.ulfric.lib.api.command.SimpleCommand
import com.ulfric.lib.api.entity.Metadata
import com.ulfric.lib.api.locale.Locale

class CommandStaffMessage : SimpleCommand()
{
	init
	{
		this.withEnforcePlayer()
		this.withIndexUnusedArgs()
	}

	override fun run()
	{
		val player = this.player
		val message = this.unusedArgs
		if (message == null || message.isBlank())
		{
			Locale.sendError(this.player, "chat.message_blank")
			return
		}

		val current = Metadata.getValue<ChatChannel>(player, Meta.CHAT_CHANNEL) ?: ChatChannel.GLOBAL
		Metadata.apply(player, Meta.CHAT_CHANNEL, ChatChannel.STAFF)

		player.chat(message)

		if (current == ChatChannel.GLOBAL)
		{
			Metadata.removeIfPresent(player, Meta.CHAT_CHANNEL)
		}
		else
		{
			Metadata.apply(player, Meta.CHAT_CHANNEL, current)
		}
	}
}
