package com.ulfric.luckyscript.lang.act;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import com.ulfric.lib.api.entity.EntityUtils;
import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.server.Events;
import com.ulfric.uspigot.metadata.LocatableMetadatable;

public class KillAct extends Action<Object> {

	public KillAct(String context)
	{
		super(context);
	}

	private boolean player;
	private boolean other;

	@Override
	protected Object parse(String context)
	{
		if (context == null || context.isEmpty()) return null;

		context = context.toLowerCase();

		this.other = context.contains("--other");

		this.player = context.contains(Strings.PLAYER);

		return null;
	}

	@Override
	public void run(Player player, LocatableMetadatable object)
	{
		if (!this.other)
		{
			EntityUtils.kill(player);

			return;
		}

		if (object instanceof Damageable)
		{
			EntityUtils.kill((Damageable) object);

			return;
		}

		if (object instanceof Block)
		{
			if (this.player)
			{
				Metadata.applyNull(object, "_ulf_fakebreak");

				Block block = (Block) object;
	
				if (!Events.call(new BlockBreakEvent(block, player)).isCancelled())
				{
					block.setType(Material.AIR, false);
				}

				Metadata.remove(object, "_ulf_fakebreak");

				return;
			}

			((Block) object).setType(Material.AIR, false);

			return;
		}

		throw new UnsupportedOperationException(object.getClass().getName());
	}

}