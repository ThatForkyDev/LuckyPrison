package com.ulfric.luckyscript.lang.act;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.teleport.TeleportUtils;
import com.ulfric.luckyscript.lang.except.ScriptParseException;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class TeleportAct extends Action<Location> {

	public TeleportAct(String context)
	{
		super(context);
	}

	private boolean other;
	private Vector shift;

	@Override
	protected Location parse(String context)
	{
		String lower = context.toLowerCase();
		if (lower.equals("--other") || lower.equals("<location>"))
		{
			this.other = true;

			String shift = StringUtils.findOption(lower, "shift");

			if (shift != null)
			{
				this.shift = LocationUtils.vectorFromString(shift);
			}

			return null;
		}

		Location location = LocationUtils.fromString(context);

		if (location == null) throw new ScriptParseException(context);

		return location;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		if (this.other)
		{
			Location location = object.getLocation();

			if (this.shift != null)
			{
				location = location.add(this.shift);
			}

			TeleportUtils.teleport(player, location);

			return;
		}
		TeleportUtils.teleport(player, this.getValue());
	}

}