package nvn.external.scraper.reviews.model;
/**
 * 
 * @author nniyati
 *
 */
public class Review {
	private String reviewer;
	private String date;
	private String content;
	private boolean dealerRecommended;
	private int weight5Star; //keeps count of 5 star rating
	public String getReviewer() {
		return reviewer;
	}
	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isDealerRecommended() {
		return dealerRecommended;
	}
	public void setDealerRecommended(boolean dealerRecommended) {
		this.dealerRecommended = dealerRecommended;
	}
	public int getWeight5Star() {
		return weight5Star;
	}
	public void setWeight5Star(int weight5Star) {
		this.weight5Star = weight5Star;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (null == obj)
			return false;
		return this.toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(200);
		sb.append("Reviewer:");
		sb.append(getReviewer());
		sb.append(", Date:");
		sb.append(getDate());
		sb.append(", Dealer Recommended:");
		sb.append(isDealerRecommended());
		sb.append(", 5 Star Rating Weight:");
		sb.append(getWeight5Star());
		
		return sb.toString();
	}
}
