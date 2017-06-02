package q2p.shapedimagecollage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

final class UserInterface {
	private static final int MAX_IMAGE_SIZE = 1024;
	private static final int MAX_PIXEL_SIZE = 128;

	private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
	public static final String readLine() {
		try {
			return READER.readLine();
		} catch(final IOException e) {
			UserInterface.fatalIO("Failed to read from console.", e);
			return null;
		}
	}
	public static final int readNumber(final int maxNumber, final String onWrongInput) {
		while(true) {
			try {
				int option = Integer.parseInt(readLine());
				if(option > 0 && option <= maxNumber)
					return option;
			} catch(NumberFormatException e) {}
			System.out.println(onWrongInput);
		}
	}

	static final void entry() {
		System.out.println("Shaped Collage Builder.");
		while(true) {
			System.out.println("Type in operation:\n1) Build parts.\n2) Build collage from parts.");
			final int operation = readNumber(2, "Invalid operation.");
			if(operation == 1) {
				inputForParts();
			} else {
				inputForCollage();
			}
		}
	}
	
	private static void inputForCollage() {
		System.out.println("Type in .png file name inside \""+FilesProcessor.shapeFolder+"\" folder, from wich shape will be readed.");
		File inputFile = null;
		while(true) {
			inputFile = new File(FilesProcessor.shapeFolder+readLine());
			
			if(!FilesProcessor.endsWith(inputFile.getName(), ".png")) {
				System.out.println("File must be .png format.");
			} else if(!inputFile.exists()) {
				System.out.println("File do not exist.");
			} else if(!inputFile.isFile()) {
				System.out.println("File is not a file.");
			} else {
				break;
			}
		}

		System.out.println("Select search method:");
		for(int i = 0; i != CollageBuilder.finders.length; i++)
			System.out.println((i+1)+") Search for " + CollageBuilder.finders[i].searchFor);
		
		CollageBuilder.selectedFinder = CollageBuilder.finders[readNumber(CollageBuilder.finders.length-1, "Invalid search method.")-1];
		
		CollageBuilder.build(inputFile);
	}

	private static void inputForParts() {
		System.out.println("Type in pixel size, from 1, to "+MAX_PIXEL_SIZE+".");
		PartUnit.pixelSize = readNumber(MAX_PIXEL_SIZE, "Invalid pixel size.");

		System.out.println("Type in image size, from 1, to "+MAX_IMAGE_SIZE+".");
		PartUnit.imageSize = readNumber(MAX_IMAGE_SIZE, "Invalid image size.");
		
		PartsBuilder.build();
	}

	static final void showProgress(final int done, final int total, final String comment) {
		final int perc = (100*done/total);
		if(perc != (100*(done-1)/total))
			System.out.println(perc + "%, "+done + "/" + total + " " + comment);
	}

	static final void abort(final String reason) {
		System.out.println(reason);
		System.exit(1);
	}
	static void fatalIO(final String comment, final IOException exception) {
		nonFatalIO(comment, exception);
		System.exit(1);
	}
	static void nonFatalIO(final String comment, final IOException exception) {
		System.out.println("Failed to I/O:\n"+comment+"\n"+exception.getMessage());
	}
}