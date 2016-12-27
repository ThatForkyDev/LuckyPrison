package com.ulfric.ess.commands;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.ulfric.ess.lang.Permissions;
import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.command.arg.ArgStrategy;
import com.ulfric.lib.api.inventory.ItemUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.player.PlayerUtils;

public class CommandVanish extends SimpleCommand {


	public CommandVanish()
	{
		this.withArgument("player", ArgStrategy.PLAYER, this::getPlayer, "ess.vanish_other_err");
	}


	@Override
	public void run()
	{
		Player player = this.getPlayer();
		Player target = (Player) this.getObject("player");

		boolean same = false;

		if (this.isPlayer() && !(same = target.equals(player)) && !this.hasPermission(Permissions.VANISH_OTHERS))
		{
			Locale.sendError(this.getSender(), "ess.vanish_other_err");

			return;
		}

		EntityEquipment equipment = target.getEquipment();

		if (!PlayerUtils.isVanished(player))
		{
			Locale.sendSuccess(player, "ess.vanish", same ? Strings.BLANK : ' ' + target.getName(), PlayerUtils.vanish(target));

			equipment.setArmorContents(new ItemStack[]
			{
				this.newPiece(Material.LEATHER_HELMET),
				this.newPiece(Material.LEATHER_CHESTPLATE),
				this.newPiece(Material.LEATHER_LEGGINGS),
				this.newPiece(Material.LEATHER_BOOTS),
			});

			return;
		}

		PlayerUtils.unvanish(target);

		ItemStack[] items = equipment.getArmorContents();
		for (int x = 0; x < items.length; x++)
		{
			ItemStack item = items[x];

			if (ItemUtils.isEmpty(item)) continue;

			switch (item.getType())
			{
				case LEATHER_HELMET:
				case LEATHER_CHESTPLATE:
				case LEATHER_LEGGINGS:
				case LEATHER_BOOTS:
					items[x] = null;

				default:
					continue;
			}
		}

		equipment.setArmorContents(items);

		Locale.sendSuccess(player, "ess.vanish_visible");
	}

	private ItemStack newPiece(Material material)
	{
		ItemStack item = new ItemStack(material);

		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

		meta.setColor(Color.BLACK);

		meta.setDisplayName(ChatColor.DARK_PURPLE + "VANISH ARMOR");

		item.setItemMeta(meta);

		return item;
	}


}