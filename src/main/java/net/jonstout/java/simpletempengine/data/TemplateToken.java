package net.jonstout.java.simpletempengine.data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Template item representing a token (i.e. a reference to data)
 * @author Jon Stout
 */
public class TemplateToken implements ITemplateItem {
	protected String token;
	protected String markup;
	
	public TemplateToken(String token) {
		super();
		this.token = token;
		this.markup = null;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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
