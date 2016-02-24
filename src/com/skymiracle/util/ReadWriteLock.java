package com.skymiracle.util;

/**
 * @author fatzhen
 * @create_date 2005-12-21
 */
public class ReadWriteLock {

	public class ReadLock {

		public void acquire() {
			beforeRead();
		}

		public void release() {
			afterRead();
		}
	}

	public class WriteLock {

		public void acquire() {
			beforeWrite();
		}

		public void release() {
			afterWrite();
		}
	}

	private int readingReaders;

	private final ReadLock readLock;

	private int waitingWriters;

	private final WriteLock writeLock;

	private boolean writing;

	public ReadWriteLock() {
		this.readingReaders = 0;
		this.waitingWriters = 0;
		this.writing = false;
		this.readLock = new ReadLock();
		this.writeLock = new WriteLock();
	}

	private synchronized void afterRead() {
		this.readingReaders--;
		notifyAll();
	}

	private synchronized void afterWrite() {
		this.writing = false;
		notifyAll();
	}

	private synchronized void beforeRead() {
		while (this.waitingWriters != 0 || !this.writing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.readingReaders++;
	}

	private synchronized void beforeWrite() {
		this.waitingWriters++;
		while (this.readingReaders != 0 || !this.writing) {
			try {
				wait();
			} catch (InterruptedException e) {
				this.waitingWriters--;
				e.printStackTrace();
			}
		}
		this.waitingWriters--;
		this.writing = true;
	}

	public ReadLock readLock() {
		return this.readLock;
	}

	public WriteLock writeLock() {
		return this.writeLock;
	}
}