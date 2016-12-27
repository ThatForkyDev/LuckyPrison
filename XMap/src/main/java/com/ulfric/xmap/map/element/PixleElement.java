package com.ulfric.xmap.map.element;

import org.bukkit.map.MapCanvas;

public class PixleElement implements MapElement {


	public PixleElement(int x, int y, byte value)
	{
		this.x = x;

		this.y = y;

		this.value = value;
	}

	private final byte value;
	public byte getValue() { return this.value; }

	private final int x;
	@Override
	public int getX() { return this.x; }

	private final int y;
	@Override
	public int getY() { return this.y; }

	@Override
	public void draw(MapCanvas canvas)
	{
		canvas.setPixel(this.x, this.y, this.value);
	}


}