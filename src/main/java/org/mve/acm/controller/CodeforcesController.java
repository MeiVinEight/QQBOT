package org.mve.acm.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.service.ComparatorService;
import org.mve.service.Service;
import org.mve.acm.services.codeforces.CodeforcesServiceContest;
import org.mve.acm.services.codeforces.CodeforcesServiceHelp;
import org.mve.invoke.common.JavaVM;
import org.mve.service.ServiceManager;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeforcesController extends Service<MessageEvent>
{
	private static final String CODEFORCES_PROPERTIES_FILENAME = "codeforces.json";
	private static final String PROPERTIES_KEY_ADMIN = "admin";
	private static final String PROPERTIES_KEY_GROUP = "group";
	private static final String PROPERTIES_KEY_CMDSYM = "cmdsym";
	private static final String PROPERTIES_KEY_CMDPFX = "cmdpfx";

	private static final Set<Long> ADMINQQ = new HashSet<>();
	private static final Set<Long> GROUPQQ = new HashSet<>();
	private static final List<Service<GroupMessageEvent>> SERVICES = new ArrayList<>();

	public static final String COMMAND_SYMBOL;
	public static final String COMMAND_PREFIX;

	private final ServiceManager service = new ServiceManager();

	public CodeforcesController()
	{
		super(COMMAND_SYMBOL + COMMAND_PREFIX);
	}

	@Override
	public void service(MessageEvent event, List<SingleMessage> contentList)
	{
		if (event instanceof GroupMessageEvent groupEvent)
		{
			if (!CodeforcesController.GROUPQQ.contains(groupEvent.getGroup().getId())) return;
		}
		if (contentList.isEmpty())
		{
			event.getSubject().sendMessage("?");
			return;
		}
		Service<Event> service = this.service.service(event.getClass(), contentList.get(0).toString());
		if (service == null)
		{
			event.getSubject().sendMessage("?");
			return;
		}
		service.subject(event, contentList);
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

		SERVICES.add(new CodeforcesServiceHelp(CodeforcesServiceHelp.CMD_HELP_0));
		SERVICES.add(new CodeforcesServiceHelp(CodeforcesServiceHelp.CMD_HELP_1));
		SERVICES.add(new CodeforcesServiceContest());
		SERVICES.sort(new ComparatorService<>());
	}
}
