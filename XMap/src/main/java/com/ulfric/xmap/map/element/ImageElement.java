package com.ulfric.xmap.map.element;

import java.awt.Image;
import java.io.File;

import org.bukkit.map.MapCanvas;

import com.ulfric.lib.api.java.Assert;
import com.ulfric.xmap.XMap;

public class ImageElement implements MapElement {


	public static final File DIR = new File(XMap.get().getDataFolder(), "images");
	static
	{
		ImageElement.DIR.mkdirs();
	}

	public ImageElement(int x, int y, Image image)
	{
		this.x = x;

		this.y = y;

		Assert.notNull(image, "The image must not be null!");

		this.image = image;
	}

	private final Image image;
	public Image getImage() { return this.image; }

	private final int x;
	@Override
	public int getX() { return this.x; }

	private final int y;
	@Override
	public int getY() { return this.y; }

	@Override
	public void draw(MapCanvas canvas)
	{
		canvas.drawImage(this.x, this.y, this.image);
	}


}