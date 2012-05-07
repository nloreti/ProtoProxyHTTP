package model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestStringImpl implements HTTPRequest{
	private enum ImplementedMethods{
		GET,HEAD,POST
	}
	private enum HttpVersion{
		HTTP_1_0,HTTP_1_1,BAD_REQUEST
	}
	private Map<String,String> headers = new HashMap<String, String>();
	private ImplementedMethods method;
	private String requestURI;
	private HttpVersion version;
	
	public static void main(String[] args) {
		String aux = "GET /javase/1.4.2/docs/api/resources/inherit.gif HTTP/1.1\r\nHost: docs.oracle.com\r\nConnection: keep-alive\r\n" +
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

		
		HttpRequestStringImpl h = new HttpRequestStringImpl("GET hola,	 HTTP/1.1 como va 		  \r\nhola cmo va\r\n Accept-Encoding: chunk\r\nHosts: Http://www.google.com\r\n\r\n hola com va???ASdfasdf asdfasd");
		h=new HttpRequestStringImpl(aux);
	}
	
	public HttpRequestStringImpl(String message) {
		String header = message.split("\r\n\r\n")[0];
		String lines[]=header.split("\r\n");
		parseFirstLine(lines[0]);
		parseHeaders(lines);
		System.out.println(method + requestURI + version);
		System.out.println(headers);
	}
	private void parseFirstLine(String line){
		String aux[] = line.split("[ |\t]+");
		method = ImplementedMethods.valueOf(aux[0]);
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
	private void parseHeaders(String[] lines){
		for(int i=1;i<lines.length;i++){
			if("".equals(lines[i])) break;
			parseHeaderLine(lines[i]);
		}
	}
	private void parseHeaderLine(String line){
		String[] aux = line.split(":");
		if(aux.length>1){
			String header = aux[0].trim();
			String value = line.substring(aux[0].length()+1).trim();
			System.out.println("aux0 = " + aux[0]);
			headers.put(header, value);
		}
	}
}
