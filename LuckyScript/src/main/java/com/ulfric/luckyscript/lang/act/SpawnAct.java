package com.ulfric.luckyscript.lang.act;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.ulfric.lib.api.entity.EntityBuilder;
import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.location.LocationUtils;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class SpawnAct extends Action<EntityBuilder> {

	public SpawnAct(String context)
	{
		super(context);
	}

	private boolean player;
	private Vector shift;

	@Override
	protected EntityBuilder parse(String context)
	{
		EntityBuilder builder = EntityBuilder.create();

		builder.withType(EntityUtils.parse(StringUtils.findOption(context, "type")));

		String name = StringUtils.findOption(context, "type");

		if (!org.apache.commons.lang3.StringUtils.isEmpty(name)) builder.withName(name);

		String fire = StringUtils.findOption(context, "fire");

		if (fire != null)
		{
			Integer duration = Numbers.parseInteger(fire);

			if (duration != null)
			{
				builder.withFire(duration);
			}
		}

		String shift = StringUtils.findOption(context, "shift");

		if (shift != null)
		{
			this.shift = LocationUtils.vectorFromString(shift);
		}

		String meta = StringUtils.findOption(context, "meta");

		if (meta != null)
		{
			String[] metas = meta.split("\\,");

			for (String metastr : metas)
			{
				String[] parts = metastr.split("\\=");

				String other = null;

				if (parts.length > 1)
				{
					other = parts[1];
				}

				builder.withMetadata(Strings.space(parts[0]), other);
			}
		}

		this.player = context.toLowerCase().contains(Strings.PLAYER);

		return builder;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		EntityBuilder builder = this.getValue();

		Location location = (this.player ? player : object).getLocation();

		if (this.shift != null)
		{
			location = location.add(this.shift);
		}

		builder.withLocation(location);

		builder.build();
	}

}