package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class AddressTag extends CompositeTag {

	private static final String mIds[] = {
		"address"
	};
	private static final String mEndTagEnders[] = {
		"address"
	};

	public AddressTag()
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
