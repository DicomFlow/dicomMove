package br.ufpb.dicomflow.tests;

public class RunnableDemo {
	public static void main(String args[]) {
		System.out.println("Main thread starting.");
		MyThread mt = new MyThread();
		Thread newThrd = new Thread(mt);
		newThrd.start();
		do {
			System.out.println("In main thread.");
			try {
				Thread.sleep(250);
			} catch (InterruptedException exc) {
				System.out.println("Main thread interrupted.");
			}
		} while (mt.count != 5);

		System.out.println("Main thread ending.");
	}
}