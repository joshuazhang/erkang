package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class TbodyTag extends CompositeTag {

	private static final String mIds[] = {
		"tbody"
	};
	private static final String mEndTagEnders[] = {
		"tbody"
	};

	public TbodyTag()
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
