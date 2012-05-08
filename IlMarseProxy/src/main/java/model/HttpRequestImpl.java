package model;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import exceptions.BadResponseException;



public class HttpRequestImpl extends HttpMsg implements HttpRequest {
	public enum ImplementedMethod{
		GET,HEAD,POST
	}
	private ImplementedMethod method;
	private String requestURI;
	private HttpVersion version;
	public HttpRequestImpl(final InputStream in) throws BadResponseException {
		super(in);
	}

	@Override
	void parseFirstLine(String line) {
		String aux[] = line.split("[ |\t]+");
		method = ImplementedMethod.valueOf(aux[0]);
		requestURI = aux[1];
		if(aux[2].equals("HTTP/1.0")){
			version=HttpVersion.HTTP_1_0;
		}else{
			if(aux[2].equals("HTTP/1.1")){
				version=HttpVersion.HTTP_1_1;
			}else{
				version=HttpVersion.BAD_REQUEST;
			}
		}

	}
	
	public ImplementedMethod getMethod(){
		return method;
	}
	public String getRequestURI(){
		return requestURI;
	}
	public HttpVersion getVersion(){
		return version;
	}

	@Override
	public String toString() {
		if(version.equals(HttpVersion.BAD_REQUEST)) return "";
		String v = version.equals(HttpVersion.HTTP_1_0)?"HTTP/1.0":"HTTP/1.1";
		String s=method + " " + requestURI + " " + v + "\r\n";
		for(String header: getHeaders().keySet()){
			s = s + header + ": " + getHeader(header) + "\r\n";
		}
		return s+"\r\n";
		
	}
	//ONLY FOR TESTING AND EXAMPLE
	public static void test() {
		final String aux = "GET /javase/1.4.2/docs/api/resources/inherit.gif HTTP/1.1\r\nHost: docs.oracle.com\r\nConnection: keep-alive\r\n" +
				"		Cache-Control: max-age=0\r\n" +
				"		If-Modified-Since: Wed, 07 Sep 2011 12:58:37 GMT\r\n" +
				"		User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.168 Safari/535.19\r\n" +
				"		If-None-Match: \"220f4eda0bd49915699315f18b8b03cf:1326411148\"\r\n" +
				"		Accept: */*\r\n" +
				"		Referer: http://docs.oracle.com/javase/1.4.2/docs/api/java/util/regex/Pattern.html\r\n" +
				"		Accept-Encoding: gzip,deflate,sdch\r\n" +
				"		Accept-Language: en-US,en;q=0.8\r\n" +
				"		Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n" +
				"		Cookie: s_nr=1336338075458; tutorial_showLeftBar=yes; s_cc=true; s_sq=%5B%5BB%5D%5D";
		final InputStream a = new InputStream() {
			int i=0;
			String a = aux;

			@Override
			public int read() throws IOException {
				if(i<aux.length())
					return aux.charAt(i++);
				return -1;
			}
		};
		HttpRequestImpl req =null;
		try {
			req = new HttpRequestImpl(a);
//			System.out.println(req.getRequestURI() + req.getMethod() + req.getVersion());
//			System.out.println(req.getHeaders());
			System.out.println(req);
		} catch (BadResponseException e) {
			e.printStackTrace();
		}
		final String aux2=req.toString();
		final InputStream b = new InputStream() {
			int i=0;
			String a = aux2;

			@Override
			public int read() throws IOException {
				if(i<aux2.length())
					return aux2.charAt(i++);
				return -1;
			}
		};
		try {
			req = new HttpRequestImpl(b);
//			System.out.println(req.getRequestURI() + req.getMethod() + req.getVersion());
//			System.out.println(req.getHeaders());
			System.out.println(req);
		} catch (BadResponseException e) {
			e.printStackTrace();
		}
	}
	
}
