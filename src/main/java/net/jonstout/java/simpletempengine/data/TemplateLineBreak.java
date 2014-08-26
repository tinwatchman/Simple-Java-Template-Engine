package net.jonstout.java.simpletempengine.data;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Exactly what it says on the tin: a template item that just exists
 * to mark a line break with a loaded template.
 * @author Jon Stout
 */
public class TemplateLineBreak implements ITemplateItem {
	public void writeTo(BufferedWriter output) throws IOException {
		output.write("\n");
	}

	public void appendTo(StringBuilder str) {
		str.append("\n");
	}
}
