package org.mve.acm.pojo.vo;

import com.google.gson.annotations.SerializedName;
import org.mve.acm.services.codeforces.ContestPhase;
import org.mve.acm.services.codeforces.ContestType;

public class CodeforcesContestVO
{
	public int id;
	public String name;
	public ContestType type;
	public ContestPhase phase;
	public boolean frozen;
	@SerializedName("durationSeconds")
	public long duration;
	@SerializedName("startTimeSeconds")
	public long start;
	@SerializedName("relativeTimeSeconds")
	public long relative;
}
