package org.mve.acm.services.codeforces;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import org.mve.acm.pojo.vo.CodeforcesContestVO;
import org.mve.services.Service;
import org.mve.services.ServicesManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CodeforcesServiceContest extends Service<GroupMessageEvent>
{
	private static final Gson GSON = new Gson();
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public CodeforcesServiceContest(String name)
	{
		super(name);
	}

	@Override
	public void service(GroupMessageEvent event, LinkedList<SingleMessage> contentList)
	{
		try
		{
			URL url = new URL("https://codeforces.com/api/contest.list");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			JsonObject component;
			try (InputStream in = conn.getInputStream())
			{
				component = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
			}
			String status = component.get(CodeforcesAPI.KEY_STATUS).getAsString();
			if (CodeforcesAPI.STATUS_FAILED.equals(status))
			{
				event.getSubject().sendMessage(component.get(CodeforcesAPI.KEY_COMMENT).getAsString());
				return;
			}

			List<CodeforcesContestVO> comingContests = new LinkedList<>();
			JsonArray jsonContestArray = component.get(CodeforcesAPI.KEY_RESULT).getAsJsonArray();
			for (JsonElement jsonElement : jsonContestArray)
			{
				JsonObject jsonContest = jsonElement.getAsJsonObject();
				CodeforcesContestVO contest = GSON.fromJson(jsonContest, CodeforcesContestVO.class);
				if (contest.phase != ContestPhase.BEFORE) break;
				comingContests.add(contest);
			}
			StringBuilder builder = new StringBuilder(1024);
			builder.append(DATE_FORMAT.format(new Date())).append("\n Current or upcoming contests:");
			// event.getSubject().sendMessage(builder.toString());
			for (int i = comingContests.size(); i --> 0;)
			{
				CodeforcesContestVO contest = comingContests.get(i);
				long durH = (contest.duration / 3600);
				long durM = (contest.duration % 3600) / 60;
				long durS = (contest.duration % 60);
				long relD = ((-contest.relative)) / 86400;
				long relH = ((-contest.relative) % 86400) / 3600;
				long relM = ((-contest.relative) % 3600) / 60;
				long relS = ((-contest.relative) % 60);
				// builder.setLength(0);
				builder.append('\n')
					.append(contest.name)
					.append("\n开始时间: ")
					.append(DATE_FORMAT.format(new Date(contest.start * 1000)))
					.append("\n持续时间: ")
					.append(String.format("%02d:%02d:%02d", durH, durM, durS))
					.append("\n距离开始: ")
					.append(String.format("%02d:%02d:%02d:%02d", relD, relH, relM, relS));
			}
			event.getSubject().sendMessage(builder.toString());
		}
		catch (Throwable e)
		{
			ServicesManager.BOT.getLogger().error(e);
		}
	}
}
