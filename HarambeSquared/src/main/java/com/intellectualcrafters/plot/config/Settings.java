package com.intellectualcrafters.plot.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.intellectualcrafters.configuration.file.YamlConfiguration;

public class Settings extends Config {

    /*
	START OF CONFIGURATION SECTION:
    NOTE: Fields are saved in declaration order, classes in reverse order
     */

	@Config.Comment("These first 4 aren't configurable") // This is a comment
	@Final // Indicates that this value isn't configurable
	public static final String ISSUES = "https://github.com/IntellectualSites/PlotSquared/issues";
	@Final
	public static final String WIKI = "https://github.com/IntellectualSites/PlotSquared/wiki";
	@Final
	public static String VERSION; // These values are set from PS before loading
	@Final
	public static String PLATFORM; // These values are set from PS before loading

	@Config.Comment("Show additional information in console")
	public static boolean DEBUG = true;
	@Config.Comment({"The big annoying text that appears when you enter a plot", "For a single plot: `/plot flag set titles false`", "For just you: `/plot toggle titles`"})
	public static boolean TITLES = true;

	@Config.Create // This value will be generated automatically
	public static Config.ConfigBlock<Auto_Clear> AUTO_CLEAR; // A ConfigBlock is a section that can have multiple instances e.g. multiple expiry tasks

	public static void save(File file)
	{
		save(file, Settings.class);
	}

	public static void load(File file)
	{
		load(file, Settings.class);
	}

