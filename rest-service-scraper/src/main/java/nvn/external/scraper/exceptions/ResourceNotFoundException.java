package nvn.external.scraper.exceptions;

public class ResourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String errMsg) {
		super(errMsg);
	}

}
