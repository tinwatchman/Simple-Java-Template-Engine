package net.jonstout.java.simpletempengine.data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Template item that's pretty much just a wrapper around a String.
 * @author Jon Stout
 */
public class TemplateString implements ITemplateItem {
	private String str;
		
	public TemplateString(String line) {
		super();
		this.str = line;
	}

	public TemplateString() {
		super();
		this.str = null;
	}

	public String get() {
		return str;
	}

	public void set(String content) {
		this.str = content;
	}

	public void writeTo(BufferedWriter output) throws IOException {
		if (str != null) {
			output.write(this.str);
		}
	}

	public void appendTo(StringBuilder str) {
		if (str != null) {
			str.append(this.str);
		}
	}
}
