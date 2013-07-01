package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class DtTag extends CompositeTag {

	private static final String mIds[] = {
		"dt"
	};
	private static final String mEndTagEnders[] = {
		"dt"
	};

	public DtTag()
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
			str=str.replaceAll("<dt>", "");
			str=str.replaceAll("</dt>", "");
		}
		return str;
	}

}
