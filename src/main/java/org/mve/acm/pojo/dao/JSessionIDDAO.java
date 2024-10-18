package org.mve.acm.pojo.dao;

import org.mve.acm.pojo.po.JSessionID;
import org.mve.mapper.SimpleMapper;
import org.mve.sql.Database;

public class JSessionIDDAO extends SimpleMapper<JSessionID>
{
	public JSessionIDDAO(Database database)
	{
		super(database);
	}
}
