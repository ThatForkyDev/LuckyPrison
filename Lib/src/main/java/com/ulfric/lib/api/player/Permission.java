package com.ulfric.lib.api.player;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.locale.Locale;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

public final class Permission {

	private final String node;
	private final String error;

	public Permission(String node, String error)
	{
		Assert.isNotEmpty(node);

		this.node = node;

		this.error = StringUtils.isBlank(error) ? "system.no_permission" : error;
	}

	public boolean validate(Player player)
	{
		return player.hasPermission(this.node);
	}

	public boolean enforce(Player player, Object... objects)
	{
		if (this.validate(player)) return true;

		Locale.sendError(player, this.error, objects);

		return false;
	}

}
