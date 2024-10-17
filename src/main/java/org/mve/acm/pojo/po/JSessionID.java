package org.mve.acm.pojo.po;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "JSESSIONID")
public class JSessionID
{
	@Column(name = "GID")
	public Long GID;
	@Column(name = "UID")
	public long UID;
	@Column(name = "JSESSIONID")
	public String JSESSIONID;
}
