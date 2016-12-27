package com.ulfric.xmap.map.element;

import org.bukkit.map.MapCanvas;

public interface MapElement {


	void draw(MapCanvas canvas);

	int getX();

	int getY();


}