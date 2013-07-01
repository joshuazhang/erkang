package me.nuoyan.opensource.creeper.plugin;

import org.htmlparser.Parser;
/**
 * 在抓去玩XML里配置的所有字段后，进行更多处理
 * @author joshuazhang
 *
 */
public interface CaptureMorePlugin extends ErkangPlugin{
	
	public void doCapture(Parser parser, Object object);

}
