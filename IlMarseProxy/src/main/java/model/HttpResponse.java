package model;

import java.io.OutputStream;

public interface HttpResponse {

	// Dado un OutputStream tiene que escribir por el mismo su respuesta;
	// Osea response.writeStream(out) es escribi por el stream out tu respuesta;
	void writeStream(OutputStream out);

}
