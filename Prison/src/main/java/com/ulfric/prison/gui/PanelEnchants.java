package com.ulfric.prison.gui;

import java.util.Arrays;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.ulfric.lib.api.gui.Panel;
import com.ulfric.lib.api.java.Strings;

public class PanelEnchants extends Panel {

	private static final PanelEnchants INSTANCE = new PanelEnchants();
	public static PanelEnchants get() { return PanelEnchants.INSTANCE; }

	private PanelEnchants()
	{
		super("enchants", ChatColor.BOLD + Strings.BLANK + ChatColor.BLUE + "Lucky Prison Enchantments", 18, null);

		this.build();
	}

	private void build()
	{
		this.addItem(this.createItem("Blasting", "Grants you the ability to blast extra blocks away while mining!"),
					 this.createItem("Circles", "Mine a circle around your pickaxe with this enchant!"),
			 		 this.createItem("Flight", "When you hold an item with this enchantment, you will fly!"),
			 		 this.createItem("Night Vision", "Seeing in the dark won't be a problem!"),
			 		 this.createItem("Speedy Gonzales", "They won't even see you coming, you'll be so... speedy."),
			 		 this.createItem("Jump", "Can Superman fly, or does he just jump REALLY high?"),
			 		 this.createItem("Adam", "Draws the owners name in the floor when you mine!"),
			 		 this.createItem("Creeper", "A creeper face will be broken into the blocks around where you mine."),
			 		 this.createItem("Haste", "You'll mine super fast and stuff."),
			 		 this.createItem("Pitchfork", "Carve a two-pronged spear into the ground."),
			 		 this.createItem("Resistance", "Your skin will be so thick, you won't even need a shield!"),
			 		 this.createItem("Rings", "Create a mini-Saturn with every swing of your pickaxe."),
			 		 this.createItem("Saturation", "Never get the munchies!"),
			 		 this.createItem("Triangles", "Just like circles, except with, you know, triangles."),
			 		 this.createItem("Trident", "It's like Pitchfork, but with an extra prong!"),
					 this.createItem("X", "The letter X is always a cool letter. Draw it everywhere!"),
			 		 this.createItem("Magic", "Gives you EXP when you mine"));
	}

	public ItemStack createItem(String name, String lore)
	{
		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + name);
		meta.setLore(Arrays.asList(WordUtils.wrap(ChatColor.GRAY + lore, 18, "\n" + ChatColor.GRAY, false).split("\n")));
		item.setItemMeta(meta);
		return item;
	}

}