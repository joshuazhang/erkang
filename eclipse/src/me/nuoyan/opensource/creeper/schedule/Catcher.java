package me.nuoyan.opensource.creeper.schedule;

import me.nuoyan.opensource.creeper.action.Action;
import me.nuoyan.opensource.creeper.pick.Pick;

import org.htmlparser.NodeFilter;

public class Catcher {
	
	private NodeFilter nodeFilter;
	
	private Pick pick;
	
	private Action action;

	public NodeFilter getNodeFilter() {
		return nodeFilter;
	}

	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}

	public Pick getPick() {
		return pick;
	}

	public void setPick(Pick pick) {
		this.pick = pick;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
