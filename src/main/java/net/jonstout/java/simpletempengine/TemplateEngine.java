package net.jonstout.java.simpletempengine;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.System;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.Paths;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import net.jonstout.java.simpletempengine.data.DataContext;
import net.jonstout.java.simpletempengine.data.Template;
import net.jonstout.java.simpletempengine.factory.DataFactory;
import net.jonstout.java.simpletempengine.factory.OutputFactory;
import net.jonstout.java.simpletempengine.factory.TemplateFactory;

/**
 * Main entry class for the Template Engine. Can be run from the
 * command line. Expects at least two arguments: the location of
 * a template file, and the location of a file containing 
 * JSON-formatted data. Accepts a third parameter, representing 
 * the location of the file the results should be dumped to (note:
 * if the file already exists, it <b>will be</b> overwritten.
 * Please be cautious when using this feature.) If no output file
 * is given, instead dumps the output directly to the console 
 * window.
 * @author Jon Stout
 */
public class TemplateEngine {
	private static String TEMPLATE_EXT = ".template";
	private static String JSON_EXT = ".json";
	private static String OUTPUT_EXT = ".html";
	private static String DEFAULT_CHARSET = "UTF-8";
	
	public static void main(String[] args) {
		// parse command line arguments
		String templateLoc = null;
		String jsonLoc = null;
		String outputLoc = null;
		if (args.length == 3) {
			templateLoc = args[0];
			jsonLoc = args[1];
			outputLoc = args[2];
		} else {
			for (String arg: args) {
				if (arg.endsWith(TemplateEngine.TEMPLATE_EXT)) {
					templateLoc = arg;
				} else if (arg.endsWith(TemplateEngine.JSON_EXT)) {
					jsonLoc = arg;
				} else if (arg.endsWith(TemplateEngine.OUTPUT_EXT)) {
					outputLoc = arg;
				}
			}
		}
		// if we don't have the arguments we need...
		if (templateLoc == null || jsonLoc == null) {
			System.out.println("ERROR: Please provide template and data file locations as arguments.");
			System.exit(0);
		}
		// otherwise, let's get started
		// load template
		Template template = null;
		try {
			template = TemplateFactory.loadTemplate(templateLoc, DEFAULT_CHARSET);
		} catch (IOException e) {
			System.out.println("Unable to load template");
			e.printStackTrace();
			System.exit(0);
		}
		// load json data
		DataContext data = null;
		try {
			data = DataFactory.loadData(jsonLoc);
		} catch (JsonParseException e) {
			System.out.println("Unable to parse JSON data");
			e.printStackTrace();
			System.exit(0);
		} catch (JsonMappingException e) {
			System.out.println("Unable to map JSON data to type");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Unable to load JSON data file");
			e.printStackTrace();
			System.exit(0);
		}
		// decide output -- to file or standard out
		OutputStream outputStream = null;
		if (outputLoc != null) {
			try {
				outputStream = Files.newOutputStream(Paths.get(outputLoc), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			} catch (IOException e) {
				System.out.println("Unable to open output file");
				e.printStackTrace();
				System.exit(0);
			}
		} else {
			outputStream = System.out;
		}
		// write output
		OutputFactory outputFactory = new OutputFactory();
		outputFactory.setOutputStream(outputStream, DEFAULT_CHARSET);
		try {
			outputFactory.printToOutput(template, data);
		} catch (IOException e) {
			System.out.println("Unable to write to output");
			e.printStackTrace();
			System.exit(0);
		}
	}

}
