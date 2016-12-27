package com.ulfric.luckyscript.lang.act;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class ParticleAct extends Action<Effect> {

	public ParticleAct(String context)
	{
		super(context);
	}

	private boolean player;
	private boolean world;
	private Vector shift;

	@Override
	protected Effect parse(String context)
	{
		Assert.isNotEmpty(context, "The context must not be null!", "The context must not be empty!");

		String typeStr = StringUtils.findOption(context, "type");

		Assert.notNull(typeStr, "Type string not found!");

		Effect effect = Assert.notNull(Effect.getByName(typeStr), "Unable to resolve particle: " + typeStr);

		this.world = context.contains("--world");

		this.player = context.contains(Strings.PLAYER);

		String shift = StringUtils.findOption(context, "shift");

		if (shift != null)
		{
			this.shift = LocationUtils.vectorFromString(shift);
		}

		return effect;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		Location location = (this.player ? player : object).getLocation();

		if (this.shift != null)
		{
			location = location.add(this.shift);
		}

		if (!this.world)
		{
			player.playEffect(location, this.getValue(), null);

			return;
		}

		for (Player lplayer : object.getLocation().getWorld().getPlayers())
		{
			lplayer.playEffect(location, this.getValue(), null);
		}
	}

}