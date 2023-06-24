package ap.ex2.BookScrabbleServer;

import java.io.InputStream;
import java.io.OutputStream;

public interface SimpleClientHandler {
	void handleClient(InputStream inFromclient, OutputStream outToClient);
	void close();
}
