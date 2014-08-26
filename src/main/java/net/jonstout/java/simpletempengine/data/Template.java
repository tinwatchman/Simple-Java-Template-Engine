package net.jonstout.java.simpletempengine.data;

import java.util.ArrayList;

/**
 * Data object representing a Template. Basically just a wrapper 
 * and iterator for a list of template items. 
 * @author Jon Stout
 */
public class Template {
	private ArrayList<ITemplateItem> content;
	private int currentIndex;
	private int length;
	
	public Template() {
		this.content = new ArrayList<ITemplateItem>();
		this.currentIndex = -1;
	}
	
	public void addItem(ITemplateItem line) {
		this.content.add(line);
	}
	
	public void loadComplete() {
		this.length = this.content.size();
	}
	
	public Boolean hasNextItem() {
		return ((this.currentIndex+1) < this.length);
	}
	
	public ITemplateItem nextItem() {
		this.currentIndex++;
		return this.content.get(this.currentIndex);
	}	
}
