package pipe;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpDateGenerator;

/**
 * 
 * 自定义response
 * 
 * @author gtt
 *
 */
class PipeResponseContent implements HttpResponseInterceptor {

	private static final HttpDateGenerator DATE_GENERATOR = new HttpDateGenerator(); 
	 
    public PipeResponseContent() {
        super();
    }
    
    public void process(final HttpResponse response, final HttpContext context) 
            throws HttpException, IOException {
    	
    	if (response == null) {
            throw new IllegalArgumentException("HTTP request may not be null");
        }
    	
    	// Date
    	int status = response.getStatusLine().getStatusCode();
        if (status >= HttpStatus.SC_OK) {
            String httpdate = DATE_GENERATOR.getCurrentDate();
            response.setHeader(HTTP.DATE_HEADER, httpdate); 
        }
    	
        // Server
    	response.addHeader(HTTP.SERVER_HEADER, "nginx/1.2.5");
    	response.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);
    	response.setHeader(HTTP.CONTENT_TYPE, "text/html");
    	
    	/**
    	System.out.println(status + " " + response.getStatusLine().getReasonPhrase());
		for(Header header : response.getAllHeaders()){
			System.out.println(header.getName() + ": " + header.getValue());
		}
		*/
    }
    
}