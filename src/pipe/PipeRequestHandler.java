package pipe;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * 
 * 处理request内容
 * 
 * @author gtt
 *
 */
class PipeRequestHandler implements HttpRequestHandler {

	private long start;
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
		start = 0;		
		// 判断是否为断点续传请求
		if (request.containsHeader("Range")) {
			String range = request.getFirstHeader("Range").getValue();
			if (range != null && range.indexOf("=") > 0) {
				String startStr = range.substring(range.indexOf("=") + 1, range.length() - 1);
				start = Long.parseLong(startStr);
			}
		}
		
		//response.setEntity(new StringEntity("Hello World", "UTF-8"));
		
		// 设置状态码
		response.setStatusCode(start > 0 ? HttpStatus.SC_PARTIAL_CONTENT : HttpStatus.SC_OK);
		
		// 设置Last-Modified
		SimpleDateFormat gmtFrmt = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		// 填充response body
		EntityTemplate entity = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {

				try {

					// 处理OutputStream
					outstream.write("Hello world".getBytes());
					
				} catch (Exception e) {
					if (!(e instanceof SocketException)) {
						System.out.println("stream output error" + e);
					}
				} 
				
			}
		});
		response.setEntity(entity);
	}
	
}