	public static boolean convertLegacy(File file)
	{
		if (!file.exists())
		{
			return false;
		}
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		// Protection
		Redstone.DISABLE_OFFLINE = config.getBoolean("protection.redstone.disable-offline");
		Redstone.DISABLE_UNOCCUPIED = config.getBoolean("protection.redstone.disable-unoccupied", Redstone.DISABLE_UNOCCUPIED);

		// PlotMe
		PlotMe.ALIAS = config.getBoolean("plotme-alias", PlotMe.ALIAS);
		Enabled_Components.PLOTME_CONVERTER = config.getBoolean("plotme-convert.enabled", Enabled_Components.PLOTME_CONVERTER);
		PlotMe.CACHE_UUDS = config.getBoolean("plotme-convert.cache-uuids", PlotMe.CACHE_UUDS);

		// UUID
		UUID.USE_SQLUUIDHANDLER = config.getBoolean("uuid.use_sqluuidhandler", UUID.USE_SQLUUIDHANDLER);
		UUID.OFFLINE = config.getBoolean("UUID.offline", UUID.OFFLINE);
		UUID.FORCE_LOWERCASE = config.getBoolean("UUID.force-lowercase", UUID.FORCE_LOWERCASE);

		// Mob stuff
		Enabled_Components.KILL_ROAD_MOBS = config.getBoolean("kill_road_mobs", Enabled_Components.KILL_ROAD_MOBS);
		Enabled_Components.KILL_ROAD_VEHICLES = config.getBoolean("kill_road_vehicles", Enabled_Components.KILL_ROAD_VEHICLES);

		// Clearing + Expiry
		//FAST_CLEAR = config.getBoolean("clear.fastmode");
		Enabled_Components.PLOT_EXPIRY = config.getBoolean("clear.auto.enabled", Enabled_Components.PLOT_EXPIRY);
		if (Enabled_Components.PLOT_EXPIRY)
		{
			Enabled_Components.BAN_DELETER = config.getBoolean("clear.on.ban");
			AUTO_CLEAR = new Config.ConfigBlock<>();
			AUTO_CLEAR.put("task1", new Auto_Clear());
			Auto_Clear task = AUTO_CLEAR.get("task1");
			task.CALIBRATION = new Auto_Clear.CALIBRATION();

			task.DAYS = config.getInt("clear.auto.days", task.DAYS);
			task.THRESHOLD = config.getInt("clear.auto.threshold", task.THRESHOLD);
			task.CONFIRMATION = config.getBoolean("clear.auto.confirmation", task.CONFIRMATION);
			task.CALIBRATION.CHANGES = config.getInt("clear.auto.calibration.changes", task.CALIBRATION.CHANGES);
			task.CALIBRATION.FACES = config.getInt("clear.auto.calibration.faces", task.CALIBRATION.FACES);
			task.CALIBRATION.DATA = config.getInt("clear.auto.calibration.data", task.CALIBRATION.DATA);
			task.CALIBRATION.AIR = config.getInt("clear.auto.calibration.air", task.CALIBRATION.AIR);
			task.CALIBRATION.VARIETY = config.getInt("clear.auto.calibration.variety", task.CALIBRATION.VARIETY);
			task.CALIBRATION.CHANGES_SD = config.getInt("clear.auto.calibration.changes_sd", task.CALIBRATION.CHANGES_SD);
			task.CALIBRATION.FACES_SD = config.getInt("clear.auto.calibration.faces_sd", task.CALIBRATION.FACES_SD);
			task.CALIBRATION.DATA_SD = config.getInt("clear.auto.calibration.data_sd", task.CALIBRATION.DATA_SD);
			task.CALIBRATION.AIR_SD = config.getInt("clear.auto.calibration.air_sd", task.CALIBRATION.AIR_SD);
			task.CALIBRATION.VARIETY_SD = config.getInt("clear.auto.calibration.variety_sd", task.CALIBRATION.VARIETY_SD);
		}

		// Done
		Done.REQUIRED_FOR_RATINGS = config.getBoolean("approval.ratings.check-done", Done.REQUIRED_FOR_RATINGS);
		Done.COUNTS_TOWARDS_LIMIT = config.getBoolean("approval.done.counts-towards-limit", Done.COUNTS_TOWARDS_LIMIT);
		Done.RESTRICT_BUILDING = config.getBoolean("approval.done.restrict-building", Done.RESTRICT_BUILDING);
		Done.REQUIRED_FOR_DOWNLOAD = config.getBoolean("approval.done.required-for-download", Done.REQUIRED_FOR_DOWNLOAD);

		// Schematics
		Paths.SCHEMATICS = config.getString("schematics.save_path", Paths.SCHEMATICS);
		Paths.BO3 = config.getString("bo3.save_path", Paths.BO3);

		// Web
		Web.URL = config.getString("web.url", Web.URL);
		Web.SERVER_IP = config.getString("web.server-ip", Web.SERVER_IP);

		// Caching
		Enabled_Components.PERMISSION_CACHE = config.getBoolean("cache.permissions", Enabled_Components.PERMISSION_CACHE);
		Enabled_Components.RATING_CACHE = config.getBoolean("cache.ratings", Enabled_Components.RATING_CACHE);

		// Rating system
		Ratings.CATEGORIES = config.contains("ratings.categories") ? config.getStringList("ratings.categories") : Ratings.CATEGORIES;

		// Titles
		TITLES = config.getBoolean("titles", TITLES);

		// Teleportation
		Teleport.DELAY = config.getInt("teleport.delay", Teleport.DELAY);
		Teleport.ON_LOGIN = config.getBoolean("teleport.on_login", Teleport.ON_LOGIN);
		Teleport.ON_DEATH = config.getBoolean("teleport.on_death", Teleport.ON_DEATH);

		// WorldEdit
		//WE_ALLOW_HELPER = config.getBoolean("worldedit.enable-for-helpers");

		// Chunk processor
		Enabled_Components.CHUNK_PROCESSOR = config.getBoolean("chunk-processor.enabled", Enabled_Components.CHUNK_PROCESSOR);
		Chunk_Processor.AUTO_TRIM = config.getBoolean("chunk-processor.auto-unload", Chunk_Processor.AUTO_TRIM);
		Chunk_Processor.MAX_TILES = config.getInt("chunk-processor.max-blockstates", Chunk_Processor.MAX_TILES);
		Chunk_Processor.MAX_ENTITIES = config.getInt("chunk-processor.max-entities", Chunk_Processor.MAX_ENTITIES);
		Chunk_Processor.DISABLE_PHYSICS = config.getBoolean("chunk-processor.disable-physics", Chunk_Processor.DISABLE_PHYSICS);

		// Comments
		Enabled_Components.COMMENT_NOTIFIER = config.getBoolean("comments.notifications.enabled", Enabled_Components.COMMENT_NOTIFIER);

		// Plot limits
		Claim.MAX_AUTO_AREA = config.getInt("claim.max-auto-area", Claim.MAX_AUTO_AREA);
		Limit.MAX_PLOTS = config.getInt("max_plots", Limit.MAX_PLOTS);
		Limit.GLOBAL = config.getBoolean("global_limit", Limit.GLOBAL);

		// Misc
		DEBUG = config.getBoolean("debug", DEBUG);
		Chat.CONSOLE_COLOR = config.getBoolean("console.color", Chat.CONSOLE_COLOR);
		Chat.INTERACTIVE = config.getBoolean("chat.fancy", Chat.INTERACTIVE);

		Enabled_Components.DATABASE_PURGER = config.getBoolean("auto-purge", Enabled_Components.DATABASE_PURGER);
		return true;
	}

