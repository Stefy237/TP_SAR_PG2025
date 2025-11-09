package info5.sar.async;

import java.util.concurrent.*;

public abstract class EventPump extends Thread {
    private BlockingQueue<Runnable> ready = new LinkedBlockingQueue<>();
    // private BlockingQueue<Runnable> delayed = new LinkedBlockingQueue<>();

    public void post(Runnable e) {
        ready.offer(e);
    }

    public void post(Runnable e, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                ready.offer(e);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Runnable e = ready.take();
                e.run();
            } catch (InterruptedException ignored) {}
        }
    }
}

