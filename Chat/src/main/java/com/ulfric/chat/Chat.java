package com.ulfric.chat;

import com.ulfric.chat.commands.CommandChannel;
import com.ulfric.chat.commands.CommandChat;
import com.ulfric.chat.commands.CommandColor;
import com.ulfric.chat.commands.CommandIgnore;
import com.ulfric.chat.commands.CommandMessage;
import com.ulfric.chat.commands.CommandNickname;
import com.ulfric.chat.commands.CommandRealname;
import com.ulfric.chat.commands.CommandReply;
import com.ulfric.chat.commands.CommandShownicks;
import com.ulfric.chat.commands.CommandSocialSpy;
import com.ulfric.chat.commands.CommandStaffMessage;
import com.ulfric.chat.listeners.ListenerConnection;
import com.ulfric.chat.modules.ModuleChatformat;
import com.ulfric.chat.modules.ModulePlotads;
import com.ulfric.lib.api.module.Plugin;

public class Chat extends Plugin {

	private static Chat i;
	public static Chat get() { return Chat.i; }

	@Override
	public void load()
	{
		Chat.i = this;

		this.withSubModule(new ModuleChatformat());
		this.withSubModule(new ModulePlotads());

		this.addCommand("staffmsg", new CommandStaffMessage());
		this.addCommand("chat", new CommandChat());
		this.addCommand("nickname", new CommandNickname());
		this.addCommand("realname", new CommandRealname());
		this.addCommand("shownicks", new CommandShownicks());
		this.addCommand("channel", new CommandChannel());
		this.addCommand("ignore", new CommandIgnore());
		this.addCommand("message", new CommandMessage());
		this.addCommand("reply", new CommandReply());
		this.addCommand("socialspy", new CommandSocialSpy());
		this.addCommand("color", new CommandColor());

		this.addListener( new ListenerConnection());
	}

	@Override
	public void disable()
	{
		Chat.i = null;
	}

}
