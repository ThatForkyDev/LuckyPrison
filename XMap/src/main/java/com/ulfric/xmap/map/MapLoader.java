package com.ulfric.xmap.map;

import com.ulfric.xmap.map.element.MapElement;

public class MapLoader {

	protected static IMapLoader impl = IMapLoader.EMPTY;

	public static XMapCanvas getMap(String name)
	{
		return MapLoader.impl.getMap(name);
	}

	public static MapElement elementFromString(String string)
	{
		return MapLoader.impl.elementFromString(string);
	}

	protected interface IMapLoader
	{
		IMapLoader EMPTY = new IMapLoader() { };

		default XMapCanvas getMap(String name) { return null; }

		default MapElement elementFromString(String string) { return null; }

		default void load() { }
	}

}