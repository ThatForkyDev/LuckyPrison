package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.PrisonHook.IPrisonHook;
import org.bukkit.entity.Player;

public final class PrisonHook extends Hook<IPrisonHook> {

	PrisonHook()
	{
		super(IPrisonHook.EMPTY, "Prison", "Prison hook module", "Packet", "1.0.0-REL");
	}

	public void updateScoreboard(Player player)
	{
		this.impl.updateScoreboard(player);
	}

	public interface IPrisonHook extends HookImpl {
		IPrisonHook EMPTY = new IPrisonHook() {
		};

		default void updateScoreboard(Player player)
		{
		}
	}

}
