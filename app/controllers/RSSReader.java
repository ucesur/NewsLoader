package controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import models.Article;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RSSReader {
	private static RSSReader instance = null;

	private RSSReader() {
	}

	public static RSSReader getInstance() {
		if (instance == null)
			instance = new RSSReader();
		return instance;
	}

	public Article getArticle(String url) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			URL u = new URL(url); // your feed
			Document doc = builder.parse(u.openStream());
			NodeList nodes = doc.getElementsByTagName("item");
			Element element = (Element) nodes.item(0);

			Article article = new Article();
			article.author = getElementValue(element, "dc:creator");
			article.description = getElementValue(element, "description");
			article.link = getElementValue(element, "guid");
			article.title = getElementValue(element, "title");
			article.pubDate = getElementValue(element, "pubDate");
			
			return article;
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) {

		}
		return "";
	} // private String getCharacterDataFromElement

	protected float getFloat(String value) {
		if (value != null && !value.equals(""))
			return Float.parseFloat(value);
		else
			return 0;
	}

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}

}