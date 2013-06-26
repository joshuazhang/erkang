package me.nuoyan.opensource.creeper.daojia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import me.nuoyan.opensource.creeper.plugin.CaptureMorePlugin;
import me.nuoyan.opensource.creeper.utils.DBUtil;
import me.nuoyan.opensource.creeper.utils.DateUtil;

public class CaptureShopPlugin implements CaptureMorePlugin {
	
//	private static AndFilter shopLinkFilter;
	
	private static NodeFilter menuFilter;
	
	private static String imageRegex = "attr\\(\\'src\\', .{1}(.*?).{1}\\)\"";
	private static String dishRegex = ".*(</span>|class=\"tip\".*?/>)(.*?)(</a></td>|</td>)$";
	private static String priceRegex = ">(.*?)???/(.*?)<";
	
	private static Pattern imagePattern = Pattern.compile(imageRegex);
	private static Pattern dishPattern = Pattern.compile(dishRegex);
	private static Pattern pricePattern = Pattern.compile(priceRegex);
	
	static {
//		shopLinkFilter = new AndFilter(new HasParentFilter(new TagNameFilter("h3"), false), new LinkRegexFilter("/rest/[0-9]+/[0-9]+"));
		menuFilter = new AndFilter(new TagNameFilter("tr"), new HasAttributeFilter("onmouseover", "$(this).css('backgroundColor', '#E7E7E7'); $(this).css('color', '#F67901');"));
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern p = Pattern.compile(imageRegex);
		Matcher m = p.matcher("<td width=\"30%\" onmouseover=\"$('#IMG518953').attr('src', 'http://img2.daojia.com.cn//images1/1699/13631395460.63320900.jpg`)\" ><a class=\"hfcL\" href=\"#\"><img src=\"/images/order21.jpg\" onmouseover=\"$('#IMG518953').attr('src', 'http://img2.daojia.com.cn//images1/1699/13631395460.63320900.jpg')\" class=\"tip\"/><span id=\"boxShadow1\"><img id=\"IMG518953\" src=\"/images/lodimg.gif\" /><b>?????????</b></span>?????????</td>");
		if (m.find()) {
			System.out.println(m.group(1));
		}
		
		Pattern p2 = Pattern.compile(dishRegex);
		Matcher m2 = p2.matcher("<td width=\"30%\" onmouseover=\"$('#IMG518953').attr('src', 'http://img2.daojia.com.cn//images1/1699/13631395460.63320900.jpg')\" ><a class=\"hfcL\" href=\"#\"><img src=\"/images/order21.jpg\" onmouseover=\"$('#IMG518953').attr('src', 'http://img2.daojia.com.cn//images1/1699/13631395460.63320900.jpg')\" class=\"tip\"/><span id=\"boxShadow1\"><img id=\"IMG518953\" src=\"/images/lodimg.gif\" /><b>?????????</b></span>?????????</td>");
		if (m2.find()) {
			System.out.println(m2.group(1));
		}
		
		
	}
	
	private static DBUtil shopDbUtil = new DBUtil("com.mysql.jdbc.Driver", 
				"jdbc:mysql://114.251.47.254:3306/daojia?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8", 
				"root", 
				"test456123", 
				"shop");

	private static DBUtil menuDbUtil = new DBUtil("com.mysql.jdbc.Driver", 
			"jdbc:mysql://114.251.47.254:3306/daojia?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8", 
			"root", 
			"test456123", 
			"menu");
	
	@Override
	public void doCapture(Parser parser, Object object) {
		
		Weed weed = (Weed) object;
		System.out.println(weed.getShopName());
		parser.reset();
		Shop shop = new Shop();
		shop.setAddress(weed.getShopAddress());
		shop.setCatagory(weed.getCatagory());
		shop.setName(weed.getShopName());
		shop.setUrl(weed.getUrl());
		shop.setCreateTime(DateUtil.getFormattedDate(new Date()));
		if (weed.getCity() != null) {
			weed.setCity(weed.getCity().substring(0, weed.getCity().indexOf(" ")));
		}
		shop.setCity(weed.getCity());
		Integer sid = null;
		try {
			sid = shopDbUtil.persist(shop);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (sid == null) {
			return;
		}
		
		weed.setSid(sid);
		try {
			parser.reset();
			NodeList nodeList = parser.extractAllNodesThatMatch(menuFilter);
			for (int i = 0; i < nodeList.size(); i++) {
				Menu menu = new Menu();
				Node node = nodeList.elementAt(i);
				List<Node> children = new ArrayList<Node>();
				for (int j = 0; j < node.getChildren().size(); j++) {
					if (node.getChildren().elementAt(j) instanceof TagNode) {
						children.add(node.getChildren().elementAt(j));
					}
				}
				
				if (children.size() < 4) {
					System.out.println();
				}
				
				Node imageAndName = children.get(0);
				String imageAndNameStr = imageAndName.toHtml();
				Matcher m = imagePattern.matcher(imageAndNameStr);
				if (m.find()) {
					menu.setImage(m.group(1));
				}
				Matcher m2 = dishPattern.matcher(imageAndNameStr);
				if (m2.find()) {
					menu.setName(m2.group(2));
				}
				menu.setCreateTime(DateUtil.getFormattedDate(new Date()));
				Matcher m3 = pricePattern.matcher(children.get(1).toHtml());
				if (m3.find()) {
					menu.setPrice(Double.valueOf(m3.group(1)));
					menu.setUnit(m3.group(2));
				}
				menu.setRemark(children.get(2).toHtml().replaceAll("<.*?>", "").replaceAll("&nbsp;", "").trim());
				menu.setSid(sid);
				menuDbUtil.persist(menu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
