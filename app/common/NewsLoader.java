package common;

import java.util.List;

import models.Article;
import models.Tag;
import controllers.Parser;
import controllers.RSSReader;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.WS;

@Every("20min")
public class NewsLoader extends Job {

	private static String urls[] = {
			"http://feeds.nytimes.com/nyt/rss/HomePage",
			"http://feeds.bbci.co.uk/news/rss.xml",
			"http://feeds.guardian.co.uk/theguardian/uk/rss" };
	private static String names[] = { "nytimes", "bbc", "guardian" };

	@Override
	public void doJob() throws Exception {

		for (int i = 0; i < 3; i++) {
			String url = urls[i];
			String name = names[i];

			Article article = RSSReader.getInstance().getArticle(url);
			String link = article.link;
			String urlForJson = Parser.createJsonTag(name, link);
			Tag jsonTag = new Tag();
			jsonTag.tagFetcher(urlForJson);
			article.content = Parser.getContent(urlForJson);
			List<Tag> tags;

			if (Article.find("link = ?", article.link).first() == null) {
				article.save();
				Article artID = Article.find("link = ?", article.link)
						.first();
				tags = Parser.createTags(jsonTag, artID.getId());
				for (Tag tag : tags) {
					tag.save();
				}
			}
		}
	}
}
