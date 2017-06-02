package q2p.shapedimagecollage;

import java.util.LinkedList;
import java.util.Random;

final class RandomFinder extends Finder {
	RandomFinder() {
		super("random");
	}
	
	final void search() {
		final LinkedList<PartUnit> avilable = new LinkedList<PartUnit>();
		for(final PartUnit partUnit : CollageBuilder.parts)
			avilable.addLast(partUnit);
		
		final boolean[] painted = new boolean[CollageBuilder.paint.length];
		for(int i = 0; i != painted.length; i++)
			painted[i] = false;

		final Random random = new Random();
		final LinkedList<PartUnit> used = new LinkedList<PartUnit>();

		for(int left = CollageBuilder.paint.length; left != 0; left--) {
			int position = random.nextInt(left);
			
			for(int i = 0; i != CollageBuilder.paint.length; i++) {
				if(!painted[i]) {
					if(position-- == 0) {
						painted[i] = true;
						final PartUnit t = findBest(avilable, CollageBuilder.colors[0][i], CollageBuilder.colors[1][i], CollageBuilder.colors[2][i]);
						CollageBuilder.paint[i] = t;
						avilable.remove(t);
						used.addLast(t);
						if(avilable.isEmpty()) {
							avilable.addAll(used);
							used.clear();
						}
						break;
					}
				}
			}
			UserInterface.showProgress(CollageBuilder.paint.length-left+1, CollageBuilder.paint.length, "pixels");
		}
	}
}