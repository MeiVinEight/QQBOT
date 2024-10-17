package org.mve.mapper;

public interface Mapper<T>
{
	// Insert a new object
	public boolean insert(T o);
	// Get an object by primary key
	public T primary(T o);
	// Update an object by primary key
	public boolean update(T o);
	// Delete an object by primary key
	public boolean delete(T o);
}
