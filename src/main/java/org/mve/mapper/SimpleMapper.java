package org.mve.mapper;

import org.mve.invoke.ConstructorAccessor;
import org.mve.invoke.FieldAccessor;
import org.mve.invoke.MagicAccessor;
import org.mve.invoke.ReflectionFactory;
import org.mve.invoke.common.JavaVM;
import org.mve.services.ServicesManager;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SimpleMapper<T> implements Mapper<T>
{
	@Override
	public boolean insert(T o)
	{
		Class<?> clazz = o.getClass();
		String tableName = o.getClass().getSimpleName();
		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null) tableName = tableAnno.name();

		try (Connection conn = SimpleMapper.connection())
		{
			StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(' ');
			StringBuilder columnBuilder = new StringBuilder("(");
			StringBuilder valuesBuilder = new StringBuilder("(");
			Field[] fields = MagicAccessor.accessor.getFields(clazz);
			for (int i = 0; i < fields.length; i++)
			{
				Field field = fields[i];
				String columnName = field.getName();
				Column columnAnno = field.getAnnotation(Column.class);
				if (columnAnno != null) columnName = columnAnno.name();
				if (i > 0)
				{
					columnBuilder.append(", ");
					valuesBuilder.append(", ");
				}
				columnBuilder.append(columnName);
				valuesBuilder.append('?');
			}
			columnBuilder.append(')');
			valuesBuilder.append(')');
			sql.append(columnBuilder).append(" VALUES ").append(valuesBuilder).append(';');
			ServicesManager.BOT.getLogger().verbose(sql.toString());

			try (PreparedStatement stmt = conn.prepareStatement(sql.toString()))
			{
				for (int i = 0; i < fields.length; i++)
				{
					Field field = fields[i];
					FieldAccessor<?> facc = ReflectionFactory.access(field);
					stmt.setObject(i + 1, facc.get(o));
				}
				int x = stmt.executeUpdate();
				return x > 0;
			}
		}
		catch (SQLException t)
		{
			ServicesManager.BOT.getLogger().error(t);
		}
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
			Set<String> primKeys = SimpleMapper.primaryKey(tableName);

			Field[] fields = MagicAccessor.accessor.getFields(clazz);
			List<FieldAccessor<?>> primaryKeys = new LinkedList<>();
			StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE ");

			for (Field field : fields)
			{
				String columnName = field.getName();
				Column column = field.getAnnotation(Column.class);
				if (column != null) columnName = column.name();

				if (primKeys.contains(columnName))
				{
					if (!primaryKeys.isEmpty())
					{
						sql.append(" AND ");
					}
					primaryKeys.add(ReflectionFactory.access(field));
					sql.append(columnName)
						.append(" = ?");
				}
			}
			sql.append(';');
			ServicesManager.BOT.getLogger().verbose(sql.toString());

			try (PreparedStatement stmt = conn.prepareStatement(sql.toString()))
			{
				// for (FieldAccessor<?> facc : primaryKeys)
				for (int i = 0; i < primaryKeys.size(); i++)
				{
					FieldAccessor<?> facc = primaryKeys.get(i);
					stmt.setObject(i + 1, facc.get(o));
				}

				try (ResultSet rs = stmt.executeQuery())
				{
					rs.next();

					Constructor<T> noArg = MagicAccessor.accessor.getConstructor(clazz);
					if (noArg == null) throw new NullPointerException("No argument constructor not found for " + clazz);
					ConstructorAccessor<T> ctor = ReflectionFactory.access(noArg);
					t = ctor.invoke();
					SimpleMapper.convert(rs, t);
				}
			}

		}
		catch (Throwable exce)
		{
			ServicesManager.BOT.getLogger().error(exce);
		}
		return t;
	}

	@Override
	public boolean update(T o)
	{
		Class<?> clazz = o.getClass();
		String tableName = o.getClass().getSimpleName();
		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null)
		{
			tableName = tableAnno.name();
		}
		try (Connection conn = SimpleMapper.connection())
		{
			StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
			StringBuilder where = new StringBuilder(" WHERE ");

			Set<String> primKeys = SimpleMapper.primaryKey(tableName);
			Field[] fields = MagicAccessor.accessor.getFields(clazz);
			int setCount = 0;
			int whereCount = 0;
			for (Field field : fields)
			{
				String columnName = field.getName();
				Column columnAnno = field.getAnnotation(Column.class);
				if (columnAnno != null) columnName = columnAnno.name();
				if (primKeys.contains(columnName))
				{
					if (whereCount > 0) where.append(" AND ");
					where.append(columnName).append(" = ?");
					whereCount++;
				}
				else
				{
					if (setCount > 0) sql.append(", ");
					sql.append(columnName).append(" = ?");
					setCount++;
				}
			}

			sql.append(where).append(';');
			ServicesManager.BOT.getLogger().verbose(sql.toString());
			try (PreparedStatement stmt = conn.prepareStatement(sql.toString()))
			{
				int idxs = 0;
				int idxw = 0;
				for (Field field : fields)
				{
					FieldAccessor<?> accessor = ReflectionFactory.access(field);
					String columnName = field.getName();
					Column columnAnno = field.getAnnotation(Column.class);
					if (columnAnno != null) columnName = columnAnno.name();
					if (primKeys.contains(columnName))
					{
						idxw++;
						stmt.setObject(setCount + idxw, accessor.get(o));
					}
					else
					{
						idxs++;
						stmt.setObject(idxs, accessor.get(o));
					}
				}
				return stmt.executeUpdate() > 0;
			}
		}
		catch (SQLException e)
		{
			ServicesManager.BOT.getLogger().error(e);
		}
		return false;
	}

	@Override
	public boolean delete(T o)
	{
		Class<?> clazz = o.getClass();
		String tableName = o.getClass().getSimpleName();
		Table tableAnno = clazz.getAnnotation(Table.class);
		if (tableAnno != null)
		{
			tableName = tableAnno.name();
		}
		try (Connection conn = SimpleMapper.connection())
		{
			StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
			Set<String> primKeys = SimpleMapper.primaryKey(tableName);
			Field[] fields = MagicAccessor.accessor.getFields(clazz);
			List<Object> args = new ArrayList<>(primKeys.size());
			for (Field field : fields)
			{
				String columnName = field.getName();
				Column columnAnno = field.getAnnotation(Column.class);
				if (columnAnno != null) columnName = columnAnno.name();
				if (primKeys.contains(columnName))
				{
					FieldAccessor<?> facc = ReflectionFactory.access(field);
					if (!args.isEmpty()) sql.append(" AND ");
					sql.append(columnName).append(" = ?");
					args.add(facc.get(o));
				}
			}

			sql.append(';');
			ServicesManager.BOT.getLogger().verbose(sql.toString());
			try (PreparedStatement stmt = conn.prepareStatement(sql.toString()))
			{
				for (int i = 0; i < args.size(); i++)
				{
					stmt.setObject(i + 1, args.get(i));
				}

				return stmt.executeUpdate() > 0;
			}
		}
		catch (SQLException e)
		{
			ServicesManager.BOT.getLogger().error(e);
		}
		return false;
	}

	public static Connection connection() throws SQLException
	{
		return DriverManager.getConnection(ServicesManager.MYSQL_URL, ServicesManager.MYSQL_USERNAME, ServicesManager.MYSQL_PASSWORD);
	}

	public static Set<String> primaryKey(String tableName)
	{
		Set<String> primKeys = new HashSet<>();
		try (Connection conn = SimpleMapper.connection())
		{
			try (ResultSet primKeyRS = conn.getMetaData().getPrimaryKeys(null, null, tableName))
			{
				while (primKeyRS.next())
				{
					primKeys.add(primKeyRS.getString("COLUMN_NAME"));
				}
			}
		}
		catch (SQLException e)
		{
			JavaVM.exception(e);
		}
		return primKeys;
	}

	public static <T> void convert(ResultSet rs, T o) throws SQLException
	{
		Field[] fields = MagicAccessor.accessor.getFields(o.getClass());
		for (Field field : fields)
		{
			FieldAccessor<?> facc = ReflectionFactory.access(field);
			String columnName = field.getName();
			Column column = field.getAnnotation(Column.class);
			if (column != null) columnName = column.name();
			Class<?> columnType = field.getType();
			facc.set(o, rs.getObject(columnName, columnType));
		}
	}
}
