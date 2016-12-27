package com.ulfric.xmap.map.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapPalette;

import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.math.Numbers;

@SuppressWarnings("deprecation")
public class TextElement implements MapElement {


	private static final Pattern COLOR_PATTERN = Pattern.compile("[\\[][c]([o][l][o]([u])?[r])?[=](([0-9](([0-9])?[0-9])?)(\\|)?){3}[\\]]");

	public TextElement(int x, int y, MapFont font, String text)
	{
		this.x = x;

		this.y = y;

		this.font = font;

		Matcher matcher = TextElement.COLOR_PATTERN.matcher(text);
		while (matcher.find())
		{
			String group = matcher.group();
			String find = group.substring(1, group.length()-1).split("\\=")[1];

			String[] rgb = find.split("\\|");

			int r = Numbers.parseInteger(rgb[0]);
			int g = rgb.length > 1 ? Numbers.parseInteger(rgb[1]) : 0;
			int b = rgb.length > 2 ? Numbers.parseInteger(rgb[2]) : 0;

			text = text.replace(group, Strings.format("{0}{1};", ChatColor.COLOR_CHAR, MapPalette.matchColor(r, g, b)));
		}

		this.text = text.replace("<n>", "\n");
	}

	private final MapFont font;
	public MapFont getFont() { return this.font; }

	private final String text;
	public String getText() { return this.text; }

	private final int x;
	@Override
	public int getX() { return this.x; }

	private final int y;
	@Override
	public int getY() { return this.y; }

	@Override
	public void draw(MapCanvas canvas)
	{
		canvas.drawText(this.x, this.y, this.font, this.text);
	}


}