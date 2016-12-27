package com.ulfric.lib.api.command.arg;

import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.player.PlayerUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.bukkit.entity.Player;

/**
 * Public for Control stuff
 *
 * @author Adam
 */
public final class InetArg implements ArgStrategy<String> {

	@Override
	public String match(String string)
	{
		if ("localhost".equalsIgnoreCase(string)) return "127.0.0.1";

		Player player = PlayerUtils.getOnline(string);

		if (player == null)
		{
			string = StringUtils.formatIP(string);

			if (!InetAddressValidator.getInstance().isValid(string)) return null;

			return string;
		}

		return PlayerUtils.getIP(player);
	}

}
