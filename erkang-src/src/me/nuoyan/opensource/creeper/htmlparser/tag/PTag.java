package me.nuoyan.opensource.creeper.htmlparser.tag;


import org.htmlparser.tags.CompositeTag;

public class PTag extends CompositeTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1283433154005021618L;
	private static final String mIds[] = {
		"p"
	};
	private static final String mEndTagEnders[] = {
		"p"
	};

	public PTag()
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
	
	public String getPText(){
		String str=this.getStringText();
		if(str!=null){
			str=str.replaceAll("<p>", "");
			str=str.replaceAll("<p>", "");
		}
		return str;
	}

}
