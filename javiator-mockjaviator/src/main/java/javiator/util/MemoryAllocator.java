/**
 * 
 */
package javiator.util;


/**
 * A class that allows to allocate memory for simulating regular memory
 * consumption. This can serve to activate the gc.
 * 
 * @author florian
 */
public class MemoryAllocator {

	/**
	 * debugging flag
	 */
	public static boolean DEBUG_ALLOCATETHREAD = false;

	/**
	 * the allocation rate in bytes/second
	 */
	private int allocationRate; 

	/**
	 * The size of each created object, in bytes
	 */
	private int allocateObjectSize = 48;

	/**
	 * Number of objects whose reference is kept in an array
	 */
	private int referenceObjectCount = 40000;

	/**
	 * The thread instance
	 */
	private AllocateThread thread;
	
	/**
	 * total number of allocated objects
	 */
	private int totalAllocatedObjects = 0;
	
	private int currentRetention = 0;

	public static MemoryAllocator staticAllocator;

	public static void setupMemoryAllocation(int allocationRate) {
		  staticAllocator = new MemoryAllocator(allocationRate);
		  staticAllocator.setEnabled(true);
		  
	}

	/**
	 * Create a disabled MemoryAllocator instance
	 */
	public MemoryAllocator(int allocationRate) {
		this.allocationRate = allocationRate;
	}

	/**
	 * trigger the thread's change() method
	 */
	private synchronized void change() {
		if (thread != null) {
			thread.change();
		}
	}

	/**
	 * Enable the memory allocator. If enable is true, the allocate thread is
	 * created and it will start allocating memory.
	 * 
	 * @param enable if true, start the allocator, otherwise stop it and remove
	 *            the thread
	 */
	public synchronized void setEnabled(boolean enable) {
		if (isEnabled() != enable) {
			if (enable) {
				thread = new AllocateThread();
			} else {
				thread.stopAllocator();
				thread = null;
				totalAllocatedObjects = 0;
				currentRetention = 0;
			}
		}
	}

	/**
	 * @return true if the allocator is running
	 */
	public synchronized boolean isEnabled() {
		return (thread != null);
	}
	
	/**
	 * @return the allocateObjectSize in bytes
	 */
	public int getAllocateObjectSize() {
		return allocateObjectSize;
	}

	/**
	 * @param allocateObjectSize the allocateObjectSize to set in bytes
	 */
	public void setAllocateObjectSize(int allocateObjectSize) {
		this.allocateObjectSize = allocateObjectSize;
		change();
	}

	/**
	 * @return the allocationRate in bytes/second
	 */
	public int getAllocationRate() {
		return allocationRate;
	}

	/**
	 * @param allocationRate the allocationRate to set in bytes/second
	 */
	public void setAllocationRate(int allocationRate) {
		this.allocationRate = allocationRate;
		change();
	}

	/**
	 * @return number of objects kept in an array
	 */
	public int getReferenceObjectCount() {
		return referenceObjectCount;
	}

	/**
	 * @param referenceObjectCount the referenceObjectCount to set
	 */
	public void setReferenceObjectCount(int referenceObjectCount) {
		this.referenceObjectCount = referenceObjectCount;
		change();
	}
	
	// status
	
	/**
	 * @return how many bytes currently held by the retention array
	 */
	public int getCurrentRetentionSize() {
		return currentRetention;
	}

	/**
	 * @return the total number of allocated objects
	 */
	public int getTotalAllocatedObjects() {
		return totalAllocatedObjects;
	}

	/**
	 * The actual allocator thread.
	 */
	private class AllocateThread extends Thread {

		/**
		 * The number of bytes allocated in total
		 */
		private long totalAllocated = 0;

		/**
		 * flag to notify the thread to stop
		 */
		private volatile boolean stopped = false;

		/**
		 * the array to hold a reference to the last allocated objects
		 */
		private Object[] allocateArray = null;

		/*
		 * the next write index in allocateArray
		 */
		private int arrayIndex = 0;

		/**
		 * The start time of allocating with one parameter set
		 */
		private long checkpointMillis;

		/**
		 * the number of bytes allocated since the checkpoint
		 */
		private long allocatedSinceCheckpoint;

		/**
		 * Create and start the maintenance thread
		 */
		public AllocateThread() {
			super("Allocator Thread");
			setDaemon(true);
			change();
			start();
		}

