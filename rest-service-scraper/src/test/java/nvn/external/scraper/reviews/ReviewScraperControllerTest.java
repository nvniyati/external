package nvn.external.scraper.reviews;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nvn.external.scraper.reviews.model.Review;
import nvn.external.scraper.reviews.model.ReviewScraper;

@SpringBootTest
class ReviewScraperControllerTest {
	Logger logger = LoggerFactory.getLogger(ReviewScraperControllerTest.class);
	@Autowired
	ReviewScraperController reviewScraperController;
	
	@Autowired
	ReviewScraper reviewScraper;
	
	//lists holds expected values
	private static List<Review> listOverlyPostiveReviews = new ArrayList<Review>();
	private List<Review> listOffensiveReviews = new ArrayList<Review>();
	private List<Review> listSortedOffensive = new ArrayList<Review>();
	
	@BeforeAll
	public static void setupData() {
		setOverlyPositiveReviews();
	}

	@BeforeEach
	void setUp() throws Exception{
		setOffensiveReviews();
		setSortedOffensiveReviews();
	}

	@Test
	@DisplayName("Test for overly positive reviews")
	void testGetOverlyPositiveReviews() {
		logger.info("******************************************");
		logger.info("***** testGetOverlyPositiveReviews: Starting to scrape Overly Positive Reviews .... *****");
		logger.info("******************************************");
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
	
		try {
			List<Review> listActualReview = reviewScraperController.getOverlyPositiveReviews(1);
			assertNotNull(listActualReview, "Expected to return 10 elements");
			assertTrue(listOverlyPostiveReviews.size() == listActualReview.size());
			logger.info("Expected Overly positive Reviews Count = " + listOverlyPostiveReviews.size() + ", Actual Review Size = " + listActualReview.size());
			logger.info("Overly Positive Reviews List:");
			for(Review r:listActualReview) {
				logger.debug(r.toString());
			}
			assertTrue(listActualReview.containsAll(listOverlyPostiveReviews));
			logger.info("***** testGetOverlyPositiveReviews: Done *****");
		} catch (IOException e) {
			e.printStackTrace();
			fail("testGetOverlyPositiveReviews failed with IOException");
		}
		
	}
	
	public Document simulateHttpDoc(int pageNo) throws IOException {
		File testFile = new File("src/test/resources/test.html");
		Document htmlDoc = Jsoup.parse(testFile, "UTF-8", "");
		return htmlDoc;
	}

	@Test
	void testGetOffensiveReviews() {
		logger.info("******************************************");
		logger.info("***** testGetOffensiveReviews: Starting to scrape Offensive Reviews .... *****");
		logger.info("******************************************");
		
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
		List<Review> listActualReview;
		try {
			listActualReview = reviewScraperController.getOffensiveReviews(1);
			assertNotNull(listActualReview, "Expected to return 9 elements");
			assertTrue(listOffensiveReviews.size() == listActualReview.size(), "Expected Size=" + listOffensiveReviews.size() + ", Actual Size = " + listActualReview.size() );
			logger.info("Expected Offensive Reviews Count = " + listOffensiveReviews.size() + ", Actual Review Size = " + listActualReview.size());
			logger.debug("Offensive Reviews List:");
			for(Review r:listActualReview) {
				logger.debug(r.toString());
			}
			assertTrue(listActualReview.containsAll(listOffensiveReviews));
		} catch (IOException e) {
			e.printStackTrace();
			fail(" testGetOffensiveReviews Failed with IOException");
		}
		logger.info("***** testGetOffensiveReviews: Done *****");
		
		
	}

