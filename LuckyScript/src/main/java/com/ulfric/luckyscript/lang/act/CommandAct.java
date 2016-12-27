package com.ulfric.luckyscript.lang.act;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class CommandAct extends Action<String> {

	public CommandAct(String context)
	{
		super(context);
	}

	private boolean player;

	@Override
	protected String parse(String context)
	{
		Assert.isNotEmpty(context, "The command must not be empty!", "The command must not be null!");

		context = context.toLowerCase();

		if (context.contains("--player"))
		{
			this.player = true;

			context = context.replace("--player", Strings.BLANK);
		}

		return context.trim();
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		Commands.dispatch(this.player ? player : this.luckySender, this.getValue().replace(Strings.PLAYER, player.getName()));
	}

	private CommandSender luckySender = new ConsoleCommandSender()
	{

		@Override
		public PermissionAttachment addAttachment(Plugin plugin)
		{
			return Bukkit.getConsoleSender().addAttachment(plugin);
		}

		@Override
		public PermissionAttachment addAttachment(Plugin plugin, int time)
		{
			return Bukkit.getConsoleSender().addAttachment(plugin, time);
		}

		@Override
		public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value)
		{
			return Bukkit.getConsoleSender().addAttachment(plugin, permission, value);
		}

		@Override
		public PermissionAttachment addAttachment(Plugin plugin, String permission, boolean value, int time)
		{
			return Bukkit.getConsoleSender().addAttachment(plugin, permission, value, time);
		}

		@Override
		public Set<PermissionAttachmentInfo> getEffectivePermissions()
		{
			return Bukkit.getConsoleSender().getEffectivePermissions();
		}

		@Override
		public boolean hasPermission(String permission)
		{
			return Bukkit.getConsoleSender().hasPermission(permission);
		}

		@Override
		public boolean hasPermission(Permission permission)
		{
			return Bukkit.getConsoleSender().hasPermission(permission);
		}

		@Override
		public boolean isPermissionSet(String permission)
		{
			return Bukkit.getConsoleSender().isPermissionSet(permission);
		}

		@Override
		public boolean isPermissionSet(Permission permission)
		{
			return Bukkit.getConsoleSender().isPermissionSet(permission);
		}

		@Override
		public void recalculatePermissions()
		{
			Bukkit.getConsoleSender().recalculatePermissions();
		}

		@Override
		public void removeAttachment(PermissionAttachment attachment)
		{
			Bukkit.getConsoleSender().removeAttachment(attachment);
		}

		@Override
		public boolean isOp()
		{
			return Bukkit.getConsoleSender().isOp();
		}

		@Override
		public void setOp(boolean value)
		{
			Bukkit.getConsoleSender().setOp(value);
		}

		@Override
		public Server getServer()
		{
			return Bukkit.getConsoleSender().getServer();
		}

		@Override
		public void sendMessage(String message)
		{
			Bukkit.getConsoleSender().sendMessage(message);
		}

		@Override
		public void sendMessage(String[] messages)
		{
			Bukkit.getConsoleSender().sendMessage(messages);
		}

		@Override
		public String getName() { return "LuckyScript"; }

		@Override
		public void abandonConversation(Conversation convo) { }

		@Override
		public void abandonConversation(Conversation convo, ConversationAbandonedEvent event) { }

		@Override
		public void acceptConversationInput(String input) { }

		@Override
		public boolean beginConversation(Conversation convo) { return false; }

		@Override
		public boolean isConversing() { return false; }

		@Override
		public void sendRawMessage(String message) { }
	};

}