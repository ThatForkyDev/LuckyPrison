package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.inventory.Enchant;
import com.ulfric.lib.api.inventory.EnchantUtils;

final class EnchantArg implements ArgStrategy<Enchant> {

	@Override
	public Enchant match(String string)
	{
		Enchant enchant = EnchantUtils.parse(string);

		if (enchant == null || enchant.getEnchant() == null || enchant.getLevel() == null) return null;

		return enchant;
	}

}
