package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.controller.CodeforcesController;
import org.mve.service.Service;

import java.util.LinkedList;

public class CodeforcesServiceHelp extends Service<GroupMessageEvent>
{
	public CodeforcesServiceHelp(String name)
	{
		super(name);
	}

	@Override
	public void service(GroupMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		// TODO HELP
		event.getSubject().sendMessage(CodeforcesController.CMD_HELP_0);
	}
}
