package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class EmTag extends CompositeTag {

	private static final String mIds[] = {
		"em"
	};
	private static final String mEndTagEnders[] = {
		"em"
	};

	public EmTag()
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
	
	public String getEmText(){
		String str=this.getStringText();
		if(str!=null){
			str=str.replaceAll("<em>", "");
			str=str.replaceAll("</em>", "");
		}
		return str;
	}

}

