package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class Dd extends CompositeTag {

	private static final String mIds[] = {
		"dd"
	};
	private static final String mEndTagEnders[] = {
		"dd"
	};

	public Dd()
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
	
	public String getDdText(){
		String str=this.getStringText();
		if(str!=null){
			str=str.replaceAll("<span>", "");
			str=str.replaceAll("</span>", "");
		}
		return str;
	}

}
