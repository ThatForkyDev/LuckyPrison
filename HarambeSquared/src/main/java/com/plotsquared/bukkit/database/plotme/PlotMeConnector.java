package com.plotsquared.bukkit.database.plotme;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

import com.intellectualcrafters.configuration.file.FileConfiguration;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Location;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;

public interface PlotMeConnector {

	Connection getPlotMeConnection(String plugin, FileConfiguration plotConfig, String dataFolder);

	HashMap<String, HashMap<PlotId, Plot>> getPlotMePlots(Connection connection) throws SQLException;

	boolean accepts(String version);

	default boolean isValidConnection(Connection connection)
	{
		return connection != null;
	}

	default void copyConfig(FileConfiguration plotConfig, String world, String actualWorldName)
	{
		int pathWidth = plotConfig.getInt("worlds." + world + ".PathWidth"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".road.width", pathWidth);
		int plotSize = plotConfig.getInt("worlds." + world + ".PlotSize"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".plot.size", plotSize);
		String wallBlock = plotConfig.getString("worlds." + world + ".WallBlockId"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".wall.block", wallBlock);
		String floor = plotConfig.getString("worlds." + world + ".PlotFloorBlockId"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".plot.floor", Collections.singletonList(floor));
		String filling = plotConfig.getString("worlds." + world + ".PlotFillingBlockId"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".plot.filling", Collections.singletonList(filling));
		String road = plotConfig.getString("worlds." + world + ".RoadMainBlockId");
		PS.get().worlds.set("worlds." + actualWorldName + ".road.block", road);
		int height = plotConfig.getInt("worlds." + world + ".RoadHeight"); //
		PS.get().worlds.set("worlds." + actualWorldName + ".road.height", height);
		PS.get().worlds.set("worlds." + actualWorldName + ".plot.height", height);
		PS.get().worlds.set("worlds." + actualWorldName + ".wall.height", height);
	}

	default Location getPlotTopLocAbs(int path, int plot, PlotId plotId)
	{
		int px = plotId.x;
		int pz = plotId.y;
		int x = px * (path + plot) - (int) Math.floor(path / 2) - 1;
		int z = pz * (path + plot) - (int) Math.floor(path / 2) - 1;
		return new Location(null, x, 256, z);
	}

	default Location getPlotBottomLocAbs(int path, int plot, PlotId plotId)
	{
		int px = plotId.x;
		int pz = plotId.y;
		int x = px * (path + plot) - plot - (int) Math.floor(path / 2) - 1;
		int z = pz * (path + plot) - plot - (int) Math.floor(path / 2) - 1;
		return new Location(null, x, 1, z);
	}

	default void setMerged(HashMap<String, HashMap<PlotId, boolean[]>> merges, String world, PlotId id, int direction)
	{
		HashMap<PlotId, boolean[]> plots = merges.get(world);
		PlotId id2 = new PlotId(id.x, id.y);
		boolean[] merge1 = plots.containsKey(id) ? plots.get(id) : new boolean[]{false, false, false, false};
		boolean[] merge2 = plots.containsKey(id2) ? plots.get(id2) : new boolean[]{false, false, false, false};
		merge1[direction] = true;
		merge2[(direction + 2) % 4] = true;
		plots.put(id, merge1);
		plots.put(id2, merge1);
	}
}
