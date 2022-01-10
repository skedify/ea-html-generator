package be.djairhogeuens.ea;

import org.sparx.Repository;

import com.sun.jna.Native;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.sparx.Package;

public class HTMLGenerator {

	private static final Logger LOGGER = Logger.getLogger(HTMLGenerator.class.getName());
	private static final List<String> requiredLibraries = new ArrayList<>();

	static {
		if (Native.POINTER_SIZE == 4) {
			requiredLibraries.add("/SSJavaCOM.dll");
		} else if (Native.POINTER_SIZE == 8) {
			requiredLibraries.add("/SSJavaCOM64.dll");
		}
	}

	public static void main(String[] args) {
		if (args.length == 1 && "--help".equals(args[0])) {
			showHelp();
			System.exit(0);
		}

		String cwd = System.getProperty("user.dir");
		String inputFileOrConnectionString = Paths.get(cwd, "default.eapx").toString();
		String inputUsername = null;
		String inputPassword = null;
		String pkg = "Default";
		String outputFolder = Paths.get(cwd, "html-report-output").toString();

		for (int i = 0; i < args.length; i += 2) {
			switch (args[i]) {
				case "--input":
					if (!isConnectionString(args[i + 1])) {
						validateInput(args[i + 1]);
						inputFileOrConnectionString = new File(args[i + 1]).getAbsolutePath().toString();
					} else {
						inputFileOrConnectionString = args[i + 1];
					}
					break;
				case "--username":
					inputUsername = args[i + 1];
					break;
				case "--password":
					inputPassword = args[i + 1];
					break;
				case "--package":
					pkg = args[i + 1];
					break;
				case "--output":
					validateAndCreateOutputFolder(args[i + 1]);
					outputFolder = new File(args[i + 1]).getAbsolutePath().toString();
					break;
				case "--help":
					throw new IllegalArgumentException(
							"The --help flag cannot be used in combination with other options");
				default:
					throw new IllegalArgumentException("Unknown option " + args[i]);
			}
		}

		validateInput(inputFileOrConnectionString);
		validateUsernameAndPassword(inputUsername, inputPassword);
		validateAndCreateOutputFolder(outputFolder);

		loadLibraries(cwd);

		Repository repo = new org.sparx.Repository();
		Package architecture = null;

		// Open repository
		LOGGER.info("Reading Enterprise Architect repository from: " + inputFileOrConnectionString);
		boolean isSuccess = false;
		try {
			if (inputUsername != null && inputPassword != null) {
				repo.SetSuppressSecurityDialog(true);
				isSuccess = repo.OpenFile2(inputFileOrConnectionString, inputUsername, inputPassword);
			} else {
				isSuccess = repo.OpenFile(inputFileOrConnectionString);
			}
		} catch (Exception e) {
			repo.CloseFile();
			repo.Exit();
			throw new RuntimeException(
					"Something went wrong while trying to read repository from " + inputFileOrConnectionString);
		}
		if (!isSuccess) {
			String error = repo.GetLastError();
			repo.CloseFile();
			repo.Exit();
			throw new RuntimeException(
					"Something went wrong while trying to read repository from " + inputFileOrConnectionString + ": "
							+ error);
		}

		// Browse through structure
		LOGGER.info("Using package: " + pkg);
		try {
			architecture = repo.GetModels().GetByName(pkg);
		} catch (Exception e) {
			repo.CloseFile();
			repo.Exit();
			throw new RuntimeException("Something went wrong while trying to open package: " + pkg + " is not found",
					e);
		}

		// Generate HTML documentation
		String guid = architecture.GetPackageGUID();
		LOGGER.fine("Package GUID: " + guid);
		String imageFormat = ".png";
		LOGGER.fine("Image format: " + imageFormat);
		String template = "<default>";
		LOGGER.fine("Template: " + template);
		String ext = ".html";
		LOGGER.fine("Extension: " + ext);

		LOGGER.info("Exporting to folder: " + outputFolder);
		repo.GetProjectInterface().RunHTMLReport(guid, outputFolder, imageFormat, template, ext);

		// Close file
		repo.CloseFile();
		repo.Exit();
		LOGGER.info("Exported successfully to folder: " + outputFolder);
	}

	private static void validateInputFile(String inputFile) {
		File file = new File(inputFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("The provided input file " + inputFile + " is not found");
		}
	}

	private static boolean isConnectionString(String inputFileOrConnectionString) {
		return inputFileOrConnectionString.contains(";Connect=Cloud");
	}

	private static void validateInput(String inputFileOrConnectionString) {
		if (!isConnectionString(inputFileOrConnectionString)) {
			validateInputFile(inputFileOrConnectionString);
		}
	}

	private static void validateUsernameAndPassword(String inputUsername, String inputPassword) {
		if ((inputUsername != null && inputPassword == null) || (inputUsername == null && inputPassword != null)) {
			throw new IllegalArgumentException("The provided username/password combination is invalid");
		}
	}

	private static void validateAndCreateOutputFolder(String outputFolder) {
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdirs();
		} else if (!folder.isDirectory()) {
			throw new IllegalArgumentException(
					"The provided output folder " + outputFolder + " already exists and is not a directory");
		}
	}

	private static void showHelp() {
		System.out.println(
				"Usage: java -jar ea-html-generator.jar [--input <input file path or connection string>] [--username <username>] [--password <password>] [--package <package name>] [--output <output folder path>]");
	}

	private static void loadLibraries(String targetFolder) {
		LibExtractor libExtractor = new LibExtractor();
		for (String library : requiredLibraries) {
			String extracted = libExtractor.extractLibrary(library, targetFolder);
			if (extracted == null) {
				LOGGER.warning("The library " + library + " could not be loaded");
			} else {
				System.load(extracted);
				LOGGER.info("Loaded library " + extracted);
			}
		}
	}
}
