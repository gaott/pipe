package pipe;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;


/**
 * HttpServer类
 * 
 * @author gtt
 *
 */
public class PipeServer {

	private static final String ALL_PATTERN = "*";

	private boolean RUNNING = false;
	
	private BasicHttpProcessor httpprocessor = null;
	private BasicHttpContext httpContext = null;
	private HttpService httpService = null;
	private HttpRequestHandlerRegistry registry = null;

	private int port;
	private ServerSocket serverSocket;
	
	public PipeServer() {
		httpContext = new BasicHttpContext();

		httpprocessor = new BasicHttpProcessor();
		httpprocessor.addInterceptor(new PipeResponseContent());

		httpService = new HttpService(httpprocessor,
				new DefaultConnectionReuseStrategy(),
				new DefaultHttpResponseFactory());

		registry = new HttpRequestHandlerRegistry();

		registry.register(ALL_PATTERN, new PipeRequestHandler());

		httpService.setHandlerResolver(registry);
	}

	/**
	 * 启动服务
	 */
	public void start() {
		RUNNING = true;
		
		if (serverSocket == null) {
			try {

				serverSocket = new ServerSocket();
				serverSocket.setReuseAddress(true);
				serverSocket.bind(new InetSocketAddress(0));
				port = serverSocket.getLocalPort();

			} catch (IOException e) {
				System.out.println(e.getMessage() + " BP: " + port);
				return;
			}
		}
		
		System.out.println("server start. port: " + port);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				runServer();
			}
		}).start();
	}

	/**
	 * 停止服务
	 */
	public void stop() {
		RUNNING = false;
	}
	
	/**
	 * 获取服务端口号
	 * 
	 * @return 服务端口号
	 */
	public int getPort(){
		return port;
	}
	
	private void runServer() {
		try {
			while (RUNNING) {

				Socket currentSocket = null; 
				DefaultHttpServerConnection httpServerConnection = new DefaultHttpServerConnection();
				
				try {
					
					currentSocket = serverSocket.accept();
					httpServerConnection.bind(currentSocket, new BasicHttpParams());
					httpService.handleRequest(httpServerConnection, httpContext);
					
				} catch (IOException e) {
					System.out.println("io error" + e.getMessage());
				} catch (HttpException e) {
					System.out.println("http error" + e.getMessage());
				} finally {

					try {
						currentSocket.close();
					} catch (Exception e) {
						System.out.println("socket error" + e.getMessage());
					}
					
					try {
						httpServerConnection.shutdown();
					} catch (Exception e) {
						System.out.println("HTTP Server Connetion error" + e.getMessage());
					}
				}
			}

			System.out.println("server close.");
			serverSocket.close();
		} catch (SocketException e) {
			System.out.println("server error " + e.getMessage());
		} catch (IOException e) {
			System.out.println("server error" + e.getMessage());
		}
		
		RUNNING = false;
	}
	
}