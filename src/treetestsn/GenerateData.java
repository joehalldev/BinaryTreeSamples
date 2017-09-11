package treetestsn;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author Joe Hall
 * Create random sample data
 */
public class GenerateData {
	public static void main(String args[]) throws IOException {
		try(FileWriter fw = new FileWriter("main.txt");
			PrintWriter pw = new PrintWriter(fw)) {
			for(int i = 0; i < 2_000_000; i++) {
				UUID uuid = UUID.randomUUID();
				pw.println(uuid.toString() + "*" + i);
			}
		}
	}
}
