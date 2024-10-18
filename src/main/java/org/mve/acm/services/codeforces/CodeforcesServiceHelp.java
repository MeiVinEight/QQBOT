package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.service.Service;

import java.util.LinkedList;

public class CodeforcesServiceHelp extends Service<GroupMessageEvent>
{
	public static final String CMD_HELP_0 = "?";
	public static final String CMD_HELP_1 = "help";

	public CodeforcesServiceHelp(String name)
	{
		super(name);
	}

	@Override
	public void service(GroupMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		// TODO HELP
		event.getSubject().sendMessage(CodeforcesServiceHelp.CMD_HELP_0);
	}
}
