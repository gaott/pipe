package pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Demo {

	public static void main(String[] args) throws IOException {
		PipeServer pipeServer = new PipeServer();
		pipeServer.start();
		
		String path = "http://127.0.0.1:" + pipeServer.getPort();
		System.out.println("server path: " + path);
		System.out.println("response: " + retrieve(path));
		
		pipeServer.stop();
	}
	
	public static String retrieve(String path) throws IOException{
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine = null;
		while ((inputLine = reader.readLine()) != null) {
			buffer.append(inputLine);
		}
			
		return buffer.toString();
	}
}
