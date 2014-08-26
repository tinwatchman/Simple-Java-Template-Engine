package net.jonstout.java.simpletempengine.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Template item that represents an Each block.
 * @author Jon Stout
 */
public class TemplateEachToken implements ITemplateItem {
	private String arrayName;
	private String instanceName;
	private ArrayList<ITemplateItem> content;
	private Boolean isClosed;
	private String markup;
	
	public TemplateEachToken(String array, String instanceName) {
		super();
		this.arrayName = array;
		this.instanceName = instanceName;
		this.content = new ArrayList<ITemplateItem>();
		this.isClosed = false;
		this.markup = null;
	}
	
	public String getArrayName() {
		return arrayName;
	}

	public void setArrayName(String arrayName) {
		this.arrayName = arrayName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public ArrayList<ITemplateItem> getContent() {
		return content;
	}
	
	public void appendContent(ITemplateItem item) {
		this.content.add(item);
	}
		
	public Boolean isClosed() {
		return this.isClosed;
	}
	
	public void setIsClosed(Boolean value) {
		this.isClosed = value;
	}
	
	public String getMarkup() {
		return markup;
	}

	public void setMarkup(String markup) {
		this.markup = markup;
	}
	
	public void writeTo(BufferedWriter output) throws IOException {
		if (this.markup != null) {
			output.write(this.markup);
		}
	}

	public void appendTo(StringBuilder str) {
		if (this.markup != null) {
			str.append(this.markup);
		}
	}
}
