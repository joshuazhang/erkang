package me.nuoyan.opensource.creeper.picking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;

public class Picker {
	
	public static String pickValue(Node node, Pick pick) {
		try {
			if (node instanceof TagNode) {
				if (pick instanceof AttrPick) {
					return ((TagNode)node).getAttribute(((AttrPick)pick).getAttrName());
				} else if (pick instanceof RegexFindPick) {
					RegexFindPick findPick = (RegexFindPick) pick;
					Pattern pattern = Pattern.compile(findPick.getPattern());
					Matcher matcher = pattern.matcher(((TagNode)node).toHtml().replaceAll("\n", "").replaceAll("\r", ""));
					if (matcher.find()) {
						return matcher.group(1).replaceAll("<.*?>", "").replaceAll("&nbsp;", " ").trim();
					}
				} else if (pick instanceof TextPick) {
					TextPick textPick = (TextPick) pick;
					return ((TagNode)node).toHtml().replaceAll("<.*?>", "").replaceAll("&nbsp;", " ");
				}
			} else if (node instanceof TextNode) {
				if (pick instanceof RegexFindPick) {
					RegexFindPick findPick = (RegexFindPick) pick;
					Pattern pattern = Pattern.compile(findPick.getPattern());
					Matcher matcher = pattern.matcher(node.toHtml());
					if (matcher.find()) {
						return matcher.group(1).replaceAll("<.*?>", "").replaceAll("&nbsp;", " ").trim();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		
		//<li><h5>领航冰品（兰豆咖啡）（北京）</h5>                    <p>东城区东直门内大街19号乾元国际商务酒店三层</p>                    <p>010-52859830</p>                    <p class="has-poi-correct">                        <a class="view-map" href="javascript:void(0)" data-id="165937" data-poiid="2367033" gaevent="InnerLink|Click|content/detail/map/detail">查看地图</a>                        <a class="search-path" href="javascript:void(0)" data-id="165937" gaevent="InnerLink|Click|content/detail/map/drive">公交/驾车</a>                        <a class="correct-poi" href="javascript:void(0)" data-id="165937" data-poiid="2367033" gaevent="InnerLink|Click|deal/correctpoi">信息报错</a>                    </p>                </li>
//		String str = "<li><h5>领航冰品（兰豆咖啡）（北京）</h5>                    <p>东城区东直门内大街19号乾元国际商务酒店三层</p>                    <p>010-52859830</p>                    <p class=\"has-poi-correct\">                        <a class=\"view-map\" href=\"javascript:void(0)\" data-id=\"165937\" data-poiid=\"2367033\" gaevent=\"InnerLink|Click|content/detail/map/detail\">查看地图</a>                        <a class=\"search-path\" href=\"javascript:void(0)\" data-id=\"165937\" gaevent=\"InnerLink|Click|content/detail/map/drive\">公交/驾车</a>                        <a class=\"correct-poi\" href=\"javascript:void(0)\" data-id=\"165937\" data-poiid=\"2367033\" gaevent=\"InnerLink|Click|deal/correctpoi\">信息报错</a>                    </p>                </li>";
//		Pattern pattern = Pattern.compile("<li>.*?</p>(.*?)<p class.*");//<li>.*?</p>(.*?)<p class.*
//		Matcher matcher = pattern.matcher(str);
//		if (matcher.find()) {
//			System.out.println(matcher.group(1));
//		}
		
		
		String str = "";
		Pattern pattern = Pattern.compile("<li>.*?</p>(.*?)<p class.*");//<li>.*?</p>(.*?)<p class.*
		Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		}
	}
}
