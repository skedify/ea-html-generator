package be.djairhogeuens.ea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class LibExtractor {

	private static final Logger LOGGER = Logger.getLogger(LibExtractor.class.getName());

	public String extractLibrary(String library, String targetFolder) {
		try {
			// Prepare target file
			String fileName = new File(library).toPath().getFileName().toString();
			File file = Paths.get(targetFolder, fileName).toFile();

			// Now, we can get the lib file from JAR and put it inside the temporary location
			InputStream lib = (getClass().getResourceAsStream(library));
			
			OutputStream target = new FileOutputStream(file, false);
			
			byte[] buf = new byte[8192];
		    int length;
		    while ((length = lib.read(buf)) > 0) {
		    	target.write(buf, 0, length);
		    }
			
		    target.close();

			LOGGER.fine("Created temporary file: " + file.getAbsoluteFile().toPath());
			
			return file.getAbsoluteFile().toPath().toString();
		} catch (IOException e) {
			// The same goes for exception - we are passing null back
			e.printStackTrace();
			return null;
		}
	}
}
