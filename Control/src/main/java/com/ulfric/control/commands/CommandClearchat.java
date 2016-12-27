package com.ulfric.control.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandClearchat extends SimpleCommand {

	public CommandClearchat()
	{
		this.withArgument("int", ArgStrategy.INTEGER, 100);
	}

	@Override
	public void run()
	{
		int amount = (int) this.getObject("int");

		if (this.hasObjects()) objects:
		{
			amount = Math.max(1, amount);

			if (amount <= 1000) break objects;

			Locale.sendError(this.getSender(), "control.cc_too_many");

			return;
		}

		for (int x = 0; x < amount; x++)
		{
			for (Player player : Bukkit.getOnlinePlayers())
			{
				player.sendMessage(Strings.BLANK);
			}
		}

		Locale.sendMass("control.cc", this.getName());

		// Slack message init
		JsonObject object = new JsonObject();
		JsonObject attachment = new JsonObject();
		attachment.addProperty("color","#f60000");
		// Slack message text
		object.addProperty("text", "An administrative action has been taken!");
		attachment.addProperty("text","Chat has been cleared by "+this.getName()+"!");
		// Slack message close and send
		JsonArray attachmentArray = new JsonArray();
		attachmentArray.add(attachment);
		object.add("attachments",attachmentArray);
	}

}