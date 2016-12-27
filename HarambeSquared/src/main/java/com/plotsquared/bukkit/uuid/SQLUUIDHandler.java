package com.plotsquared.bukkit.uuid;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.collect.HashBiMap;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.config.Settings;
import com.intellectualcrafters.plot.database.SQLite;
import com.intellectualcrafters.plot.object.RunnableVal;
import com.intellectualcrafters.plot.object.StringWrapper;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.TaskManager;
import com.intellectualcrafters.plot.util.UUIDHandler;
import com.intellectualcrafters.plot.util.UUIDHandlerImplementation;
import com.intellectualcrafters.plot.uuid.UUIDWrapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SQLUUIDHandler extends UUIDHandlerImplementation {

	static final int MAX_REQUESTS = 500;
	private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	private static final int INTERVAL = 12000;
	private final JSONParser jsonParser = new JSONParser();
	private final SQLite sqlite;

	public SQLUUIDHandler(UUIDWrapper wrapper)
	{
		super(wrapper);
		this.sqlite = new SQLite(MainUtil.getFile(PS.get().IMP.getDirectory(), "usercache.db"));
		try
		{
			this.sqlite.openConnection();
		}
		catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}

		try (PreparedStatement stmt = this.getConnection().prepareStatement(
				"CREATE TABLE IF NOT EXISTS `usercache` (uuid VARCHAR(32) NOT NULL, username VARCHAR(32) NOT NULL, PRIMARY KEY (uuid, username)"
				+ ')'))
		{
			stmt.execute();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		this.startCaching(null);
	}

	private Connection getConnection()
	{
		synchronized (this.sqlite)
		{
			return this.sqlite.getConnection();
		}
	}

	@Override
	public boolean startCaching(Runnable whenDone)
	{
		if (!super.startCaching(whenDone))
		{
			return false;
		}
		TaskManager.runTaskAsync(new Runnable() {
			@Override
			public void run()
			{
				try
				{
					HashBiMap<StringWrapper, UUID> toAdd = HashBiMap.create(new HashMap<StringWrapper, UUID>());
					try (PreparedStatement statement = SQLUUIDHandler.this.getConnection().prepareStatement("SELECT `uuid`, `username` FROM `usercache`");
						 ResultSet resultSet = statement.executeQuery())
					{
						while (resultSet.next())
						{
							StringWrapper username = new StringWrapper(resultSet.getString("username"));
							UUID uuid = UUID.fromString(resultSet.getString("uuid"));
							toAdd.put(new StringWrapper(username.value), uuid);
						}
					}
					SQLUUIDHandler.this.add(toAdd);
					// This should be called as long as there are some unknown plots
					ArrayDeque<UUID> toFetch = new ArrayDeque<>();
					for (UUID u : UUIDHandler.getAllUUIDS())
					{
						if (!SQLUUIDHandler.this.uuidExists(u))
						{
							toFetch.add(u);
						}
					}
					if (toFetch.isEmpty())
					{
						if (whenDone != null)
						{
							whenDone.run();
						}
						return;
					}
					FileUUIDHandler fileHandler = new FileUUIDHandler(SQLUUIDHandler.this.uuidWrapper);
					fileHandler.startCaching(new Runnable() {
						@Override
						public void run()
						{
							// If the file based UUID handler didn't cache it, then we can't cache offline mode
							// Also, trying to cache based on files again, is useless as that's what the file based uuid cacher does
							if (Settings.UUID.OFFLINE)
							{
								if (whenDone != null)
								{
									whenDone.run();
								}
								return;
							}

							TaskManager.runTaskAsync(new Runnable() {
								@Override
								public void run()
								{
									try
									{
										if (toFetch.isEmpty())
										{
											if (whenDone != null)
											{
												whenDone.run();
											}
											return;
										}
										for (int i = 0; i < Math.min(500, toFetch.size()); i++)
										{
											UUID uuid = toFetch.pop();
											HttpURLConnection connection =
													(HttpURLConnection) new URL(SQLUUIDHandler.this.PROFILE_URL + uuid.toString().replace("-", ""))
																				.openConnection();
											InputStreamReader reader = new InputStreamReader(connection.getInputStream());
											JSONObject response = (JSONObject) SQLUUIDHandler.this.jsonParser.parse(reader);
											String name = (String) response.get("name");
											if (name != null)
											{
												SQLUUIDHandler.this.add(new StringWrapper(name), uuid);
											}
										}
									}
									catch (IOException | ParseException e)
									{
										e.printStackTrace();
									}
									TaskManager.runTaskLaterAsync(this, SQLUUIDHandler.this.INTERVAL);
								}
							});
						}
					});
				}
				catch (SQLException e)
				{
					throw new SQLUUIDHandlerException("Couldn't select :s", e);
				}
			}
		});
		return true;
	}

	@Override
	public void fetchUUID(String name, RunnableVal<UUID> ifFetch)
	{
		PS.debug(C.PREFIX + "UUID for '" + name + "' was null. We'll cache this from the Mojang servers!");
		if (ifFetch == null)
		{
			return;
		}
		TaskManager.runTaskAsync(() ->
								 {
									 try
									 {
										 URL url = new URL(this.PROFILE_URL);
										 HttpURLConnection connection = (HttpURLConnection) url.openConnection();
										 connection.setRequestMethod("POST");
										 connection.setRequestProperty("Content-Type", "application/json");
										 connection.setUseCaches(false);
										 connection.setDoInput(true);
										 connection.setDoOutput(true);
										 String body = JSONArray.toJSONString(Collections.singletonList(name));
										 OutputStream stream = connection.getOutputStream();
										 stream.write(body.getBytes());
										 stream.flush();
										 stream.close();
										 JSONArray array = (JSONArray) this.jsonParser.parse(new InputStreamReader(connection.getInputStream()));
										 JSONObject jsonProfile = (JSONObject) array.get(0);
										 String id = (String) jsonProfile.get("id");
										 String name1 = (String) jsonProfile.get("name");
										 ifFetch.value = UUID.fromString(
												 id.substring(0, 8) + '-' + id.substring(8, 12) + '-' + id.substring(12, 16) + '-' + id.substring(16, 20) + '-' + id
																																										  .substring(20, 32));
									 }
									 catch (IOException | ParseException e)
									 {
										 e.printStackTrace();
									 }
									 TaskManager.runTask(ifFetch);
								 });
	}

	@Override
	public void handleShutdown()
	{
		super.handleShutdown();
		try
		{
			this.getConnection().close();
		}
		catch (SQLException e)
		{
			throw new SQLUUIDHandlerException("Couldn't close database connection", e);
		}
	}

	@Override
	public boolean add(StringWrapper name, UUID uuid)
	{
		// Ignoring duplicates
		if (super.add(name, uuid))
		{
			TaskManager.runTaskAsync(() ->
									 {
										 try (PreparedStatement statement = this.getConnection().prepareStatement("REPLACE INTO usercache (`uuid`, `username`) VALUES(?, ?)"))
										 {
											 statement.setString(1, uuid.toString());
											 statement.setString(2, name.toString());
											 statement.execute();
											 PS.debug(C.PREFIX + "&cAdded '&6" + uuid + "&c' - '&6" + name + "&c'");
										 }
										 catch (SQLException e)
										 {
											 e.printStackTrace();
										 }
									 });
			return true;
		}
		return false;
	}

	/**
	 * This is useful for name changes
	 */
	@Override
	public void rename(UUID uuid, StringWrapper name)
	{
		super.rename(uuid, name);
		TaskManager.runTaskAsync(() ->
								 {
									 try (PreparedStatement statement = this.getConnection().prepareStatement("UPDATE usercache SET `username`=? WHERE `uuid`=?"))
									 {
										 statement.setString(1, name.value);
										 statement.setString(2, uuid.toString());
										 statement.execute();
										 PS.debug(C.PREFIX + "Name change for '" + uuid + "' to '" + name.value + '\'');
									 }
									 catch (SQLException e)
									 {
										 e.printStackTrace();
									 }
								 });
	}

	private static class SQLUUIDHandlerException extends RuntimeException {

		SQLUUIDHandlerException(String s, Throwable c)
		{
			super("SQLUUIDHandler caused an exception: " + s, c);
		}
	}
}
