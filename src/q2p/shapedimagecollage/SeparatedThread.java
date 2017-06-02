package q2p.shapedimagecollage;

abstract class SeparatedThread implements Runnable {
	private static final int availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors());
	private final Thread waitingThread = Thread.currentThread();
	protected final void suspendOtherThreads() {
		waitingThread.interrupt();
	}
	
	static abstract class Factory {
		abstract SeparatedThread fabric(final int position, final int beg, final int length);
	}
	
	static final boolean start(final Factory factory, int amount) {
		final int[] sectors = separateAmountByProcessors(amount);
		
		final Thread[] threads = new Thread[sectors.length];
		
		for(int i = 0, j = 0; i != sectors.length; j += sectors[i++])
			threads[i] = new Thread(factory.fabric(i, j, j+sectors[i]));
		
		try {
			for(final Thread thread : threads)
				thread.start();
			for(final Thread thread : threads)
				thread.join();
			return true;
		} catch(final InterruptedException e) {
			for(final Thread thread : threads) {
				synchronized(thread) {
					if(!thread.isInterrupted())
						thread.interrupt();
				}
			}
			return false;
		}
	}

	private static final int[] separateAmountByProcessors(int amount) {
		final int chunk = amount/availableProcessors;
		final int[] ret = new int[chunk == 0 ? amount : availableProcessors];
		
		amount -= chunk*ret.length;
		for(int i = 0; i != ret.length; i++) {
			ret[i] = chunk;
			if(amount != 0) {
				ret[i]++;
				amount--;
			}
		}
		return ret;
	}
}