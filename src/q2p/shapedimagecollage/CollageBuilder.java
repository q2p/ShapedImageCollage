package q2p.shapedimagecollage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import q2p.shapedimagecollage.SeparatedThread.Factory;

final class CollageBuilder {
	static PartUnit[] parts;
	static short[][] colors;
	static PartUnit[] paint;
	
	static int inputWidth;
	static int inputHeight;
	
	static final Finder[] finders = new Finder[] {
		new RandomFinder(),
		new BestMatchFinder(),
		new ContrastFinder()
	};

	static Finder selectedFinder = null;
	
	static String saveFolderPath;
	
	static final void build(final File inputFile) {
		if(!new File(FilesProcessor.shapeFolder).exists() && !new File(FilesProcessor.shapeFolder).mkdirs()) {
			System.out.println("Error: Failed to initilize shape folder: \"" + FilesProcessor.shapeFolder + "\"");
			return;
		}

		if(!readInputImage(inputFile))
			return;

		saveFolderPath = FilesProcessor.outputFolder + inputFile.getName()+"/";
		try {
			FilesProcessor.initDirectory(saveFolderPath);
		} catch(final IOException e) {
			UserInterface.nonFatalIO("Failed to initilize folder for resulting files \""+saveFolderPath+"\".", e);
			return;
		}
		
		if(!readParts())
			return;

		if(!calculateMedians())
			return;
				
		paint = new PartUnit[colors[0].length];

		System.out.println("Begin painting.");
		
		selectedFinder.search();
		
		colors = null;
		
		// TODO: при return не обнуляются переменные.
		
		if(!Composer.compose())
			return;
		
		parts = null;
	}

	private static boolean calculateMedians() {
		System.out.println("Reading parts images and calculating medians.");
		return SeparatedThread.start(new MediansFactory(), parts.length);
	}
	
	private static final class MediansCalculator extends SeparatedThread {
		private static int inc;
		private final int beg;
		private final int end;
		public MediansCalculator(final int beg, final int end) {
			this.beg = beg;
			this.end = end;
		}
		public final void run() {
			BufferedImage img;
			for(int i = beg; i != end; i++) {
				try {
					img = ImageIO.read(new File(FilesProcessor.pixelsFolder+i+".png"));
				} catch (final Exception e) {
					System.out.println("Failed to read image: \""+FilesProcessor.pixelsFolder+i+".png\"");
					suspendOtherThreads();
					return;
				}
				if(img == null) {
					System.out.println("Failed to read image: \""+FilesProcessor.pixelsFolder+i+".png\"");
					suspendOtherThreads();
					return;
				}
				PartUnit.pixelSize = img.getWidth();
				parts[i].calculateMedian(img);
				if(Thread.interrupted())
					return;
				showProgress();
			}
		}
		private static synchronized void showProgress() {
			UserInterface.showProgress(++inc, parts.length, "parts");
		}
	}

	private static final class MediansFactory extends Factory {
		MediansCalculator fabric(final int position, final int beg, final int end) {
			MediansCalculator.inc = 0;
			return new MediansCalculator(beg, end);
		}
	}
	
	private static boolean readParts() {
		System.out.println("Reading parts file names.");
		FileInputStream fis = null;
		byte[] buff;
		try {
			fis = new FileInputStream(FilesProcessor.namesFile);
			buff = new byte[fis.available()];
			fis.read(buff);
		} catch(IOException e) {
			UserInterface.nonFatalIO("Failed to read file with names: \""+FilesProcessor.namesFile+"\".", e);
			return false;
		} finally {
			try {
				if(fis != null)
					fis.close();
			} catch(IOException e1) {}
			fis = null;
		}
		
		final String[] lines = new String(buff, StandardCharsets.UTF_8).split("\n");
		buff = null;
		parts = new PartUnit[lines.length];
		for(int i = parts.length-1; i != -1; i--)
			parts[i] = new PartUnit(lines[i], i);
		
		return true;
	}

	private static boolean readInputImage(final File inputFile) {
		System.out.println("Reading input image.");
		BufferedImage img;
		try {
			img = ImageIO.read(inputFile);
			if(img == null)
				throw new IOException();
		} catch (final OutOfMemoryError e) {
			System.out.println("Failed to read input shape image, because of low memory.");
			return false;
		} catch (final Exception e) {
			System.out.println("Failed to read input shape image.");
			return false;
		}
		
		inputWidth = img.getWidth();
		inputHeight = img.getHeight();

		System.out.println("Picking colors from input image.");
		colors = new short[3][inputWidth*inputHeight];
		Color c;
		int position;
		for(int x = inputWidth-1; x != -1; x--) {
			for(int y = inputHeight-1; y != -1; y--) {
				c = new Color(img.getRGB(x, y));
				position = getColor(x, y);
				colors[0][position] = (short)c.getRed();
				colors[1][position] = (short)c.getGreen();
				colors[2][position] = (short)c.getBlue();
			}
		}
		
		return true;
	}

	static final int getColor(final int x, final int y) {
		return y*inputWidth+x;
	}

	/* TODO: надо? private static final int getColor(final int width, final int x, final int y) {
		return y*width+x;
	}*/
}