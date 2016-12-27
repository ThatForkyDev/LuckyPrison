package com.intellectualcrafters.plot.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.intellectualcrafters.plot.util.StringMan;

public interface StmtMod<T> {

	String getCreateMySQL(int size);

	default String getCreateMySQL(int size, String query, int params)
	{
		StringBuilder statement = new StringBuilder(query);
		for (int i = 0; i < size - 1; i++)
		{
			statement.append('(').append(StringMan.repeat(",?", params).substring(1)).append("),");
		}
		statement.append('(').append(StringMan.repeat(",?", params).substring(1)).append(')');
		return statement.toString();
	}

	default String getCreateSQLite(int size, String query, int params)
	{
		StringBuilder statement = new StringBuilder(query);
		String modParams = StringMan.repeat(",?", params).substring(1);
		for (int i = 0; i < size - 1; i++)
		{
			statement.append("UNION SELECT ").append(modParams).append(' ');
		}
		return statement.toString();
	}

	String getCreateSQLite(int size);

	String getCreateSQL();

	void setMySQL(PreparedStatement stmt, int i, T obj) throws SQLException;

	void setSQLite(PreparedStatement stmt, int i, T obj) throws SQLException;

	void setSQL(PreparedStatement stmt, T obj) throws SQLException;
}
