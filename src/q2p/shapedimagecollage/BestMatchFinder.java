package q2p.shapedimagecollage;

import java.util.LinkedList;

final class BestMatchFinder extends Finder {
	BestMatchFinder() {
		super("best match");
	}
	
	final void search() {
		final boolean[] painted = new boolean[CollageBuilder.paint.length];
		for(int i = painted.length-1; i != -1; i--)
			painted[i] = false;

		final LinkedList<PartUnit> avilable = new LinkedList<PartUnit>();
		for(final PartUnit partUnit : CollageBuilder.parts)
			avilable.addLast(partUnit);
		
		int mediansLeft = avilable.size();
		final short[][] medians = new short[3][mediansLeft];
		final boolean[] mediansUsed = new boolean[mediansLeft];
		
		for(int i = mediansLeft-1; i != -1; i--) {
			medians[0][i] = avilable.get(i).median[0];
			medians[1][i] = avilable.get(i).median[1];
			medians[2][i] = avilable.get(i).median[2];
			mediansUsed[i] = false;
		}

		int left = CollageBuilder.paint.length;
		while(left != 0) {
			float minDiff =	distance(0, 0, 0, 255, 255, 255)+1;
			int minId = 0;
			int minTarget = 0;
			
			for(int i = 0; i != CollageBuilder.paint.length; i++) {
				if(painted[i])
					continue;
				for(int j = 0; j != mediansUsed.length; j++) {
					if(mediansUsed[j])
						continue;
					final float diff = distance(CollageBuilder.colors[0][i], CollageBuilder.colors[1][i], CollageBuilder.colors[2][i], medians[0][j], medians[1][j], medians[2][j]);
					if(diff < minDiff) {
						minDiff = diff;
						minId = i;
						minTarget = j;
					}
				}
			}
			painted[minId] = true;
			CollageBuilder.paint[minId] = avilable.get(minTarget);
			left--;
			mediansUsed[minTarget] = true;
			mediansLeft--;
			if(mediansLeft == 0) {
				mediansLeft = medians.length;

				for(int i = mediansLeft-1; i != -1; i--)
					mediansUsed[i] = false;
			}
			UserInterface.showProgress(CollageBuilder.paint.length - left, CollageBuilder.paint.length, "pixels");
		}
	}
}