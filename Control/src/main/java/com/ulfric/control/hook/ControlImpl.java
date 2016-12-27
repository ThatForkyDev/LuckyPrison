package com.ulfric.control.hook;

import com.ulfric.control.coll.IPCache;
import com.ulfric.lib.api.hook.ControlHook.IControlHook;

public enum ControlImpl implements IControlHook {

	INSTANCE;

	@Override
	public int countUniqueIPs()
	{
		return IPCache.INSTANCE.size();
	}

}