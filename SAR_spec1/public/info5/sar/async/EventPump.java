package info5.sar.async;

import java.util.concurrent.*;

public abstract class EventPump extends Thread {
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    public void post(Runnable e) {
        queue.offer(e);
    }

    public void post(Runnable e, int delayMillis) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
                queue.offer(e);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable e = queue.take();
                e.run();
            } catch (InterruptedException ignored) {}
        }
    }
}

