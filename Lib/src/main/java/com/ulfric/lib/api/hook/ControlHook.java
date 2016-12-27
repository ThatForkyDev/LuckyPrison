package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.ControlHook.IControlHook;

public final class ControlHook extends Hook<IControlHook> {

	ControlHook()
	{
		super(IControlHook.EMPTY, "Control", "Control hook module", "Packet", "1.0.0-REL");
	}

	public int countUniqueIPs()
	{
		return this.impl.countUniqueIPs();
	}

	public interface IControlHook extends HookImpl {
		IControlHook EMPTY = new IControlHook() {
		};

		default int countUniqueIPs()
		{
			return 0;
		}
	}

}
