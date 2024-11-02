package org.mve.acm.services.codeforces;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.utils.ExternalResource;
import org.mve.acm.QQBOT;
import org.mve.selenium.TemporarySHA1;
import org.mve.service.Service;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.util.LinkedList;

public class CodeforcesScreenshotProblemService extends Service<MessageEvent>
{
	public static final String SERVICE_NAME = "problem";
	public static final TemporarySHA1 OUTPUT_TYPE = new TemporarySHA1("img");

	public CodeforcesScreenshotProblemService()
	{
		super(CodeforcesScreenshotProblemService.SERVICE_NAME);
	}

	@Override
	public synchronized void service(MessageEvent event, LinkedList<SingleMessage> contentList)
	{
		if (contentList.isEmpty())
		{
			event.getSubject().sendMessage("WRONG PROBLEM ID");
			return;
		}

		String problemID = contentList.get(0).contentToString();
		if (problemID.length() != 5)
		{
			event.getSubject().sendMessage("WRONG PROBLEM ID");
			return;
		}

		String contestIDStr = problemID.substring(0, 4);
		int contestID;
		try
		{
			contestID = Integer.parseInt(contestIDStr);
		}
		catch (Throwable e)
		{
			event.getSubject().sendMessage("WRONG CONTEST ID:" + contestIDStr + "\n" + e);
			return;
		}

		char problemNumber = problemID.charAt(4);
		if (!(problemNumber >= 'A' && problemNumber <= 'Z'))
		{
			event.getSubject().sendMessage("WRONG PROBLEM NUMBER:" + problemNumber);
			return;
		}

		String problemURL = "https://codeforces.com/contest/" + contestID + "/problem/" + problemNumber;

		// Check problem exist

		// screenshot problem content
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		options.addArguments("--user-agent=" + CodeforcesAPI.USER_AGENT);
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().setSize(new Dimension(1920, 1080));
		try
		{
			driver.get(problemURL);
			File screenshotFile;
			try
			{
				WebElement problemindexholder = driver.findElement(By.className("problemindexholder"));
				driver.manage().window()
					.setSize(new Dimension(1920, problemindexholder.getRect().height + 460 + 139));
				screenshotFile = problemindexholder.getScreenshotAs(OUTPUT_TYPE);
				System.out.println(screenshotFile.getAbsolutePath());
				event.getSubject().sendMessage(problemURL);
				try (ExternalResource resource = ExternalResource.create(screenshotFile))
				{
					event.getSubject().sendMessage(event.getSubject().uploadImage(resource));
				}
			}
			catch (NoSuchElementException e)
			{
				event.getSubject().sendMessage("NO SUCH PROBLEM");
			}
			catch (Throwable t)
			{
				event.getSubject().sendMessage(t.toString());
			}
		}
		catch (Throwable t)
		{
			QQBOT.BOT.getLogger().error(t);
			event.getSubject().sendMessage(t.toString());
		}
	}
}
