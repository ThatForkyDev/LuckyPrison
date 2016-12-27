package com.ulfric.lib.api.inventory;

import com.ulfric.lib.api.java.Strings;

interface ItemPart {

	String MATERIAL = "id.";

	String AMOUNT = "am.";

	String DURABILITY = "du.";

	String ENCHANT = "en.";

	String STORED_ENCHANT = "se.";

	String NAME = "na.";

	String LORE = "lo.";

	String ITEM_FLAG = "if.";

	String BOOK_TITLE = "ti.";

	String BOOK_AUTHOR = "au.";

	String BOOK_PAGE = "pa.";

	String FIREWORK_POWER = "fp.";

	String FIREWORK_EFFECT = "fe.";

	String SKULL_OWNER = "so.";

	String MAP_SCALING = "ms.";

	String ARMOR_COLOR = "co.";

	String BANNER_BASE = "bb.";

	String BANNER_PATTERN = "bp.";

	String POTION_TYPE = "po.";

	String POTION_EFFECT = "pe.";

	static <T> String build(T t, String type)
	{
		return (type + t).replace(" ", Strings.FAKE_SPACE) + ' ';
	}

}
