package org.mve.mapper;

import org.mve.sql.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class Mapper<T>
{
	private final Database database;

	public Mapper(Database database)
	{
		this.database = database;
	}

	// Insert a new object
	public abstract boolean insert(T o);
	// Get an object by primary key
	public abstract T primary(T o);
	// Get objects by given params
	public abstract List<T> select(Map<String, Object> where, Class<T> type);
	// Update an object by primary key
	public abstract boolean update(T o);
	// Delete an object by primary key
	public abstract boolean delete(T o);

	public Connection connection() throws SQLException
	{
		return DriverManager.getConnection(this.database.address, this.database.username, this.database.password);
	}
}
