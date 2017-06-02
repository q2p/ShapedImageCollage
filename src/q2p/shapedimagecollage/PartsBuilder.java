package q2p.shapedimagecollage;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;

// TODO: многопоточность
// TODO: разбить на функции

final class PartsBuilder {
	final static LinkedList<PartUnit> parts = new LinkedList<PartUnit>();
	
	static final void build() {
		FilesProcessor.loadFromDir(FilesProcessor.mainFolder+"all/");
		final String[] nws = new File(FilesProcessor.mainFolder+"nws/").list();
		for(final String nw : nws) {
			if(!nw.startsWith("nw") || nw.contains("-") || nw.contains("."))
				continue;
			
			try { Integer.parseInt(nw.substring(2)); }
			catch(final NumberFormatException e) { continue; }
			
			FilesProcessor.loadFromDir(FilesProcessor.mainFolder+"nws/"+nw+"/");
		}

		try {
			FilesProcessor.initDirectory(FilesProcessor.scaledFolder);
			FilesProcessor.initDirectory(FilesProcessor.pixelsFolder);
		} catch(final IOException e) {
			UserInterface.nonFatalIO("Failed to initilize folders for scaled and pixel images.", e);
			return;
		}
		
		final LinkedList<PartUnit> pool = new LinkedList<PartUnit>(parts);
		
		PartUnit part;
		BufferedImage inputImage;
		BufferedImage tempImage;
		final BufferedImage pixelImage = new BufferedImage(PartUnit.pixelSize, PartUnit.pixelSize, BufferedImage.TYPE_INT_RGB);
		final Graphics g = pixelImage.getGraphics();
		final int[] sizes = new int[2];
		int x, y;

		int lastProcessed = 0;
		final int total = parts.size();
		
		while(!pool.isEmpty()) {
			part = pool.removeFirst();
			try {
				try {
					inputImage = ImageIO.read(new File(part.path));
				} catch(final OutOfMemoryError e) {
					throw e;
				} catch (final Exception e) {
					System.out.println("Failed to read image from file: \""+part.path+"\". Skipping it.");
					e.printStackTrace();
					UserInterface.showProgress(++lastProcessed, total, "");
					continue;
				}
				if(inputImage == null) {
					System.out.println("Failed to read image from file: \""+part.path+"\". Skipping it.");
					UserInterface.showProgress(++lastProcessed, total, "");
					continue;
				}
		
				getScaledSizes(sizes, inputImage.getWidth(), inputImage.getHeight(), PartUnit.imageSize);
				
				tempImage = new BufferedImage(sizes[0], sizes[1], BufferedImage.TYPE_INT_ARGB);
				tempImage.getGraphics().drawImage(inputImage.getScaledInstance(sizes[0], sizes[1], Image.SCALE_SMOOTH), 0, 0, null);
				try {
					ImageIO.write(tempImage, "png", new File(FilesProcessor.scaledFolder+part.id+".png"));
				} catch (final IOException e) {
					UserInterface.nonFatalIO("Failed to write to file: \""+FilesProcessor.scaledFolder+part.id+".png\"", e);
					FilesProcessor.cleanUpPart(part.id);
					return;
				}
				sizes[0] = Math.min(inputImage.getWidth(), inputImage.getHeight());
				x = (inputImage.getWidth()-sizes[0])/2;
				y = (inputImage.getHeight()-sizes[0])/2;
				tempImage = new BufferedImage(sizes[0], sizes[0], BufferedImage.TYPE_INT_RGB);
				tempImage.getGraphics().drawImage(inputImage, 0, 0, sizes[0], sizes[0], x, y, x+sizes[0], y+sizes[0], null);
				inputImage = null;
				g.drawImage(tempImage.getScaledInstance(PartUnit.pixelSize, PartUnit.pixelSize, Image.SCALE_SMOOTH), 0, 0, null);
				tempImage = null;
				try {
					ImageIO.write(pixelImage, "png", new File(FilesProcessor.pixelsFolder+part.id+".png"));
				} catch (final IOException e) {
					UserInterface.nonFatalIO("Failed to write to file: \""+FilesProcessor.pixelsFolder+part.id+".png\"", e);
					FilesProcessor.cleanUpPart(part.id);
					return;
				}
				
				part.done = true;
		
				UserInterface.showProgress(++lastProcessed, total, "");
			} catch(final OutOfMemoryError e) {
				pool.addLast(part);
				System.out.println("Out of memory, while was trying to process \""+part.path+"\", trying to recover, sleep mode for 30 secounds.");
				
				System.gc();
				try {
					Thread.sleep(30*1000);
				} catch(final InterruptedException e1) {}
				FilesProcessor.cleanUpPart(part.id);
			}
		}
		
		System.out.println("Parts building is done.");

		try {
			FilesProcessor.savePartsNamesToFile();
		} catch(final IOException e) {
			UserInterface.nonFatalIO("Failed to write file names.", e);
		}
		
		parts.clear();
	}
	
	private static final void getScaledSizes(final int[] sizes, final int width, final int height, final int desired) {
		if(height > width) {
			sizes[1] = desired;
			sizes[0] = Math.max(1, (int)(
					(float)(desired*width) /
					(float)height
					));
		} else if(width > height) {
			sizes[0] = desired;
			sizes[1] = Math.max(1, (int)(
					(float)(desired*height) /
					(float)width
					));
		} else
			sizes[0] = sizes[1] = desired;
	}
}