package logs.ui.events;

import java.util.ArrayList;
import java.util.LinkedList;

public class LogEventNodeList {
	ArrayList<LogEventNode> list;
	
	public LogEventNodeList(){
		list=new ArrayList<LogEventNode>();
	}
	public void addNode(LogEventNode node){
		list.add(node);
	}
	public ArrayList<LogEventNode> getList(){
		return list;
	}
}
