package nvn.external.scraper.reviews;

import java.io.IOException;

import org.jsoup.nodes.Document;

@FunctionalInterface
public interface ReadDcoument {
	Document getDocument(int pageNo) throws IOException;
}
