package net.jonstout.java.simpletempengine.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jonstout.java.simpletempengine.data.ITemplateItem;
import net.jonstout.java.simpletempengine.data.Template;
import net.jonstout.java.simpletempengine.data.TemplateLineBreak;
import net.jonstout.java.simpletempengine.data.TemplateString;
import net.jonstout.java.simpletempengine.data.TemplateToken;

/**
 * Static factory methods to (a) load a template from a file, (b) parse 
 * said template into a series of template item objects, and (c) return 
 * said items within a Template object. 
 * @author Jon Stout
 * @see net.jonstout.java.simpletempengine.data.Template
 * @see net.jonstout.java.simpletempengine.data.ITemplateItem
 */
public class TemplateFactory {
	public static String TOKEN_REGEX = "<<\\s*([\\w\\.]+)\\s*>>";
	public static String START_EACH_REGEX = "<<\\s*EACH ([\\w\\.]+) ([\\w\\.]+)\\s*>>";
	public static String END_EACH_REGEX = "<<\\s*ENDEACH\\s*>>";
	
	public static Template loadTemplate(String fileLoc, String charsetName) throws IOException {
		Template template = new Template();
		// open template file
		Path filePath = Paths.get(fileLoc);
		Charset charset = Charset.forName(charsetName);
		BufferedReader reader = Files.newBufferedReader(filePath, charset);
		// process template file
		Pattern tokenPattern = Pattern.compile(TOKEN_REGEX, Pattern.CASE_INSENSITIVE);
		Pattern startEachPattern = Pattern.compile(START_EACH_REGEX, Pattern.CASE_INSENSITIVE);
		Pattern endEachPattern = Pattern.compile(END_EACH_REGEX, Pattern.CASE_INSENSITIVE);
		String line;
		int len;
		int index;
		ITemplateItem currentLineItem = null;
		EachTokenFactory eachFactory = new EachTokenFactory();
		Matcher tokenMatcher;
		Matcher startEachMatcher;
		Matcher endEachMatcher;
		Matcher firstMatcher = null;
		while ((line = reader.readLine()) != null) {
			// process line
			index = 0;
			len = line.length();
			// run regex 
			tokenMatcher = tokenPattern.matcher(line);
			startEachMatcher = startEachPattern.matcher(line);
			endEachMatcher = endEachPattern.matcher(line);
			// go through the line
			while (index < len) {
				currentLineItem = null;
				firstMatcher = findFirstMatch(index, tokenMatcher, startEachMatcher, endEachMatcher);
				if (firstMatcher == null) {
					// no token matches; just throw everything into a TemplateLine object
					currentLineItem = new TemplateString(line.substring(index));
					index = len;
				} else if (firstMatcher.start() > index) {
					// there's stuff between the next token and our current position. Let's get that first.
					currentLineItem = new TemplateString(line.substring(index, firstMatcher.start()));
					index = firstMatcher.start();
				} else if (firstMatcher.start() == index && firstMatcher == tokenMatcher) {
					// if we've got a new token
					currentLineItem = new TemplateToken(firstMatcher.group(1));
					index = firstMatcher.end();
				} else if (firstMatcher.start() == index && firstMatcher == startEachMatcher) {
					// if we're opening an each block
					eachFactory.startEachBlock(firstMatcher.group(1), firstMatcher.group(2));
					index = firstMatcher.end();
				} else if (eachFactory.isInEachBlock() && firstMatcher.start() == index && firstMatcher == endEachMatcher) {
					// if we're closing an each block
					eachFactory.closeEachBlock();
					index = firstMatcher.end();
					// check to see if this is bottom-level each block
					if (eachFactory.isEachBlockClosed()) {
						currentLineItem = eachFactory.flush();
					}
				}
				// now that we've got the line item, figure out where to put it
				if (currentLineItem != null && eachFactory.isInEachBlock()) {
					// if we're in an each block, add it to said each block
					eachFactory.addItemToEachBlock(currentLineItem);
				} else if (currentLineItem != null) {
					// otherwise, just add the line item to the template
					template.addItem(currentLineItem);
				}
				// let's add line breaks, just to make sure we match the template
				if (index == len && eachFactory.isInEachBlock()) {
					eachFactory.addItemToEachBlock(new TemplateLineBreak());
				} else if (index == len) {
					template.addItem(new TemplateLineBreak());
				}
			}
		}
		// close file
		reader.close();
		// prep template
		template.loadComplete();
		// and return
		return template;
	}
		
	/**
	 * Sorts out which regex match comes first in the line.
	 * @param currentIndex index in the current line
	 * @param tokenMatcher TOKEN_REGEX matcher for current line
	 * @param startEachMatcher START_EACH_REGEX matcher for current line
	 * @param endEachMatcher END_EACH_REGEX matcher for current line
	 * @return the matcher with the lowest index
	 */
	private static Matcher findFirstMatch(int currentIndex, Matcher tokenMatcher, Matcher startEachMatcher, Matcher endEachMatcher) {
		Boolean hasToken = tokenMatcher.find(currentIndex);
		Boolean hasStartEach = startEachMatcher.find(currentIndex);
		Boolean hasEndEach = endEachMatcher.find(currentIndex);
		if (hasToken && !hasStartEach && !hasEndEach) {
			return tokenMatcher;
		} else if (hasStartEach && !hasToken && !hasEndEach) {
			return startEachMatcher;
		} else if (hasEndEach && !hasToken && !hasEndEach) {
			return endEachMatcher;
		} else if (hasToken && hasStartEach && !hasEndEach) {
			return checkTokenMatch(tokenMatcher, startEachMatcher);
		} else if (hasToken && hasEndEach && !hasStartEach) {
			return checkTokenMatch(tokenMatcher, endEachMatcher);
		} else if (hasStartEach && hasEndEach) {
			return getFirstMatcher(startEachMatcher, endEachMatcher);
		}
		// this SHOULD catch all possibilities. If it doesn't, might
		// need to revisit this function.
		return null;
	}
	
	/**
	 * Checks to see if a TOKEN_REGEX match is just a token, or is instead a
	 * START_EACH_REGEX or END_EACH_REGEX match.
	 * @param tokenMatcher
	 * @param otherMatcher
	 * @return the correct matcher for the job
	 */
	private static Matcher checkTokenMatch(Matcher tokenMatcher, Matcher otherMatcher) {
		Matcher firstMatcher = getFirstMatcher(tokenMatcher, otherMatcher);
		if (firstMatcher == null) {
			// if tokenMatcher has picked up a startEach or endEach tag, which will happen
			return otherMatcher;
		}
		return firstMatcher;
	}
	
	/**
	 * Checks to see which pattern match comes first in line. If they have the same index (i.e. are the same tag), returns null.
	 * @param matcherA first Matcher to compare
	 * @param matcherB second Matcher to compare
	 * @return matcher with the lowest index, or null if they have the same index
	 */
	private static Matcher getFirstMatcher(Matcher matcherA, Matcher matcherB) {
		int aStart = matcherA.start();
		int bStart = matcherB.start();
		if (aStart < bStart) {
			return matcherA;
		} else if (bStart < aStart) {
			return matcherB;
		}
		return null;
	}
}
