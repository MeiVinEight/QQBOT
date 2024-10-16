package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.services.Service;

import java.util.LinkedList;

public class CodeforcesServiceCookie extends Service<GroupMessageEvent>
{
	public CodeforcesServiceCookie(String name)
	{
		super(name);
	}

	@Override
	public void service(GroupMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		String jsessionid = "";
		if (!contentList.isEmpty()) jsessionid = contentList.getFirst().toString();
		if (jsessionid.length() != 32)
		{
			event.getSubject().sendMessage("Wrong JSESSIONID");
			return;
		}

		// TODO Database Access
	}
}
