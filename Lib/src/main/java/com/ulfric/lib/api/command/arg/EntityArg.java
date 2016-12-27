package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.entity.EntityUtils;
import org.bukkit.entity.EntityType;

final class EntityArg implements ArgStrategy<EntityType> {


	@Override
	public EntityType match(String string)
	{
		return EntityUtils.parse(string);
	}


}
