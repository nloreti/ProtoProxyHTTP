package throughput;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.rmi.ServerException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import model.HttpRequestImpl;
import model.HttpResponseImpl;
import application.FilterHandler;
import application.FilterSocketServer;
import application.ProxyHTTP;
import application.Statistics;
import connection.ConnectionImpl;
import exceptions.EncodingException;
import exceptions.ResponseException;

public class ThroughputTest {

	public static void main(final String[] args) {
		testThroughput();
	}

	public static void testThroughputLocal() {
		final ExecutorService es = Executors.newFixedThreadPool(20);
		final int connections = 100;
		final AtomicInteger completed = new AtomicInteger();
		final AtomicInteger downloaded = new AtomicInteger();
		final AtomicInteger uploaded = new AtomicInteger();
		final AtomicInteger errors = new AtomicInteger();

		try {
			final FilterSocketServer filterServer = new FilterSocketServer(
					InetAddress.getByName("localhost"), new FilterHandler());

			new Thread(filterServer).start();
			final ProxyHTTP proxy = new ProxyHTTP();
			final Runnable r = new Runnable() {

				public void run() {
					proxy.run();

				}
			};
			es.execute(r);
			final long time = System.currentTimeMillis();
			for (int i = 0; i < connections; i++) {
				final HttpRequestImpl req;
				final String addr;
				switch (i % 4) {
				case 1:
					req = getImageRequest1();
					addr = "localhost";
					break;

				case 2:
				case 3:
				case 0:
					req = getImageRequest1();
					addr = "www.clarin.com";
					break;

				case 5:
					req = getImageRequest2();
					addr = "o1.t26.net";
					break;

				default:
					req = getImageRequest2();
					addr = "o1.t26.net";
					break;
				}
				es.execute(new Runnable() {

					public void run() {
						try {
							final ConnectionImpl c = new ConnectionImpl(
									InetAddress.getByName("www.clarin.com"), 80);
							c.send(req);

							uploaded.addAndGet(req.getWritten());
							final HttpResponseImpl res = c.receive();
							downloaded.addAndGet(res.getRead()
									+ res.getBodyBytes().length);
						} catch (final Exception e) {
							errors.incrementAndGet();
						} finally {
							completed.incrementAndGet();
						}
					}

				});
			}
			es.awaitTermination(10, TimeUnit.SECONDS);
			while (completed.intValue() < connections) {
				Thread.currentThread().sleep(20);
			}
			es.shutdownNow();
			// <<<<<<< HEAD
			// final double tp =
			// ((Statistics.getInstance().getProxyClientBytes() + Statistics
			// .getInstance().getProxyServerBytes()) / (System
			// .currentTimeMillis() - time));
			// final double tpc = Statistics.getInstance().getProxyClientBytes()
			// / (System.currentTimeMillis() - time);
			// System.out.println(Statistics.getInstance().getProxyClientBytes());
			// System.out.println("Total throughput = " + tp + "KB/s");
			// System.out.println("Max throughput to clients= " + tpc + "KB/s");
			// =======
			final double up = (uploaded.doubleValue() / (System
					.currentTimeMillis() - time));
			final double down = (downloaded.doubleValue() / (System
					.currentTimeMillis() - time));
			System.out.println("Errores: " + errors);
			System.out.println("Velocidad promedio de subida = " + up * 8
					+ "Kb/s");
			System.out.println("Velocidad promedio de bajada= " + down * 8
					+ "Kb/s");
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public static void testThroughput() {
		final ExecutorService es = Executors.newFixedThreadPool(100);
		final int connections = 100;
		final AtomicInteger completed = new AtomicInteger();
		final AtomicInteger errors = new AtomicInteger();
		try {
			final FilterSocketServer filterServer = new FilterSocketServer(
					InetAddress.getByName("localhost"), new FilterHandler());

			new Thread(filterServer).start();
			final ProxyHTTP proxy = new ProxyHTTP();
			final Runnable r = new Runnable() {

				public void run() {
					proxy.run();

				}
			};
			es.execute(r);
			final long time = System.currentTimeMillis();
			for (int i = 0; i < connections; i++) {
				es.execute(new Runnable() {

					public void run() {
						try {
							final HttpRequestImpl req = getImageRequest1();
							final ConnectionImpl c = new ConnectionImpl(
									InetAddress.getByName("localhost"), 8090);
							c.send(req);

							c.receive();
						} catch (final Exception e) {
							errors.incrementAndGet();
						} finally {
							completed.incrementAndGet();
						}
					}

				});
			}
			// es.awaitTermination(10, TimeUnit.SECONDS);
			while (completed.intValue() < connections) {
				Thread.currentThread().sleep(20);
			}
			es.shutdownNow();

			final double tp = (Statistics.getInstance().getProxyServerBytes() / (System
					.currentTimeMillis() - time));
			final double tpc = Statistics.getInstance().getProxyClientBytes()
					/ (System.currentTimeMillis() - time);
			System.out.println(errors);

			System.out.println("In/out throughput to servers = " + tp + "KB/s");
			System.out.println("In/out throughput to clients= " + tpc + "KB/s");
			System.out.println("Errores=" + (Integer)errors.intValue()*100/connections);
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public static HttpRequestImpl getLocalCssRequest() {
		final String a = "GET /zonaProp/css/bootstrap.css HTTP/1.1\r\n"
				+ "Host: localhost:8080\r\n"
				+ "Connection: keep-alive\r\n"
				+ "Cache-Control: no-cache\r\n"
				+ "Pragma: no-cache\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.53 Safari/536.5\r\n"
				+ "Accept: text/css,*/*;q=0.1\r\n"
				+ "Referer: http://localhost:8080/zonaProp/login\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n"
				+ "Cookie: JSESSIONID=01E58BE3F426329DEFEEBABA7BE67063; JSESSIONID=1dww5lw8g1kc9\r\n\r\n";

		final InputStream in1 = new InputStream() {
			int p;

			@Override
			public int read() throws IOException {
				if (this.p < a.length()) {
					return a.charAt(this.p++);
				}
				return -1;
			}
		};
		HttpRequestImpl req = null;
		try {
			req = new HttpRequestImpl(in1);
		} catch (final ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

	public static HttpRequestImpl getImageRequest1() {
		final String a = "GET /sociedad/intenso-invierno-ingreso-madrugada-DyN_CLAIMA20120604_0105_3.jpg HTTP/1.1\r\n"
				+ "Host: www.clarin.com\r\n"
				+ "Connection: keep-alive\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.53 Safari/536.5\r\n"
				+ "Accept: */*\r\n"
				+ "Referer: http://www.clarin.com/\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n"
				+ "Cookie: _pbtnf=visitas_visitantesole_deportes; __utma=23465064.1636318134.1333837018.1334676693.1338754638.3; __utmc=23465064; __utmz=23465064.1334676693.2.2.utmcsr=facebook.com|utmccn=(referral)|utmcmd=referral|utmcct=/l.php; _chartbeat2=yywm6tk8m3g51sq7.1338754641541.1338754873886.00000000000001\r\n\r\n";

		final InputStream in1 = new InputStream() {
			int p;

			@Override
			public int read() throws IOException {
				if (this.p < a.length()) {
					return a.charAt(this.p++);
				}
				return -1;
			}
		};
		HttpRequestImpl req = null;
		try {
			req = new HttpRequestImpl(in1);
		} catch (final ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

	public static HttpRequestImpl getImageRequest2() {
		final String a = "GET /img/hosted-wiroos-on.gif HTTP/1.1\r\n"
				+ "Host: o1.t26.net\r\n"
				+ "Connection: keep-alive\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.53 Safari/536.5\r\n"
				+ "Accept: */*\r\n" + "Referer: http://www.taringa.net/\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n\r\n";

		final InputStream in1 = new InputStream() {
			int p;

			@Override
			public int read() throws IOException {
				if (this.p < a.length()) {
					return a.charAt(this.p++);
				}
				return -1;
			}
		};
		HttpRequestImpl req = null;
		try {
			req = new HttpRequestImpl(in1);
		} catch (final ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

	public static HttpRequestImpl getFlashRequest() {
		final String a = "GET /esb/4/1/474c/2849c7366704ff5d.swf HTTP/1.1\r\n"
				+ "Host: ar-pri2.img.e-planning.net\r\n"
				+ "Connection: keep-alive\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.53 Safari/536.5\r\n"
				+ "Accept: */*\r\n" + "Accept-Encoding: gzip,deflate,sdch\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n\r\n";

		final InputStream in1 = new InputStream() {
			int p;

			@Override
			public int read() throws IOException {
				if (this.p < a.length()) {
					return a.charAt(this.p++);
				}
				return -1;
			}
		};
		HttpRequestImpl req = null;
		try {
			req = new HttpRequestImpl(in1);
		} catch (final ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

	public static HttpRequestImpl getFlashRequest2() {
		final String a = "GET /esb/4/21/46b5/c03ef2d680c98fb0.swf HTTP/1.1\r\n"
				+ "Host: us-wdc.img.e-planning.net\r\n"
				+ "Connection: keep-alive\r\n"
				+ "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.53 Safari/536.5\r\n"
				+ "Accept: */*\r\n" + "Referer: http://www.taringa.net/\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch\r\n"
				+ "Accept-Language: en-US,en;q=0.8\r\n"
				+ "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3\r\n\r\n";

		final InputStream in1 = new InputStream() {
			int p;

			@Override
			public int read() throws IOException {
				if (this.p < a.length()) {
					return a.charAt(this.p++);
				}
				return -1;
			}
		};
		HttpRequestImpl req = null;
		try {
			req = new HttpRequestImpl(in1);
		} catch (final ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final EncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return req;
	}

}
