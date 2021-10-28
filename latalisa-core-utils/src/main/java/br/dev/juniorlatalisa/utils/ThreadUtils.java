package br.dev.juniorlatalisa.utils;

import java.util.concurrent.ThreadFactory;

public class ThreadUtils {

	public static Thread createThread(Runnable target, String name, int newPriority) {
		Thread retorno = new Thread(target, name);
		retorno.setPriority(newPriority);
		return retorno;
	}

	public static ThreadFactory createThreadFactory(String name, int newPriority) {
		return (target) -> createThread(target, name, newPriority);
	}

	public static boolean execute(Runnable command, String name, int newPriority, long timeout) {
		boolean[] sync = { false };
		Thread thread = createThread(() -> execute(command, sync), name, newPriority);
		thread.start();
		synchronized (sync) {
			try {
				sync.wait(timeout);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		return sync[0];
	}

	protected static void execute(Runnable command, boolean[] sync) {
		command.run();
		synchronized (sync) {
			sync[0] = true;
			sync.notifyAll();
		}
	}

}