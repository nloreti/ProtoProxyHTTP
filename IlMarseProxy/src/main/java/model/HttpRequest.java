package model;

import java.io.OutputStream;

public interface HttpRequest {

	void writeStream(OutputStream out);

	String getHost();

}
