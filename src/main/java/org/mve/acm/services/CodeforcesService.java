package org.mve.acm.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.collect.CollectorArray;
import org.mve.invoke.common.JavaVM;
import org.mve.mirai.Message;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CodeforcesService implements Consumer<GroupMessageEvent>
{
	private static final String CODEFORCES_PROPERTIES_FILENAME = "codeforces.json";
	private static final String PROPERTIES_KEY_ADMIN = "admin";
	private static final String PROPERTIES_KEY_GROUP = "group";
	private static final String PROPERTIES_KEY_CMDSYM = "cmdsym";
	private static final String PROPERTIES_KEY_CMDPFX = "cmdpfx";

	private static final Set<Long> ADMINQQ = new HashSet<>();
	private static final Set<Long> GROUPQQ = new HashSet<>();
	private static final String COMMAND_SYMBOL;
	private static final String COMMAND_PREFIX;

	@Override
	public void accept(GroupMessageEvent event)
	{
		if (CodeforcesService.GROUPQQ.contains(event.getGroup().getId()))
		{

			// Filter MessageContent
			List<SingleMessage> contentList = event.getMessage()
				.stream().filter(MessageContent.class::isInstance)
				.collect(new CollectorArray<>(new LinkedList<>()));
			System.out.println(contentList.getClass());

			if (contentList.get(0).contentToString().startsWith(COMMAND_SYMBOL + COMMAND_PREFIX))
			{
				String content = contentList.remove(0).toString();
				content = content.substring(COMMAND_SYMBOL.length() + COMMAND_PREFIX.length())
					.stripLeading();
				if (!content.isEmpty()) contentList.add(0, new PlainText(content));
				// System.out.println(messageContent);
				event.getSubject().sendMessage(Message.message(contentList));
			}
		}
	}

	static
	{
		JsonObject property = null;
		try (FileInputStream propertyFile = new FileInputStream(CodeforcesService.CODEFORCES_PROPERTIES_FILENAME))
		{
			InputStreamReader reader = new InputStreamReader(propertyFile);

			// Resolve json properties
			property = (JsonObject) JsonParser.parseReader(reader);
		}
		catch (Throwable t)
		{
			JavaVM.exception(t);
		}

		assert property != null;
		// Read admin qq(s)
		JsonArray jsonAdminArray = property.getAsJsonArray(PROPERTIES_KEY_ADMIN);
		for (JsonElement jsonAdmin : jsonAdminArray)
		{
			long adminqq = jsonAdmin.getAsLong();
			CodeforcesService.ADMINQQ.add(adminqq);
		}
		// Read group qq(s)
		JsonArray jsonGroupArray = property.getAsJsonArray(PROPERTIES_KEY_GROUP);
		for (JsonElement jsonAdmin : jsonGroupArray)
		{
			long groupqq = jsonAdmin.getAsLong();
			CodeforcesService.GROUPQQ.add(groupqq);
		}
		// Read command settings
		COMMAND_SYMBOL = property.get(PROPERTIES_KEY_CMDSYM).getAsString();
		COMMAND_PREFIX = property.get(PROPERTIES_KEY_CMDPFX).getAsString();
	}
}
