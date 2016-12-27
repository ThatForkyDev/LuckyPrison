package com.ulfric.xmap.map;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import com.google.common.collect.Lists;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.world.Worlds;
import com.ulfric.xmap.map.element.MapElement;

public class XMapRenderer extends MapRenderer {

	protected XMapRenderer(XMapCanvas xmap, int index, short mapId)
	{
		super(false);

		this.xmap = xmap;

		this.index = index;

		this.mapId = mapId;
	}

	protected void init()
	{
		MapView view = Assert.notNull(Bukkit.getMap(this.mapId, Worlds.main()));

		view.setScale(this.xmap.getScale());

		view.getRenderers().clear();

		view.addRenderer(this);
	}

	private final XMapCanvas xmap;
	public XMapCanvas getXMap() { return this.xmap; }

	private final short mapId;
	public short getMapId() { return this.mapId; }

	private final int index;
	public int getIndex() { return this.index; }

	private List<MapElement> elements;
	protected void withElement(MapElement element)
	{
		Assert.notNull(element, "The map element must not be null!");

		if (this.elements == null) this.elements = Lists.newArrayList();

		this.elements.add(element);
	}

	@Override
	public void initialize(MapView view)
	{
		if (this.initLock) return;

		this.initLock = true;

		super.initialize(view);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		if (this.renderLock) return;

		this.renderLock = true;

		byte background = this.getXMap().getBackground();

		if (background >= 0)
		{
			for (int x = 0; x < 128; x++)
			{
				for (int y = 0; y < 128; y++)
				{
					canvas.setPixel(x, y, background);
				}
			}
		}

		for (MapElement element : this.elements)
		{
			element.draw(canvas);
		}

		canvas.setCursors(new MapCursorCollection());
	}

	private boolean initLock = false;
	private boolean renderLock = false;

}