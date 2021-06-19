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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import nvn.external.scraper.reviews.model.Review;
import nvn.external.scraper.reviews.model.ReviewScraper;

@SpringBootTest
class ReviewScraperControllerTest {
	
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
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
		List<Review> listActualReview = reviewScraperController.getOverlyPositiveReviews(1);
		assertNotNull(listActualReview, "Expected to return 10 elements");
		assertTrue(listOverlyPostiveReviews.size() == listActualReview.size());
		assertTrue(listActualReview.containsAll(listOverlyPostiveReviews));
	}
	
	public Document simulateHttpDoc(int pageNo) throws IOException {
		File testFile = new File("src/test/resources/test.html");
		Document htmlDoc = Jsoup.parse(testFile, "UTF-8", "");
		return htmlDoc;
	}

	@Test
	void testGetOffensiveReviews() {
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
		List<Review> listActualReview = reviewScraperController.getOffensiveReviews(1);
		assertNotNull(listActualReview, "Expected to return 9 elements");
		assertTrue(listOffensiveReviews.size() == listActualReview.size(), "Expected Size=" + listOffensiveReviews.size() + ", Actual Size = " + listActualReview.size() );
		validateReview(listActualReview, listOffensiveReviews);
		assertTrue(listActualReview.containsAll(listOffensiveReviews));
	}

	@Test
	void testSortReviews() {
		reviewScraperController.setHTTPDoccReader((i)->simulateHttpDoc(i));
		List<Review> listActualReview = reviewScraperController.sortReviews(1, 5);
		assertNotNull(listActualReview, "Expected to return 9 elements");
		assertTrue(listActualReview.size() == 5, "Expected Size= 5" + ", Actual Size = " + listActualReview.size());
		assertTrue(listActualReview.containsAll(listSortedOffensive.subList(0, 5)));
		System.out.println("Actual Sorted Offensive Reviews, top 5: ");
		listActualReview.stream().forEach(System.out::println);
		validateReview(listActualReview, listSortedOffensive.subList(0, 5));
		
		listActualReview = reviewScraperController.sortReviews(1, 2);
		assertNotNull(listActualReview, "Expected to return 9 elements");
		assertTrue(listActualReview.size() == 2, "Expected Size= 2" + ", Actual Size = " + listActualReview.size());
		System.out.println("Actual Sorted Offensive Reviews, top 2: ");
		listActualReview.stream().forEach(r->System.out.println(r.toString()));
		assertTrue(listActualReview.containsAll(listSortedOffensive.subList(0, 2)));
		validateReview(listActualReview, listSortedOffensive.subList(0, 2));
		
		listActualReview = reviewScraperController.sortReviews(1, 20);
		assertNotNull(listActualReview, "Expected to return 9 elements");
		assertTrue(listActualReview.size() <= 20);
		System.out.println("Actual Sorted Offensive Reviews, top 20: ");
		listActualReview.stream().forEach(r->System.out.println(r.toString()));
		validateReview(listActualReview, listSortedOffensive);
		assertTrue(listActualReview.containsAll(listSortedOffensive));
		
		
		listActualReview = reviewScraperController.sortReviews(1, 0);
		assertNotNull(listActualReview, "Expected to return 9 elements");
		assertTrue(listActualReview.size()==0);
		System.out.println("Actual Sorted Offensive Reviews, top 0: ");
		listActualReview.stream().forEach(r->System.out.println(r.toString()));
		assertTrue(listActualReview.isEmpty());
		
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
	
	
	private void validateReview(List<Review> actualList, List<Review> expectedList) {
		for(Review r:actualList) {
			if(expectedList.contains(r)) {
				System.out.println("Found: " + r.getReviewer());
			}else {
				System.out.println("Not Found: " + r.getReviewer());
				fail("Review Not Matched: " + r.getReviewer());
			}
		}
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
