package nvn.external.scraper.reviews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nvn.external.scraper.exceptions.ResourceNotFoundException;
import nvn.external.scraper.reviews.model.Review;
import nvn.external.scraper.reviews.model.ReviewScraper;
import nvn.external.scraper.reviews.model.ReviewsCriteria;

/**
 * 
 * @author nniyati
 *
 */
@RestController

public class ReviewScraperController {

	@Value("${source.review.url}")
	private String SOURCE_URL;

	@Value("${source.default.pagecount}")
	private String DEFAULT_PAGECOUNT;

	@Autowired
	ReviewScraper reviewScraper;

	ReadDcoument docReader = i -> getHttpDocument(i);

	@RequestMapping("")
	public String displayApplicationTitle() {
		return "Welcome to Review Scraper";
	}

	@RequestMapping("/reviews/overlyPositive")
	public List<Review> getOverlyPositiveReviews(@RequestParam(value = "pages", defaultValue = "5") int pages)
			throws IOException {
		ReviewsCriteria funcAllPositiveReviews = reviewScraper.getOverlyPositive();
		List<Review> listAllReviews = new ArrayList<>();
		if (null != funcAllPositiveReviews) {
			listAllReviews = getCriteriaReview(pages, funcAllPositiveReviews);
		}

		return listAllReviews;
	}

	@RequestMapping("/reviews/offensive")
	public List<Review> getOffensiveReviews(@RequestParam(value = "pages", defaultValue = "5") int pages)
			throws IOException {
		List<Review> listAllReviews = new ArrayList<>();

		ReviewsCriteria funcOffensiveReviews = reviewScraper.getOffensive();

		if (null != funcOffensiveReviews) {
			listAllReviews = getCriteriaReview(pages, funcOffensiveReviews);
		}

		return listAllReviews;
	}

	@RequestMapping("/reviews/offensive/sort")
	public List<Review> sortReviews(@RequestParam(value = "pages", defaultValue = "5") int pages,
			@RequestParam(value = "topCount", defaultValue = "3") int topCount) throws IOException {

		// get all offensive reviews
		List<Review> listOffensiveReviews = getOffensiveReviews(pages);

		if (null != listOffensiveReviews) {
			// sort offensive reviews based on criteria
			listOffensiveReviews = reviewScraper.sortOffensiveReviews(listOffensiveReviews);
		}

		// return only as many as asked or all or max 3 (default 3)
		return ((null != listOffensiveReviews) && (listOffensiveReviews.size() > topCount))
				? listOffensiveReviews.subList(0, topCount)
				: listOffensiveReviews;
	}

	private List<Review> getCriteriaReview(int countPages, ReviewsCriteria funcCriteria) throws IOException {
		List<Review> listAllReviews = new ArrayList<>();
		for (int pageNo = 1; pageNo <= countPages; pageNo++) {

			Document doc = docReader.getDocument(pageNo);
			if (null == doc) {
				continue;
			}
			reviewScraper.setHtmlDocument(doc);
			List<Review> listReviews = funcCriteria.runCriteria();
			if (null != listReviews) {
				listAllReviews.addAll(listReviews);
			}

		}
		return listAllReviews;
	}

	public void setHTTPDoccReader(ReadDcoument funcDocReader) {
		this.docReader = funcDocReader;
	}

	private Document getHttpDocument(int pageNo) throws IOException {
		String url = String.format(SOURCE_URL, pageNo);
		return Jsoup.connect(url).get();
	}
}