		/**
		 * Stops and wait for the end of the execution of this thread.
		 */
		public void stopAllocator() {
			synchronized (this) {
				stopped = true;
				this.notifyAll();
			}
			if (isAlive()) {
				try {
					this.join(2000);
				} catch (InterruptedException ie) {
				}
			}
		}
		
		private synchronized void recalcCurrentRetention() {
			currentRetention = 0;
			if (allocateArray != null) {
				for (int i = 0; i < allocateArray.length; i++) {
					Object o = allocateArray[i];
					if (o != null) {
						currentRetention += ((byte[]) o).length;
					}
				}
			}
		}

		/**
		 * Called to re-setup the internal runtime variables
		 */
		public synchronized void change() {
			if (allocateArray == null
					|| allocateArray.length != referenceObjectCount) {
				// create new array
				Object[] newArray = new Object[referenceObjectCount];
				if (allocateArray != null) {
					System.arraycopy(allocateArray, 0, newArray, 0, Math.min(
							referenceObjectCount, allocateArray.length));
				}
				allocateArray = newArray;
				recalcCurrentRetention();
			}
			// reset the checkpoint
			checkpointMillis = System.currentTimeMillis();
			allocatedSinceCheckpoint = 0;
		}

		/**
		 * allocate the specified number of bytes on the heap.
		 */
		private synchronized void allocate(int bytes) {
			Object newObject = new byte[bytes];
			if (arrayIndex >= allocateArray.length) {
				arrayIndex = 0;
			}
			totalAllocated += bytes;
			allocatedSinceCheckpoint += bytes;
			totalAllocatedObjects++;

			if (allocateArray != null && arrayIndex < allocateArray.length) {
				currentRetention += bytes;
				if (allocateArray[arrayIndex] != null) {
					currentRetention -= ((byte[]) allocateArray[arrayIndex]).length;
				}
				allocateArray[arrayIndex++] = newObject;
			}
		}

		/**
		 * in a loop, allocate the objects
		 */
		public void run() {
			while (!stopped) {
				try {
					synchronized (this) {
						long elapsedSinceCheckpoint = (System.currentTimeMillis())
								- checkpointMillis;
						long shouldHaveAllocated = (long) (((double) allocationRate) * (elapsedSinceCheckpoint / 1000.0));
						// number of bytes that should be allocated now is
						// the difference of shouldHaveAllocated and allocatedSinceCheckpoint
						// we only allocate in chunks of allocateObjectSize
						while ((shouldHaveAllocated - allocatedSinceCheckpoint) >= allocateObjectSize) {
							allocate(allocateObjectSize);
						}
						this.wait(10);
					}
				} catch (Throwable t) {
					System.err.println("Error in allocation thread: " + t);
					t.printStackTrace();
				}
			}
		}

	}
	
	public String toString() {
		return "Allocator: " + getFriendlyByteSize(getAllocationRate()) + 
			"/s with " + getFriendlyByteSize(getAllocateObjectSize()) +
			" objects; " + getFriendlyByteSize(getCurrentRetentionSize()) +
			" in use (maximum " + getFriendlyByteSize(getReferenceObjectCount() * getAllocateObjectSize()) + 
			")";
	}
	
	/**
	 * @return a string with the byte size with a suitable unit, bytes, KB, etc. 
	 */
	public static String getFriendlyByteSize(long bytes) {
		long threshold = 2 << 10; 
		if (bytes < 4*threshold) {
			return Long.toString(bytes)+" bytes";
		} 
		threshold = threshold << 10; 
		if (bytes < 4*threshold) {
			return format2(bytes / 1024.0)+" KB";
		}
		threshold = threshold << 10; 
		if (bytes < 4*threshold) {
			return format2(bytes / (1024.0*1024.0))+" MB";
		}
		threshold = threshold << 10; 
		if (bytes < 4*threshold) {
			return format2(bytes / (1024.0*1024.0*1024.0))+" GB";
		}
		return format2(bytes / (1024.0*1024.0*1024.0*1024.0))+" TB";
	}

	/**
	 * Format a floating point number with 2 decimals.
	 * 
	 * @param d the number to convert to string
	 * @return the string with the number d with 3 decimals.
	 */
	public static final String format2(double d) {
		return "" + (Math.rint(d * 100.0) / 100.0);
	}


}
