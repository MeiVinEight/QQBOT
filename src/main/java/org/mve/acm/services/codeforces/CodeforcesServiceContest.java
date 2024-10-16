package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.services.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class CodeforcesServiceContest extends Service<GroupMessageEvent>
{
	public CodeforcesServiceContest(String name)
	{
		super(name);
	}

	@Override
	public void service(GroupMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		event.getSubject().sendMessage(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
	}
}
