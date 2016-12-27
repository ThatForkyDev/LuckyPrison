package com.ulfric.prison.listeners;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.ulfric.lib.api.entity.Metadata;
import com.ulfric.lib.api.hook.Hooks;
import com.ulfric.lib.api.java.StringUtils;
import com.ulfric.lib.api.java.Strings;
import com.ulfric.lib.api.locale.Locale;
import com.ulfric.lib.api.server.Commands;
import com.ulfric.lib.api.task.Tasks;
import com.ulfric.lib.api.time.Ticks;
import com.ulfric.prison.lang.Permissions;
import com.ulfric.uspigot.event.server.CommandPostprocessEvent;

public class ListenerConnection implements Listener {

	private static final ListenerConnection INSTANCE = new ListenerConnection();
	public static ListenerConnection get() { return ListenerConnection.INSTANCE; }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostCommand(CommandPostprocessEvent event)
	{
		if (event.isValid()) return;

		CommandSender sender = event.getSender();

		if (!(sender instanceof Player)) return;

		if (!sender.hasPermission("prison.alias")) return;

		String command = event.getMessage().toLowerCase();

		List<String> aliases = Hooks.DATA.getPlayerDataAsStringList(((Player) sender).getUniqueId(), "prison.aliases");

		for (String string : aliases)
		{
			String find = StringUtils.findOption(string, "cmd");

			if (find == null) continue;

			if (!find.equals(command)) continue;

			String execute = StringUtils.findOption(string, "exe");

			if (execute == null) continue;

			execute = execute.replace(Strings.FAKE_SPACE, " ");

			Commands.dispatch(sender, execute);

			event.setValid(true);

			return;
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		player.setWalkSpeed(0.25F);

		Tasks.runLater(() -> Locale.send(player, "prison.using_locale", Locale.getLanguage(player)), Ticks.fromSeconds(2.5));

		if (!player.hasPermission(Permissions.JOIN_PARTICLE) || Metadata.removeIfPresent(player, "vanished")) return;

		Tasks.runLater(() ->
		{
			Location location = player.getLocation().add(0, 0.65, 0);
			Location[] locations = {location.clone(), location.clone(), location.clone(), location.clone()};
			World world = location.getWorld();
			for (int x = 0; x < 4; x++)
			{
				locations[0].add(0.25, 0, 0);
				locations[1].add(0, 0, 0.25);
				locations[2].subtract(0.25, 0, 0);
				locations[3].subtract(0, 0, 0.25);
				for (Location loc : locations)
				{
					world.playEffect(loc, Effect.COLOURED_DUST, 5);
				}
			}
		}, Ticks.SECOND);
	}

}