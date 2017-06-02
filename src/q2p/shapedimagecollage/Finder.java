package q2p.shapedimagecollage;

import java.util.List;

abstract class Finder {
	final String searchFor;

	Finder(final String searchFor) {
		this.searchFor = searchFor;
	}

	static final PartUnit findBest(final List<PartUnit> avilable, final int r, final int g, final int b) {
		float d = distance(0, 0, 0, 255, 255, 255)+1;
		PartUnit p = null;
		
		float d2;
		
		for(final PartUnit q : avilable) {
			d2 = distance(r, g, b, q.median[0], q.median[1], q.median[2]);
			if(d2 < d) {
				d = d2;
				p = q;
			}
		}
		
		return p;
	}
	
	static final float distance(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
		final float dx = r1 - r2;
		final float dy = g1 - g2;
		final float dz = b1 - b2;
		
		return (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	abstract void search();
}