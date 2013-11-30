package controllers;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import play.Play;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;

public class TagFetcher {

	private static final String calaisAPIKey = Play.configuration
			.getProperty("open.calais.api.key");

	public static String getTags(String url) {

		String articleContent = Parser.getContent(url);

		WSRequest request = WS.url("http://api.opencalais.com/tag/rs/enrich");
		request.setHeader("x-calais-licenseID", calaisAPIKey);
		request.setHeader("Content-Type", "text/html; charset=UTF-8");
		request.setHeader("Accept", "application/json");
		request.setHeader("calculateRelevanceScore", "true");
		request.setHeader("enableMetadataType", "SocialTags");
		request.body(articleContent);
		String response = request.post().getString();
		//System.out.println(response);
		return response;

	}
}