	@Config.Comment("This is an auto clearing task called `task1`")
	@Config.BlockName("task1") // The name for the default block
	public static final class Auto_Clear extends Config.ConfigBlock {
		@Config.Create // This value has to be generated since an instance isn't static
		public CALIBRATION CALIBRATION;
		public int THRESHOLD = 1;
		public int REQUIRED_PLOTS = -1;
		public boolean CONFIRMATION = true;
		public int DAYS = 7;
		public List<String> WORLDS = new ArrayList<>(Collections.singletonList("*"));

		@Config.Comment("See: https://github.com/IntellectualSites/PlotSquared/wiki/Plot-analysis")
		public static final class CALIBRATION {
			public int VARIETY;
			public int VARIETY_SD;
			public int CHANGES;
			public int CHANGES_SD = 1;
			public int FACES;
			public int FACES_SD;
			public int DATA_SD;
			public int AIR;
			public int AIR_SD;
			public int DATA;
		}
	}

	public static final class Chunk_Processor {
		@Config.Comment("Auto trim will not save chunks which aren't claimed")
		public static boolean AUTO_TRIM;
		@Config.Comment("Max tile entities per chunk")
		public static int MAX_TILES = 4096;
		@Config.Comment("Max entities per chunk")
		public static int MAX_ENTITIES = 512;
		@Config.Comment("Disable block physics")
		public static boolean DISABLE_PHYSICS;
	}

	public static final class UUID {
		@Config.Comment("Force PlotSquared to use offline UUIDs (it usually detects the right mode)")
		public static boolean OFFLINE;
		@Config.Comment("Force PlotSquared to use lowercase UUIDs")
		public static boolean FORCE_LOWERCASE;
		@Config.Comment("Use a database to store UUID/name info")
		public static boolean USE_SQLUUIDHANDLER;
		@Config.Ignore
		public static boolean NATIVE_UUID_PROVIDER;
	}

	@Config.Comment("Configure the paths PlotSquared will use")
	public static final class Paths {
		public static String SCHEMATICS = "schematics";
		public static String BO3 = "bo3";
		public static String SCRIPTS = "scripts";
		public static String TEMPLATES = "templates";
		public static String TRANSLATIONS = "translations";
	}

	public static final class Web {
		@Config.Comment("We are already hosting a web interface for you:")
		public static String URL = "http://empcraft.com/plots/";
		@Config.Comment("The ip that will show up in the interface")
		public static String SERVER_IP = "your.ip.here";
	}

