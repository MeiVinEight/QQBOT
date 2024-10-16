package org.mve.mirai;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.List;

public class Message
{
	public static MessageChain message(List<SingleMessage> list)
	{
		MessageChainBuilder builder = new MessageChainBuilder(list.size());
		builder.addAll(list);
		return builder.build();
	}
}
