package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class StrongTag extends CompositeTag {

	private static final String mIds[] = {
		"strong"
	};
	private static final String mEndTagEnders[] = {
		"strong"
	};

	public StrongTag()
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
	
	public String getStrongText(){
		String str=this.getStringText();
		if(str!=null){
			if(str.indexOf(">")>0){
				str=str.substring(str.indexOf(">")+1,str.length());
				if(str.indexOf("<")>0){
					str.substring(0, str.indexOf("<"));
					return str;
				}
			}
		}
		return str;
	}

}