	public static final class Done {
		@Config.Comment("Require a done plot to download")
		public static boolean REQUIRED_FOR_DOWNLOAD;
		@Config.Comment("Only done plots can be rated")
		public static boolean REQUIRED_FOR_RATINGS;
		@Config.Comment("Restrict building when a plot is done")
		public static boolean RESTRICT_BUILDING;
		@Config.Comment("The limit being how many plots a player can claim")
		public static boolean COUNTS_TOWARDS_LIMIT = true;
	}

	public static final class Chat {
		@Config.Comment("Sometimes console color doesn't work, you can disable it here")
		public static boolean CONSOLE_COLOR = true;
		@Config.Comment("Should chat be interactive")
		public static boolean INTERACTIVE = true;
	}

	@Config.Comment("Relating to how many plots someone can claim  ")
	public static final class Limit {
		@Config.Comment("Should the limit be global (over multiple worlds)")
		public static boolean GLOBAL;
		@Config.Comment("The range of permissions to check e.g. plots.plot.127")
		public static int MAX_PLOTS = 127;
	}

	@Config.Comment("Switching from PlotMe?")
	public static final class PlotMe {
		@Config.Comment("Cache the uuids from the PlotMe database")
		public static boolean CACHE_UUDS;
		@Config.Comment("Have `/plotme` as a command alias")
		public static boolean ALIAS;
	}

	public static final class Teleport {
		@Config.Comment("Teleport to your plot on death")
		public static boolean ON_DEATH;
		@Config.Comment("Teleport to your plot on login")
		public static boolean ON_LOGIN;
		@Config.Comment("Add a teleportation delay to all commands")
		public static int DELAY;
	}

	public static final class Redstone {
		@Config.Comment("Disable redstone in unoccupied plots")
		public static boolean DISABLE_UNOCCUPIED;
		@Config.Comment("Disable redstone when all owners/trusted/members are offline")
		public static boolean DISABLE_OFFLINE;
	}

	public static final class Claim {
		@Config.Comment("The max plots claimed in a single `/plot auto <size>` command")
		public static int MAX_AUTO_AREA = 4;
	}

	public static final class Ratings {
		public static List<String> CATEGORIES = new ArrayList<>();
	}

	@Config.Comment({"Enable or disable part of the plugin", "Note: A cache will use some memory if enabled"})
	public static final class Enabled_Components { // Group the following values into a new config section
		@Config.Comment("The database stores all the plots")
		public static boolean DATABASE = true;
		@Config.Comment("Events are needed to track a lot of things")
		public static boolean EVENTS = true;
		@Config.Comment("Commands are used to interact with the plugin")
		public static boolean COMMANDS = true;
		@Config.Comment("The UUID cacher is used to resolve player names")
		public static boolean UUID_CACHE = true;
		@Config.Comment("Stores user metadata in a database")
		public static boolean PERSISTENT_META = true;
		@Config.Comment("Optimizes permission checks")
		public static boolean PERMISSION_CACHE = true;
		@Config.Comment("Optimizes block changing code")
		public static boolean BLOCK_CACHE = true;
		@Config.Comment("Getting a rating won't need the database")
		public static boolean RATING_CACHE = true;
		@Config.Comment("The converter will attempt to convert the PlotMe database")
		public static boolean PLOTME_CONVERTER = true;
		@Config.Comment("Allow WorldEdit to be restricted to plots")
		public static boolean WORLDEDIT_RESTRICTIONS = true;
		@Config.Comment("Expiry will clear old or simplistic plots")
		public static boolean PLOT_EXPIRY;
		@Config.Comment("Processes chunks (trimming, or entity/tile limits) ")
		public static boolean CHUNK_PROCESSOR;
		@Config.Comment("Kill mobs or vehicles on roads")
		public static boolean KILL_ROAD_MOBS;
		public static boolean KILL_ROAD_VEHICLES;
		@Config.Comment("Notify a player of any missed comments upon plot entry")
		public static boolean COMMENT_NOTIFIER;
		@Config.Comment("Actively purge invalid database entries")
		public static boolean DATABASE_PURGER;
		@Config.Comment("Delete plots when a player is banned")
		public static boolean BAN_DELETER;
	}
}
