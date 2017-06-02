package q2p.shapedimagecollage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class Composer {
	static final boolean compose() {
		System.out.println("Building image.");
		final BufferedImage img = new BufferedImage(PartUnit.pixelSize*CollageBuilder.inputWidth, PartUnit.pixelSize*CollageBuilder.inputHeight, BufferedImage.TYPE_INT_RGB);
		BufferedImage timg = null;
		for(int y = 0; y != CollageBuilder.inputHeight; y++) {
			for(int x = 0; x != CollageBuilder.inputWidth; x++) {
				try {
					timg = ImageIO.read(new File(FilesProcessor.pixelsFolder+CollageBuilder.paint[CollageBuilder.getColor(x, y)].id+".png"));
					if(timg == null)
						throw new Exception();
				} catch (final Exception e) {
					// TODO: abort("Error: Can't read image: \""+paint[getColor(x, y)]+".png\".\n"+e.getMessage());
					return false;
				}
				img.getGraphics().drawImage(timg, x*PartUnit.pixelSize, y*PartUnit.pixelSize, null);
				UserInterface.showProgress(CollageBuilder.getColor(x, y)+1, CollageBuilder.inputWidth*CollageBuilder.inputHeight, "");
			}
		}
		System.out.println("Writing image."); // TODO:
		try {
			ImageIO.write(img, "png", new File(CollageBuilder.saveFolderPath+"img.png"));
		} catch (final IOException e) {
			// TODO: abort("Error: Can't write to file \""+savePath+"img.png\".");
			return false;
		}
		// TODO: не закончено
		return true;
	}
}