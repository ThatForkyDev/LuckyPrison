package com.ulfric.lib.api.hook;

import com.ulfric.lib.api.hook.XMapHook.IXMapHook;
import com.ulfric.lib.api.java.Named;
import org.bukkit.map.MapView;

public final class XMapHook extends Hook<IXMapHook> {

	XMapHook()
	{
		super(IXMapHook.EMPTY, "XMap", "XMap hook module", "Packet", "1.0.0-REL");
	}

	@Override
	public void postEnable()
	{
		this.impl.buildEngine(true);
	}

	@Override
	public void postDisable()
	{
		this.impl.clearEngine();
	}

	public XMap map(String name)
	{
		return this.impl.map(name);
	}

	public interface IXMapHook extends HookEngineImpl {
		IXMapHook EMPTY = new IXMapHook() {};

		default XMap map(String name)
		{
			return null;
		}
	}

	public interface XMap extends Named {
		byte getBackground();

		MapView.Scale getScale();

		int getRendererCount();
	}

	@FunctionalInterface
	public interface MapEngine {
		XMap getMap(String name);
	}
}
