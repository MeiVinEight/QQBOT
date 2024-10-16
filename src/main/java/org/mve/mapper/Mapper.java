package org.mve.mapper;

import java.util.List;

public interface Mapper<T>
{
	// Insert a new object
	public boolean insert(T o);
	// Get an object by primary key
	public T primary(T o);
	// Select objects by given params
	public List<T> select(T o);
	// Update an object by primary key
	public boolean update(T o);
	// Delete an object by primary key
	public boolean delete(T o);
}
