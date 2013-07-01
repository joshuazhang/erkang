package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class DlTag extends CompositeTag {

	private static final String mIds[] = {
		"dl"
	};
	private static final String mEndTagEnders[] = {
		"dl"
	};

	public DlTag()
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
	
	public String getDtText(){
		String str=this.getStringText();
		if(str!=null){
			str=str.replaceAll("<dl>", "");
			str=str.replaceAll("</dl>", "");
		}
		return str;
	}

}
