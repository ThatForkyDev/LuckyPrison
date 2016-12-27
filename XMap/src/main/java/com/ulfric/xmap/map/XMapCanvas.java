package com.ulfric.xmap.map;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView.Scale;

import com.ulfric.lib.api.chat.Chat;
import com.ulfric.lib.api.java.Named;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.xmap.map.element.MapElement;

public class XMapCanvas implements Named {

	protected XMapCanvas(File... files)
	{
		this.renderers = new XMapRenderer[files.length-1];

		for (File file : files)
		{
			FileConfiguration conf = YamlConfiguration.loadConfiguration(file);

			if (file.getName().equals("map_info.yml"))
			{
				this.name = conf.getString("name");

				this.scale = Scale.valueOf(conf.getString("map.scale").toUpperCase());

				this.background = (byte) conf.getInt("map.background");

				this.lore = conf.getStringList("item.lore");

				if (this.lore != null) this.lore = (List<String>) Chat.color(this.lore);

				this.displayName = Chat.color(conf.getString("item.name"));

				if (this.displayName != null) this.displayName = Chat.color(this.displayName);

				continue;
			}

			int index = conf.getInt("index");

			XMapRenderer renderer = new XMapRenderer(this, index, (short) conf.getInt("id"));

			for (String string : conf.getStringList("elements"))
			{
				MapElement element = MapLoader.elementFromString(string);

				if (element == null)
				{
					Bukkit.getLogger().warning(Strings.format("Bad XMap element in map {0}: {1}", this.name, string));

					continue;
				}

				renderer.withElement(MapLoader.elementFromString(string));
			}

			this.renderers[index] = renderer;
		}

		for (XMapRenderer renderer : this.renderers)
		{
			if (renderer == null) continue;

			renderer.init();
		}
	}

	private byte background;
	public byte getBackground() { return this.background; }

	private Scale scale;
	public Scale getScale() { return this.scale; }

	private String name;
	@Override
	public String getName() { return this.name; }

	private String displayName;
	public String getDisplayName() { return this.displayName; }

	private List<String> lore;
	public List<String> getLore() { return this.lore; }

	private XMapRenderer[] renderers;
	public XMapRenderer[] getRenderers() { return this.renderers; }
	public int getTotalRenderers() { return this.renderers.length; }
	public XMapRenderer getRenderer(int index)
	{
		if (this.renderers.length-1 < index) return null;

		return this.renderers[index];
	}

}