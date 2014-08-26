package net.jonstout.java.simpletempengine.factory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.jonstout.java.simpletempengine.data.DataContext;
import net.jonstout.java.simpletempengine.data.ITemplateItem;
import net.jonstout.java.simpletempengine.data.Template;
import net.jonstout.java.simpletempengine.data.TemplateEachToken;
import net.jonstout.java.simpletempengine.data.TemplateToken;

/**
 * Factory class that combines a Template with data and outputs
 * the resulting markup to a designated output stream.
 * @author Jon Stout
 */
public class OutputFactory {
	private DataContext context;
	private BufferedWriter writer;
	
	public OutputFactory() {
		super();
		this.writer = null;
	}
	
	/**
	 * Sets the output stream the factory should write to. Note: this
	 * method *must* be used before printToOutput is called.
	 * @param outputStream stream to write to
	 * @param charset name of the character set to use
	 */
	public void setOutputStream(OutputStream outputStream, String charset) {
		Charset cs = Charset.forName(charset);
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream, cs));
	}
	
	/**
	 * Combines a Template and a DataContext to create markup, and writes
	 * that markup to the designated output stream. Closes the output 
	 * stream and clears itself when finished.
	 * @param template
	 * @param ctx
	 * @throws IOException
	 */
	public void printToOutput(Template template, DataContext ctx) throws IOException {
		if (this.writer != null) {
			this.context = ctx;
			// process template
			ITemplateItem item;
			while (template.hasNextItem()) {
				// process
				item = template.nextItem();
				this.processTemplateItem(item);
				// output
				item.writeTo(this.writer);
			}
			// close and clear
			this.writer.close();
			this.writer = null;
			this.context = null;
		}
	}
	
	private void processTemplateItem(ITemplateItem line) {
		TemplateToken token;
		TemplateEachToken eachToken;
		if (line.getClass() == TemplateToken.class) {
			token = (TemplateToken) line;
			this.processToken(token);
		} else if (line.getClass() == TemplateEachToken.class) {
			eachToken = (TemplateEachToken) line;
			this.processEachToken(eachToken);
		}
	}
	
	private void processToken(TemplateToken token) {
		Object value = context.get(token.getToken());
		if (value != null) {
			String valueStr = value.toString();
			token.setMarkup(valueStr);
		} else {
			token.setMarkup("");
		}
	}
	
	private void processEachToken(TemplateEachToken eachToken) {
		// here's where it really gets fun
		// find array represented by the each block
		Object arrayValue = context.get(eachToken.getArrayName());
		// prep to go through each token content
		ArrayList<ITemplateItem> eachContent = eachToken.getContent();
		int contentlen = eachContent.size();
		ITemplateItem currentItem;
		StringBuilder markup = new StringBuilder();
		Boolean isProcessed = false;
		// process the value
		if (arrayValue != null && arrayValue.getClass() == ArrayList.class) {
			// if given value is an array
			this.context.add(eachToken.getInstanceName());
			@SuppressWarnings("unchecked")
			ArrayList<Object> list = (ArrayList<Object>) arrayValue;
			int listlen = list.size();
			Object listItem;
			for (int i=0; i<listlen; i++) {
				// put line item into context
				listItem = list.get(i);
				this.context.put(eachToken.getInstanceName(), listItem);
				// loop through each token's content
				for (int n=0; n<contentlen; n++) {
					currentItem = eachContent.get(n);
					this.processTemplateItem(currentItem);
					// add to markup
					currentItem.appendTo(markup);
				}
			}
			isProcessed = true;
		} else if (arrayValue != null && arrayValue.getClass() == LinkedHashMap.class) {
			// if given value is a json object
			this.context.add(eachToken.getInstanceName());
			@SuppressWarnings("unchecked")
			LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) arrayValue;
			Iterator<String> keys = map.keySet().iterator();
			String key;
			Object mapValue;
			while (keys.hasNext()) {
				key = keys.next();
				mapValue = map.get(key);
				this.context.put(eachToken.getInstanceName(), mapValue);
				for (int a=0; a<contentlen; a++) {
					currentItem = eachContent.get(a);
					this.processTemplateItem(currentItem);
					// add to markup
					currentItem.appendTo(markup);
				}
			}
			isProcessed = true;
		}
		// finally, set token's markup
		if (isProcessed) {
			eachToken.setMarkup(markup.toString());
		}
	}
}
