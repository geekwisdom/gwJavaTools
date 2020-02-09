package org.geekwisdom;
public class GWException extends Exception {

	/**
	 * 
	 */
	private long errorCode;
	private static final long serialVersionUID = 1L;
	public GWException(String errorMessage, int errorCde) {
        super(errorMessage);
        this.errorCode = errorCde;
    }
	
	public GWException(String errorMessage) {
        super(errorMessage);
        this.errorCode = -1;
    }
	
	public long getErrorCode() {
	        return errorCode;
	    }
}
