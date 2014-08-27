package net.jonstout.java.simpletempengine.factory;

import java.util.ArrayList;

import net.jonstout.java.simpletempengine.data.ITemplateItem;
import net.jonstout.java.simpletempengine.data.TemplateEachToken;

/**
 * A class used by TemplateFactory to manage TemplateEachToken 
 * creation. This is to allow for the possibility of nested 
 * Each tokens.
 * @author Jon Stout
 * @see net.jonstout.java.simpletempengine.factory.TemplateFactory
 * @see net.jonstout.java.simpletempengine.data.TemplateEachToken
 */
public class EachTokenFactory {
	private ArrayList<TemplateEachToken> openTokens;
	private TemplateEachToken closedToken;
	private int currentLevel;
	
	public EachTokenFactory() {
		super();
		this.openTokens = new ArrayList<TemplateEachToken>();
		this.closedToken = null;
		this.currentLevel = 0;
	}
	
	public Boolean isInEachBlock() {
		return (this.openTokens.size() > 0);
	}
	
	public Boolean isEachBlockClosed() {
		return (this.openTokens.size() == 0 && this.closedToken != null);
	}
	
	public TemplateEachToken flush() {
		TemplateEachToken token = this.closedToken;
		this.closedToken = null;
		return token;
	}
	
	public void startEachBlock(String array, String instance) {
		TemplateEachToken token = new TemplateEachToken(array, instance);
		this.openTokens.add(token);
		if (this.openTokens.size() > 1) {
			this.currentLevel++;
		}
	}
	
	public void addItemToEachBlock(ITemplateItem item) {
		getCurrentEach().appendContent(item);
	}
	
	public void closeEachBlock() {
		TemplateEachToken closedToken = getCurrentEach();
		closedToken.setIsClosed(true);
		this.openTokens.remove(this.currentLevel);
		if (this.currentLevel == 0) {
			// store token until we grab it from the store
			this.closedToken = closedToken;
		} else {
			// otherwise, add token to its parent token
			this.currentLevel--;
			getCurrentEach().appendContent(closedToken);
		}
	}
	
	private TemplateEachToken getCurrentEach() {
		return this.openTokens.get(this.currentLevel);
	}
}
