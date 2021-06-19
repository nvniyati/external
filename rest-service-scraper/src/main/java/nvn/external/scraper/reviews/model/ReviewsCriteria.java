package nvn.external.scraper.reviews.model;

import java.util.List;

@FunctionalInterface
public interface ReviewsCriteria {
	List<Review> runCriteria();
}
