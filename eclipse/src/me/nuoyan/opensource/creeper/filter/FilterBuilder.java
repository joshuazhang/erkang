package me.nuoyan.opensource.creeper.filter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.filters.TagNameFilter;

/**
 * ��ȡxml�����ļ�������Filter�ڵ������
 * @author joshuazhang
 *
 */
public class FilterBuilder {
	
	public static NodeFilter buildNodeFilter(InputStream inputStream) throws Exception {
		StringBuffer docText = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		String line;
		while ((line=br.readLine()) != null) {
			docText.append(line);
		}
		br.close();
		inputStream.close();
		Document document = DocumentHelper.parseText(docText.toString());
		
		NodeFilter targetFilter = null;
		targetFilter = getNodeFilter((Element) document.getRootElement().elements().get(0));
		return targetFilter;
	}
	
	public static NodeFilter getNodeFilter(Element element) throws ParseException {
		String name = element.getName();
		if ("and".equals(name)) {
			List<Element> children = element.elements();
			List<NodeFilter> filters = new ArrayList<NodeFilter>();
			for (Element child : children) {
				filters.add(getNodeFilter(child));
			}
			NodeFilter[] filterArr = new NodeFilter[filters.size()];
			AndFilter andFilter = new AndFilter(filters.toArray(filterArr));
			return andFilter;
		} else if ("or".equals(name)) {
			List<Element> children = element.elements();
			List<NodeFilter> filters = new ArrayList<NodeFilter>();
			for (Element child : children) {
				filters.add(getNodeFilter(child));
			}
			NodeFilter[] filterArr = new NodeFilter[filters.size()];
			OrFilter orFilter = new OrFilter(filters.toArray(filterArr));
			return orFilter;
		} else if ("hasparent".equals(name)) {
			Element e = (Element) element.elements().get(0);
			String recursive = element.attributeValue("recursive");
			boolean isRecursive = false;
			if ("true".equals(recursive)) {
				isRecursive = true;
			}
			return new HasParentFilter(getNodeFilter(e), isRecursive);
		} else if ("hasattribute".equals(name)) {
			String attr = null;
			Element attrEle = element.element("attr");
			if (attrEle!=null) {
				attr = attrEle.getText();
			} else {
				throw new ParseException("\"hasattribute\" filter should has one and only \"attr\" child");
			}
			String attrValue = null;
			Element valueEle = element.element("value");
			if (valueEle != null) {
				attrValue = valueEle.getText();
			}
			if (attrValue == null) {
				return new HasAttributeFilter(attr);
			} else {
				return new HasAttributeFilter(attr, attrValue);
			}
		} else if ("haschild".equals(name)) {
			Element e = (Element) element.elements().get(0);
			String recursive = element.attributeValue("recursive");
			boolean isRecursive = false;
			if ("true".equals(recursive)) {
				isRecursive = true;
			}
			return new HasChildFilter(getNodeFilter(e), isRecursive);
		} else if ("regex".equals(name)) {//The strategy to use. One of MATCH, LOOKINGAT or FIND(default).
			Element s = element.element("strategy");
			String pattern = element.element("pattern").getText();
			int sint = RegexFilter.FIND;
			if (s != null) {
				String strategy = s.getTextTrim();
				if ("MATCH".equals(strategy)) {
					sint = RegexFilter.MATCH;
				} else if("LOOKINGAT".equals(strategy)) {
					sint = RegexFilter.LOOKINGAT;
				}
			}
			return new RegexFilter(pattern, sint);
		} else if ("tagname".equals(name)) {
			return new TagNameFilter(element.getTextTrim());
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		NodeFilter filter = FilterBuilder.buildNodeFilter(FilterBuilder.class.getResourceAsStream("/com/chujian/creeper/filter/sample.xml"));
		
		System.out.println(filter);
	}

}
