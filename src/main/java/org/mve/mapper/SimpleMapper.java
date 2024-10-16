package org.mve.mapper;

import org.mve.services.ServicesManager;

import javax.persistence.Table;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimpleMapper<T> implements Mapper<T>
{
	@Override
	public boolean insert(T o)
	{
		return false;
	}

	@Override
	public T primary(T o)
	{
		Class<?> clazz = o.getClass();
		String tableName = o.getClass().getSimpleName();
		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null)
		{
			tableName = tableAnno.name();
		}
		T t = null;
		try (Connection conn = SimpleMapper.connection())
		{
			ResultSet primKey = conn.getMetaData().getPrimaryKeys(null, null, tableName);
			Set<String> primaryKeys = new HashSet<>();
			while (primKey.next())
			{
				primaryKeys.add(primKey.getString("COLUMN_NAME"));
			}
		}
		catch (Throwable exce)
		{
			ServicesManager.BOT.getLogger().error(exce);
		}
		return null;
	}

	@Override
	public List<T> select(T o)
	{
		return null;
	}

	@Override
	public boolean update(T o)
	{
		return false;
	}

	@Override
	public boolean delete(T o)
	{
		return false;
	}

	public static Connection connection()
	{
		return null;
	}
}
