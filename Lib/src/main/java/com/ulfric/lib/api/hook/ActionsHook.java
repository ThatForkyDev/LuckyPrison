package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.ActionsHook.IActionsHook;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

public final class ActionsHook extends Hook<IActionsHook> {

	ActionsHook()
	{
		super(IActionsHook.EMPTY, "Actions", "Logging hook module", "Packet", "1.0.0-REL");
	}

	public void log(Player player, String message)
	{
		this.impl.log(player, message);
	}

	public void log(Player player, Supplier<String> supplier)
	{
		this.impl.log(player, supplier);
	}

	public interface IActionsHook extends HookImpl {
		IActionsHook EMPTY = new IActionsHook() {
		};

		default void log(Player player, String message)
		{
		}

		default void log(Player player, Supplier<String> supplier)
		{
		}
	}

}
