package nvn.external.scraper.reviews.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nvn.external.scraper.exceptions.ResourceNotFoundException;

/**
 * Scraper to extract overly rated reviews.
 * Def: Overly rated: Overly rating= 5 stars, positive rating
 * Def: Offensive rating: an overly rated review with Recommended Dealer = NO OR Any of the 5 category ratings < 5 stars
 * 5 rating categories: Customer service, Quality of work, friendliness, pricing, overall experience, recommend dealer
 * 
 * @author nniyati
 *
 */

@Service
public class ReviewScraper {
	@Value("${review.class.entry}")
	private String REVIEW_ENTRY_CLASS;

	@Value("${review.class.date:italic col-xs-6 col-sm-12 pad-none margin-none font-20}")
	private String REVIEW_DATE_CLASS;

	@Value("${review.class.name:italic font-18 black notranslate}")
	private String REVIEW_NAME_CLASS;

	@Value("${review.class.content:font-16 review-content margin-bottom-none line-height-25}")
	private String REVIEW_CONTENT_CLASS;

	@Value("${review.class.recommenddealer:td small-text boldest}")
	private String REVIEW_RECOMMEND_DEALER_CLASS;
	
	@Value("${review.class.ratingtable:table width-100 pad-left-none pad-right-none margin-bottom-md}")
	private String REVIEW_RATING_TABLE_CLASS;
	
	@Value("${review.class.ratingtable.star.5:rating-static-indv rating-50 margin-top-none td}")
	private String REVIEW_5_STAR_RATING;

	Logger logger = LoggerFactory.getLogger(ReviewScraper.class);

	private static final String YES = "YES";

	private Document htmlDocument;

	// Functional Programming using lambda
	private Predicate<Review> isStar5Rater = r -> r.getWeight5Star() < 5;
	private ReviewsCriteria overlyPositive = () -> getOverlyPositiveReviews();
	private ReviewsCriteria offensive = () -> getOffensiveReviews();
	
	// Comparators
	public Comparator<Review> byRating = Comparator.comparing(Review::getWeight5Star);
	
	public ReviewScraper() {
		
	}
	public List<Review> getOverlyPositiveReviews() {
		List<Review> reviews = new ArrayList<>();
		if (null == this.htmlDocument) {
			return null;
		}

		Elements elmReviews = htmlDocument.getElementsByClass(REVIEW_ENTRY_CLASS);

		for (Element elmReview : elmReviews) {
			Review review = new Review();

			Elements element = null;
			element = elmReview.getElementsByClass(REVIEW_RATING_TABLE_CLASS);
			if (!element.isEmpty()) {

				Element elmRatingTable = element.get(0);
				if (null != elmRatingTable) {
					Elements elmStar5 = elmRatingTable
							.getElementsByClass(REVIEW_5_STAR_RATING);
					int star5Ratings = (null == elmStar5) ? 0 : elmStar5.size();
					review.setWeight5Star(star5Ratings);
				}
			}

			element = elmReview.getElementsByClass(REVIEW_DATE_CLASS);
			if (!element.isEmpty()) {
				review.setDate(element.get(0).ownText());
			}

			element = elmReview.getElementsByClass(REVIEW_NAME_CLASS);
			if (!element.isEmpty()) {
				review.setReviewer(element.get(0).ownText());
			}

			/*
			 * element = elmReview.getElementsByClass(REVIEW_CONTENT_CLASS); if
			 * (!element.isEmpty()) { review.setContent(element.get(0).ownText()); }
			 */
			element = elmReview.getElementsByClass(REVIEW_RECOMMEND_DEALER_CLASS);
			if (!element.isEmpty()) {
				review.setDealerRecommended(YES.equalsIgnoreCase(element.get(0).ownText()));
			}
			
			reviews.add(review);
		}

		return reviews;
	}
	
	public List<Review> getOffensiveReviews() {
		List<Review> listReviews = getOverlyPositiveReviews();
		if (null == listReviews) {
			throw new ResourceNotFoundException("No Reviews Found");
		}
		// extract review with recommendation = no
		List<Review> listNoRecom = listReviews.stream().filter((r -> !r.isDealerRecommended()))
				.collect(Collectors.toList());

		// exclude reviewers that do not recommend the dealer and find those that have
		// rating under 5 star
		List<Review> listNotStar5 = listReviews.stream().filter((r -> r.isDealerRecommended())).filter(isStar5Rater)
				.collect(Collectors.toList());
		
		//to reuse the list, first clear it
		listReviews.clear();

		if (null != listNoRecom) {
			listReviews.addAll(listNoRecom);
		}
		if (null != listNotStar5) {
			listReviews.addAll(listNotStar5);
		}
		
		return listReviews;
	}
	

	/**
	 * retrieves all offensive reviews. (overly rated, 
	 * @param listReviews
	 * @return
	 */
	public List<Review> sortOffensiveReviews(List<Review> listReviews) {
		if (null == listReviews) {
			throw new ResourceNotFoundException("No Reviews Found");
		}
		// extract review with recommendation = no
		List<Review> notRecommended = listReviews.stream().filter(r -> !r.isDealerRecommended()).sorted(byRating).collect(Collectors.toList()); 
		//extract review with recommendation=YES but has any rating among 5 categories with < 5 stars
		List<Review> recommended = listReviews.stream().filter(r -> r.isDealerRecommended()).sorted(byRating).collect(Collectors.toList());
		
		//to reuse the list, first clear it
		listReviews.clear();
		
		if (null != notRecommended) {
			listReviews.addAll(notRecommended);
		}
		if (null != recommended) {
			listReviews.addAll(recommended);
		}

		return listReviews;
	}

	public ReviewsCriteria getOverlyPositive() {
		return overlyPositive;
	}

	public ReviewsCriteria getOffensive() {
		return offensive;
	}
	public void setHtmlDocument(Document htmlDocument) {
		this.htmlDocument = htmlDocument;
	}
}