	@Test
	void testSortReviews() {
		logger.info("******************************************");
		logger.info("***** testSortReviews: Sorting Offensive Reviews .... *****");
		logger.info("***** SORTING CRITERIA: Most Severe Offense = Recommended Dealer=NO (Dealer Recommended:FALSE) ****");
		logger.info("SECONDLY, 5 Star Rating Weight: less the weight offense is severe. (weight = number of categories with 5 star rating )*****");
		logger.info("******************************************");
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
		
		logger.info("--------------------------------------");
		logger.info("Top 5 Offensive Reviews...");
		logger.info("--------------------------------------");
		List<Review> listActualReview;
		try {
			listActualReview = reviewScraperController.sortReviews(1, 5);
			assertNotNull(listActualReview, "Expected to return 5 elements");
			assertTrue(listActualReview.size() == 5, "Expected Size= 5" + ", Actual Size = " + listActualReview.size());
			logger.info("Expected Top 5 offensive Reviews, Actual Review Size = " + listActualReview.size());
			logger.debug("SORTED Offensive Reviews TOP 5 List:");
			for(Review r:listActualReview) {
				logger.debug(r.toString());
			}
			
			assertTrue(listActualReview.containsAll(listSortedOffensive.subList(0, 5)));
			logger.info("--------------------------------------");
			logger.info("Top 2 Offensive Reviews...");
			logger.info("--------------------------------------");
			listActualReview = reviewScraperController.sortReviews(1, 2);
			assertNotNull(listActualReview, "Expected to return 9 elements");
			assertTrue(listActualReview.size() == 2, "Expected Size= 2" + ", Actual Size = " + listActualReview.size());
			logger.info("Expected Top 2 offensive Reviews, Actual Review Size = " + listActualReview.size());
			logger.debug("SORTED Offensive Reviews TOP 2 List:");
			for(Review r:listActualReview) {
				logger.debug(r.toString());
			}
			
			assertTrue(listActualReview.containsAll(listSortedOffensive.subList(0, 2)));
		} catch (IOException e) {
			e.printStackTrace();
			fail("testSortReviews failed for TOP 5 scenario");
		}
		
		logger.info("--------------------------------------");
		logger.info("Top 20 Offensive Reviews, however there are ONLY 9 are available so should return 9 ...");
		logger.info("--------------------------------------");
		try {
			listActualReview = reviewScraperController.sortReviews(1, 20);
			assertNotNull(listActualReview, "Expected to return 9 elements");
			assertTrue(listActualReview.size() <= 20);
			logger.info("Expected Top <=20 offensive Reviews, Actual Review Size = " + listActualReview.size());
			logger.debug("SORTED Offensive Reviews TOP <=20 List:");
			for(Review r:listActualReview) {
				logger.debug(r.toString());
			}
			
			assertTrue(listActualReview.containsAll(listSortedOffensive));
		} catch (IOException e) {
			e.printStackTrace();
			fail("testSortReviews failed for TOP 20 scenario");
		}
	
		
		logger.info("Top 0 Offensive Reviews...");
		logger.info("--------------------------------------");
		try {
			listActualReview = reviewScraperController.sortReviews(1, 0);
			assertNotNull(listActualReview, "Expected to return 9 elements");
			assertTrue(listActualReview.size()==0);
			logger.info("Expected Top 0 offensive Reviews, Actual Review Size = " + listActualReview.size());
			logger.debug("SORTED Offensive Reviews TOP 0 List:");
			if(listActualReview.size()==0)
			{
				logger.debug("EMPTY list FOUND");
			}
			assertTrue(listActualReview.isEmpty());
		} catch (IOException e) {
			e.printStackTrace();
			fail("testSortReviews failed for TOP 0 scenario");
		}
		
		logger.info("***** testSortReviews: Done *****");
	}

	private static void setOverlyPositiveReviews() {
		addReview("- Melisaswart", "June 17, 2021", "", true, 4);
		addReview("- Kristy", "June 14, 2021", "", true, 4);
		addReview("- Erinjeffers5", "June 12, 2021", "", true, 5);
		addReview("- Cullipher’s", "June 11, 2021", "", true, 3);
		addReview("- Pamelachristopher", "June 11, 2021", "", true, 4);
		
		addReview("- Mary1246", "June 10, 2021", "", true, 3);
		addReview("- Randi Zuniga", "June 09, 2021", "", true, 3);
		addReview("- Patricia McKinney", "June 07, 2021", "", true, 4);
		addReview("- Brandy", "June 07, 2021", "", true, 4);
		addReview("- Watsonamy35", "June 01, 2021", "", false, 4);
	}
	
	private void setOffensiveReviews() {
		
		listOffensiveReviews= listOverlyPostiveReviews.stream().filter(r->!r.isDealerRecommended()).collect(Collectors.toList());
		listOffensiveReviews.addAll(listOverlyPostiveReviews.stream().filter(r->r.isDealerRecommended()).filter(r->r.getWeight5Star() < 5).collect(Collectors.toList()));
	}
	
	private void setSortedOffensiveReviews() {
		listSortedOffensive = listOverlyPostiveReviews.stream().filter(r->!r.isDealerRecommended()).sorted(reviewScraper.byRating).collect(Collectors.toList());
		listSortedOffensive.addAll(listOverlyPostiveReviews.stream().filter(r->r.isDealerRecommended()).filter(r->r.getWeight5Star() < 5).sorted(reviewScraper.byRating).collect(Collectors.toList()));
 	}
	
	public static void addReview(String reviewer, String date, String content, boolean isRecommended, int star5) {
		Review review = new Review();
		review.setReviewer(reviewer);
		review.setDate(date);
		review.setContent(content);
		review.setDealerRecommended(isRecommended);
		review.setWeight5Star(star5);
		listOverlyPostiveReviews.add(review);
	}
}
