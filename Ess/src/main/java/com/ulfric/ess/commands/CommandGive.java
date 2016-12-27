package com.ulfric.ess.commands;

import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.command.arg.ExactArg;
import com.ulfric.lib.api.command.arg.RegexArg;
import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.inventory.ItemBuilder;
import com.ulfric.lib.api.inventory.ItemPair;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;

public class CommandGive extends SimpleCommand {

	public CommandGive()
	{
		this.withArgument("type", ArgStrategy.MATERIAL, "ess.give_err");
		this.withArgument("amt", ArgStrategy.POSITIVE_INTEGER, 1);
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer);
		this.withArgument("ench", ArgStrategy.ENCHANT);
		this.withArgument("roman", new ExactArg("--roman"));
		this.withArgument("name", RegexArg.of(Pattern.quote("na.") + "[a-z0-9\\<\\>\\&]+"));
	}

	@Override
	public void run()
	{
		ItemBuilder builder = new ItemBuilder();

		ItemPair pair = (ItemPair) this.getObject("type");
		builder.withType(pair);

		int amount = (int) this.getObject("amt");
		builder.withAmount(amount);

		Player target = (Player) this.getObject("player");

		Enchant enchant = (Enchant) this.getObject("ench");

		if (enchant != null)
		{
			builder.withEnchant(enchant.getEnchant(), enchant.getLevel(), true, this.hasObject("roman"));
		}

		String name = (String) this.getObject("name");

		if (name != null)
		{
			builder.withName(Chat.color(Strings.space(name.substring(3, name.length()))));
		}

		target.getInventory().addItem(builder.build());

		Locale.sendSuccess(this.getSender(), "ess.give", pair.toString(), amount, target.getName());
	}

}