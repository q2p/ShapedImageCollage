package q2p.shapedimagecollage;

import java.awt.Color;
import java.awt.image.BufferedImage;

public final class PartUnit {
	static int pixelSize;
	static int imageSize;
	
	public final String path;
	public final int id;
	public short[] median = null;

	public boolean done = false;
	
	public PartUnit(final String path, final int id) {
		this.path = path;
		this.id = id;
	}
	
	public final void calculateMedian(final BufferedImage img) {
		long r = 0;
		long g = 0;
		long b = 0;
		Color c;
		for(int y = img.getHeight()-1; y != -1; y--) {
			for(int x = img.getWidth()-1; x != -1; x--) {
				c = new Color(img.getRGB(x, y));
				r += c.getRed();
				g += c.getGreen();
				b += c.getBlue();
			}
		}
		median = new short[] {
			(short)(r/(img.getWidth()*img.getHeight())),
			(short)(g/(img.getWidth()*img.getHeight())),
			(short)(b/(img.getWidth()*img.getHeight()))
		};
	}
}