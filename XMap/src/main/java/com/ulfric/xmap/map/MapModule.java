package com.ulfric.xmap.map;

import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bukkit.map.MinecraftFont;

import com.google.common.collect.Maps;
import com.ulfric.lib.api.java.Assert;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;
import com.ulfric.lib.api.module.SimpleModule;
import com.ulfric.xmap.XMap;
import com.ulfric.xmap.listener.MapListener;
import com.ulfric.xmap.map.MapLoader.IMapLoader;
import com.ulfric.xmap.map.element.ImageElement;
import com.ulfric.xmap.map.element.MapElement;
import com.ulfric.xmap.map.element.PixleElement;
import com.ulfric.xmap.map.element.TextElement;

public class MapModule extends SimpleModule {

	public MapModule()
	{
		super("map", "Map loading module", "Packet", "1.0.0-REL");

		this.withSubModule(new MapPaginationModule());
	}

	@Override
	public void postEnable()
	{
		MapLoader.impl = new IMapLoader()
		{
			private Map<String, XMapCanvas> maps;

			@Override
			public void load()
			{
				File folder = new File(XMap.get().getDataFolder(), "maps");

				if (folder.mkdirs()) return;

				File[] files = folder.listFiles();

				this.maps = Maps.newHashMapWithExpectedSize(Math.max(files.length, 3));

				for (File file : folder.listFiles())
				{
					if (!file.isDirectory()) continue;

					XMapCanvas map = new XMapCanvas(file.listFiles());

					this.maps.put(map.getName(), map);
				}
			}

			@Override
			public XMapCanvas getMap(String name)
			{
				return this.maps.get(name);
			}

			@Override
			public MapElement elementFromString(String string)
			{
				Assert.isNotEmpty(string);

				MapElement element = null;

				int x = Numbers.parseInteger(StringUtils.findOption(string, "xpos"));

				int y = Numbers.parseInteger(StringUtils.findOption(string, "ypos"));

				String type = Assert.notNull(StringUtils.findOption(string, "type"));
				switch (type)
				{
					case "pixle":
						element = new PixleElement(x, y, Byte.valueOf(StringUtils.findOption(string, "byte")));
						break;

					case "image":
						try
						{
							element = new ImageElement(x, y, ImageIO.read(new File(ImageElement.DIR, StringUtils.findOption(string, "img"))));
						}
						catch (Exception exception) { exception.printStackTrace(); }
						break;

					case "text":
						element = new TextElement(x, y, MinecraftFont.Font, Strings.space(StringUtils.findOption(string, "text")));
						break;

					default:
						throw new UnsupportedOperationException("Map element type not found: " + type);
				}

				return Assert.notNull(element);
			}
		};

		MapLoader.impl.load();
	}

	@Override
	public void postDisable()
	{
		MapLoader.impl = IMapLoader.EMPTY;
	}

	private class MapPaginationModule extends SimpleModule
	{
		public MapPaginationModule()
		{
			super("mappagination", "Map pagination module", "Packet", "1.0.0-REL");

			this.addListener(new MapListener());
		}
	}

}