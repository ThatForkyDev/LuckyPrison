package com.ulfric.prison.commands;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ulfric.lib.api.command.SimpleCommand;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Booleans;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.prison.enchantments.LoadableEnchantment;
import com.ulfric.prison.lang.Meta;

public class CommandNightvision extends SimpleCommand {


	public CommandNightvision()
	{
		this.withEnforcePlayer();
	}

	private final PotionEffect eff = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);

	@Override
	public void run()
	{
		Player player = this.getPlayer();

		boolean value = Metadata.removeIfPresent(player, Meta.NIGHT_VISION);

		if (!value)
		{
			Metadata.applyNull(player, Meta.NIGHT_VISION);
			this.getPlayer().addPotionEffect(this.eff);
		}
		else
		{
			ItemStack inHand = getPlayer().getItemInHand();
			boolean shouldRemove = true;
			for (Map.Entry<Enchantment, Integer> enchantment : inHand.getEnchantments().entrySet())
			{
				if (!(enchantment.getKey() instanceof LoadableEnchantment)) continue;
				LoadableEnchantment loadableEnchantment = ((LoadableEnchantment) enchantment.getKey());

				if (loadableEnchantment == null || loadableEnchantment.getEffect() == null) continue;

				if (!loadableEnchantment.getEffect().equals(PotionEffectType.NIGHT_VISION)) continue;

				shouldRemove = false;
				break;
			}
			if (shouldRemove)
			{
				this.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
			}
		}

		Locale.sendSuccess(player, "prison.night_vision_toggle", Booleans.fancify(!value, player));


	}


}