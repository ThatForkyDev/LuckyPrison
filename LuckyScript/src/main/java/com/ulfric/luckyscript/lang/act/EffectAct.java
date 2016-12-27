package com.ulfric.luckyscript.lang.act;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class EffectAct extends Action<PotionEffect> {

	public EffectAct(String context)
	{
		super(context);
	}

	private boolean override;

	@Override
	protected PotionEffect parse(String context)
	{
		String typeStr = StringUtils.findOption(context, "type");

		Assert.notNull(typeStr, "No potion type found!");

		PotionEffectType type = PotionEffectType.getByName(typeStr);

		Assert.notNull(type, "No potion type found from the given string!");

		String durStr = StringUtils.findOption(context, "duration");

		Assert.notNull(durStr, "No duration (int) found!");

		Integer dur = Numbers.parseInteger(durStr);

		Assert.isTrue(dur > 0, "The duration entered is not a valid integer!");

		Integer amp = 1;

		String ampStr = StringUtils.findOption(context, "amplifier");

		if (ampStr != null)
		{
			amp = Numbers.parseInteger(ampStr);

			Assert.isTrue(amp >= 0, "The amplifier must be positive!");
		}

		context = context.toLowerCase();

		this.override = context.contains("--override");

		return new PotionEffect(type, dur, amp, context.contains("--ambient"), !context.contains("--noparticle"));
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		player.addPotionEffect(this.getValue(), this.override);
	}

}