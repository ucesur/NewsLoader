package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jsoup.Jsoup;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import models.Article;
import models.Tag;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class Parser {

	public static List<Tag> createTags(Tag jsonTag, long id) {
		List<Tag> tags = new ArrayList<Tag>();
		JSONObject json = (JSONObject) JSONSerializer.toJSON(jsonTag.tag);

		for (int i = 1; i < json.names().size(); i++) {
			JSONObject jsonName = json.getJSONObject(json.names().getString(i));

			if (jsonName.getString("_typeGroup").equals("socialTag")) {
				// || jsonName.getString("_typeGroup").equals("entities")
				String tagName = jsonName.getString("name");
				Tag tag = new Tag();
				tag.tag = tagName;
				tag.article_id = id;
				tags.add(tag);
			}
		}
		return tags;
	}

	public static String createJsonTag(String name, String link)
			throws InstantiationException, IllegalAccessException {

		String print = "";

		if (name.equals("bbc")) {
			print = "?print=true";
		} else if (name.equals("wsj")) {
			print = "#printMode";
		} else if (name.equals("nytimes")) {
			print = "?pagewanted=print";
		} else if (name.equals("guardian")) {
			print = "/print";
		}

		return link + print;
	}

	public static String getContent(String url) {

		String content = "";
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.connect(url).get();
			org.jsoup.select.Elements heads = doc.select("h1");
			org.jsoup.select.Elements bodies = doc.select("p");

			for (org.jsoup.nodes.Element head : heads) {
				content += head.text() + "\n";
			}

			for (org.jsoup.nodes.Element body : bodies) {
				content += body.text() + "\n";
			}
			return content;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
