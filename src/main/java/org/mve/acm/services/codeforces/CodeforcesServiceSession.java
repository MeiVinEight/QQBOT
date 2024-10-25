package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.UserMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.QQBOT;
import org.mve.acm.pojo.dao.JSessionIDDAO;
import org.mve.acm.pojo.po.JSessionID;
import org.mve.mapper.Mapper;
import org.mve.service.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

public class CodeforcesServiceSession extends Service<UserMessageEvent>
{
	public static final String COMMAND_SESSION = "session";

	private final Mapper<JSessionID> mapper = new JSessionIDDAO(QQBOT.DATABASE);

	public CodeforcesServiceSession()
	{
		super(COMMAND_SESSION);
	}

	@Override
	public void service(UserMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		JSessionID session = new JSessionID();
		session.UID = event.getSender().getId();
		session = this.mapper.primary(session);

		if (contentList.isEmpty())
		{
			if (session != null)
			{
				StringBuilder builder = new StringBuilder(session.JSESSIONID);
				try
				{
					URL url = new URL(CodeforcesAPI.API_PROBLEMSET_SUBMIT);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("Host", CodeforcesAPI.CODEFORCES_HOST);
					conn.setRequestProperty("User-Agent", CodeforcesAPI.USER_AGENT);
					conn.setRequestProperty("Cookie", "JSESSIONID=" + session.JSESSIONID);
					if (conn.getResponseCode() == 302)
					{
						builder.append("❌");
					}
					else if (conn.getResponseCode() == 200)
					{
						builder.append("✅");
					}
					else
					{
						builder.append(":VERIFY FAILED");
					}
					conn.disconnect();
				}
				catch (IOException e)
				{
					QQBOT.BOT.getLogger().error(e);
					builder.append(":VERIFY FAILED");
				}
				event.getSubject().sendMessage(builder.toString());
			}
			else
			{
				event.getSubject().sendMessage("NO JSESSIONID SET");
			}
			return;
		}

		String jsessionid = contentList.getFirst().toString();
		if (jsessionid.length() != 32)
		{
			event.getSubject().sendMessage("WRONG JSESSIONID");
			return;
		}

		if (session != null)
		{
			session.JSESSIONID = jsessionid;
			this.mapper.update(session);
		}
		else
		{
			session = new JSessionID();
			session.UID = event.getSender().getId();
			session.JSESSIONID = jsessionid;
			this.mapper.insert(session);
		}
		event.getSubject().sendMessage("JSESSIONID UPDATED");
	}
}
