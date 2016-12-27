package com.ulfric.luckyscript.lang.act;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class SoundAct extends Action<Sound> {

	public SoundAct(String context)
	{
		super(context);
	}

	private boolean player;
	private boolean world;
	private float volume;
	private float pitch;
	private Vector shift;

	@Override
	protected Sound parse(String context)
	{
		Assert.isNotEmpty(context, "The context must not be null!", "The context must not be empty!");

		String typeStr = Assert.notNull(StringUtils.findOption(context, "type"), "Type string not found!");

		Sound sound = Assert.notNull(Sound.valueOf(typeStr.toUpperCase()));

		this.world = context.contains("--world");

		this.player = context.contains(Strings.PLAYER);

		this.volume = Assert.notNull(Numbers.parseFloat(Assert.notNull(StringUtils.findOption(context, "volume"), "Volume string not found!")), "The volume must not be null!");

		this.pitch = Assert.notNull(Numbers.parseFloat(Assert.notNull(StringUtils.findOption(context, "pitch"), "Pitch string not found!")), "The pitch must not be null!");

		String shift = StringUtils.findOption(context, "shift");

		if (shift != null)
		{
			this.shift = LocationUtils.vectorFromString(shift);
		}

		return sound;
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
			player.playSound(location, this.getValue(), this.volume, this.pitch);

			return;
		}

		for (Player lplayer : object.getLocation().getWorld().getPlayers())
		{
			lplayer.playSound(location, this.getValue(), this.volume, this.pitch);
		}
	}

}