package http;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;

import model.HttpRequestImpl;
import model.HttpResponseImpl;

import org.junit.Test;

import exceptions.EncodingException;
import exceptions.ResponseException;

import junit.framework.Assert;

public class HttpImplTest {
	@Test
	public void RequestReadWriteTest() {
		final String a = "GET /a/diff/619/1518794/show26.asp?1518794;228583;3546552324489692425;69273477;S;systemtarget=%24a%3D0t%3B%24cn%3DAR_07%3B%24isp%3D0%3B%24qc%3D1307001397%3B%24ql%3Dmedium%3B%24qpc%3D1605%3B%24qpp%3D0%3B%24qt%3D162_1161_76049t%3B%24b%3D12999%3B%24o%3D12100%3B%24sh%3D1024%3B%24sw%3D1600; HTTP/1.1\r\n" +
				"Accept-Language: en-us,en;q=0.5\r\n" +
				"Max-Forwards: 5\r\n" +
				"Cookie: pbw=%24b%3D12999%3B%24o%3D12100%3B%24sh%3D1024%3B%24sw%3D1600; TestIfCookieP=ok; pdomid=10; dyncdn=limit; pid=3546552324489692425; pbwmaj6=y; sasd=%24a%3D0t%3B%24cn%3DAR%5F07%3B%24isp%3D0%3B%24qc%3D1307001397%3B%24ql%3Dmedium%3B%24qpc%3D1605%3B%24qpp%3D0%3B%24qt%3D162%5F1161%5F76049t; vs=32872=4958112&31787=4957695\r\n" +
				"Host: ww619.smartadserver.com\r\n" +
				"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n" +
				"Referer: http://www.clarin.com/\r\n" +
				"Accept-Encoding: gzip, deflate\r\n" +
				"Via: HTTP/1.1 IlMarseProxy\r\n" +
				"User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10.7; rv:6.0.2) Gecko/20100101 Firefox/6.0.2\r\n" +
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n" +
				"Proxy-Connection: keep-alive\r\n\r\n";
		
		InputStream in1 = new InputStream() {
			int p;
			@Override
			public int read() throws IOException {
				if(p<a.length())
					return a.charAt(p++);
				return -1;
			}
		};
		InputStream in2 = new InputStream() {
			int p;
			@Override
			public int read() throws IOException {
				if(p<a.length())
					return a.charAt(p++);
				return -1;
			}
		};
		HttpRequestImpl req1;
		HttpRequestImpl req2;
		try {
			req1 = new HttpRequestImpl(in1);
			req2=new HttpRequestImpl(in2);
			Assert.assertEquals(req1.toString(), req2.toString());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void ResponseReadWriteTest() {
		final String a = "HTTP/1.1 200 OK\r\n" +
				"Date: Tue, 05 Jun 2012 03:29:42 GMT\r\n" +
				"Server: Apache\r\n" +
				"Last-Modified: Mon, 04 Jun 2012 19:16:49 GMT\r\n" +
				"Accept-Ranges: bytes\r\n" +
				"Vary: Accept-Encoding,User-Agent\r\n" +
				"Content-Encoding: gzip\r\n" +
				"X-Filmed-By: Filmed by Lemonhead\r\n" +
				"Cache-Control: max-age=0, no-cache, no-store, must-revalidate\r\n" +
				"Pragma: no-cache\r\n" +
				"Expires: Thu, 01 Dec 1994 00:00:00 GMT\r\n" +
				"Content-Length: 634\r\n" +
				"Content-Type: application/javascript\r\n\r\n";
		
		InputStream in1 = new InputStream() {
			int p;
			@Override
			public int read() throws IOException {
				if(p<a.length())
					return a.charAt(p++);
				return -1;
			}
		};
		InputStream in2 = new InputStream() {
			int p;
			@Override
			public int read() throws IOException {
				if(p<a.length())
					return a.charAt(p++);
				return -1;
			}
		};
		HttpResponseImpl res1;
		HttpResponseImpl res2;
		try {
			res1 = new HttpResponseImpl(in1);
			res2 = new HttpResponseImpl(in2);
			Assert.assertEquals(res1.toString(), res2.toString());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
