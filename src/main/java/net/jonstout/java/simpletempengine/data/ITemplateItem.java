package net.jonstout.java.simpletempengine.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.StringBuilder;

/**
 * A common interface for Template Items. Each template item represents 
 * a 'chunk' of a loaded template's content. While template items might 
 * have their own distinct functionalities, this interface allows us to 
 * treat them as the same class of object.
 * @author Jon Stout
 */
public interface ITemplateItem {
	public void writeTo(BufferedWriter output) throws IOException;
	public void appendTo(StringBuilder str);
}
