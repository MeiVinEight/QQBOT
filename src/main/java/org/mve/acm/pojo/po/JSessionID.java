package org.mve.acm.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = JSessionID.TABLE_NAME)
public class JSessionID
{
	public static final String TABLE_NAME = "JSESSIONID";
	public static final String COLUMN_UID = "UID";
	public static final String COLUMN_JSESSIONID = "JSESSIONID";

	@Column(name = COLUMN_UID)
	public long UID;
	@Column(name = COLUMN_JSESSIONID)
	public String JSESSIONID;
}
