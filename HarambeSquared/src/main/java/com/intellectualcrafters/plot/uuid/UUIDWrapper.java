package com.intellectualcrafters.plot.uuid;

import java.util.UUID;

import com.intellectualcrafters.plot.object.OfflinePlotPlayer;
import com.intellectualcrafters.plot.object.PlotPlayer;

public interface UUIDWrapper {

	UUID getUUID(PlotPlayer player);

	UUID getUUID(OfflinePlotPlayer player);

	UUID getUUID(String name);

	OfflinePlotPlayer getOfflinePlayer(UUID uuid);

	OfflinePlotPlayer getOfflinePlayer(String name);

	OfflinePlotPlayer[] getOfflinePlayers();
}
