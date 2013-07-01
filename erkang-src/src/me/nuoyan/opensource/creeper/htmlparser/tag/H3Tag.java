package me.nuoyan.opensource.creeper.htmlparser.tag;

import org.htmlparser.tags.CompositeTag;

public class H3Tag extends CompositeTag {

	private static final String mIds[] = {
		"h3"
	};
	private static final String mEndTagEnders[] = {
		"h3"
	};

	public H3Tag()
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
	
	public String getH2Text(){
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
