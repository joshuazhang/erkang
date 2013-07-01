package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class Li extends CompositeTag {

	private static final String mIds[] = {
		"li"
	};
	private static final String mEndTagEnders[] = {
		"li"
	};

	public Li()
	{
	}

	public String[] getIds()
	{
		return mIds;
	}
	public String[] getEndTagEnders()
	{
		return mEndTagEnders;
	}

}

