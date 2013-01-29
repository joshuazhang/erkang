package me.nuoyan.opensource.creeper.pick;

import org.htmlparser.nodes.TagNode;

public class Picker {
	
	public static String pickAttr(TagNode node, Pick pick) {
		if (pick instanceof AttrPick) {
			return node.getAttribute(((AttrPick)pick).getAttrName());
		}
		return null;
		
	}
}
