package org.mve.acm.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageContent;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.services.ComparatorService;
import org.mve.acm.services.Service;
import org.mve.acm.services.codeforces.CodeforcesServiceContest;
import org.mve.acm.services.codeforces.CodeforcesServiceCookie;
import org.mve.acm.services.codeforces.CodeforcesServiceHelp;
import org.mve.collect.CollectorArray;
import org.mve.invoke.common.JavaVM;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class CodeforcesController implements Consumer<GroupMessageEvent>
{
	private static final String CODEFORCES_PROPERTIES_FILENAME = "codeforces.json";
	private static final String PROPERTIES_KEY_ADMIN = "admin";
	private static final String PROPERTIES_KEY_GROUP = "group";
	private static final String PROPERTIES_KEY_CMDSYM = "cmdsym";
	private static final String PROPERTIES_KEY_CMDPFX = "cmdpfx";

	private static final Set<Long> ADMINQQ = new HashSet<>();
	private static final Set<Long> GROUPQQ = new HashSet<>();
	private static final List<Service<GroupMessageEvent>> SERVICES = new ArrayList<>();

	private static final String COMMAND_SYMBOL;
	private static final String COMMAND_PREFIX;
	public static final String CMD_HELP_0 = "?";
	public static final String CMD_HELP_1 = "help";
	public static final String CMD_CONTEST = "contest";
	public static final String CMD_COOKIE = "cookie";

	@Override
	public void accept(GroupMessageEvent event)
	{
		if (CodeforcesController.GROUPQQ.contains(event.getGroup().getId()))
		{
			// Filter MessageContent
			LinkedList<SingleMessage> contentList = event.getMessage()
				.stream().filter(MessageContent.class::isInstance)
				.collect(new CollectorArray<>(new LinkedList<>()));

			if (!contentList.isEmpty() && contentList.getFirst().contentToString().startsWith(COMMAND_SYMBOL + COMMAND_PREFIX))
			{
				String content = contentList.removeFirst().toString();
				content = content.substring(COMMAND_SYMBOL.length() + COMMAND_PREFIX.length())
					.stripLeading();
				if (content.isEmpty()) content = CMD_HELP_0;


				for (int i = CodeforcesController.SERVICES.size(); i --> 0;)
				{
					Service<GroupMessageEvent> service = CodeforcesController.SERVICES.get(i);
					if (content.startsWith(service.name))
					{
						content = content.substring(service.name.length())
							.stripLeading();
						if (!content.isEmpty()) contentList.addFirst(new PlainText(content));
						service.service(event, contentList);
					}
				}
			}
		}
	}

	static
	{
		JsonObject property = null;
		try (FileInputStream propertyFile = new FileInputStream(CodeforcesController.CODEFORCES_PROPERTIES_FILENAME))
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
			CodeforcesController.ADMINQQ.add(adminqq);
		}
		// Read group qq(s)
		JsonArray jsonGroupArray = property.getAsJsonArray(PROPERTIES_KEY_GROUP);
		for (JsonElement jsonAdmin : jsonGroupArray)
		{
			long groupqq = jsonAdmin.getAsLong();
			CodeforcesController.GROUPQQ.add(groupqq);
		}
		// Read command settings
		COMMAND_SYMBOL = property.get(PROPERTIES_KEY_CMDSYM).getAsString();
		COMMAND_PREFIX = property.get(PROPERTIES_KEY_CMDPFX).getAsString();

		SERVICES.add(new CodeforcesServiceHelp(CMD_HELP_0));
		SERVICES.add(new CodeforcesServiceHelp(CMD_HELP_1));
		SERVICES.add(new CodeforcesServiceContest(CMD_CONTEST));
		SERVICES.add(new CodeforcesServiceCookie(CMD_COOKIE));
		SERVICES.sort(new ComparatorService<>());
	}
}
