package me.nuoyan.opensource.creeper.pick;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.nodes.TagNode;

public class Picker {
	
	public static String pickAttr(TagNode node, Pick pick) {
		if (pick instanceof AttrPick) {
			return node.getAttribute(((AttrPick)pick).getAttrName());
		} else if (pick instanceof RegexFindPick) {
			RegexFindPick findPick = (RegexFindPick) pick;
			Pattern pattern = Pattern.compile(findPick.getPattern());
			Matcher matcher = pattern.matcher(node.toHtml());
			if (matcher.find()) {
				return matcher.group(1);
			}
		} else if (pick instanceof TextPick) {
			TextPick textPick = (TextPick) pick;
			return node.toHtml().replaceAll("<.*?>", "");
		}
		return null;
		
	}
}
