package org.motechproject.ananya.framework;

public abstract class TimedRunner {

    private int tries;
    private int intervalSleep;

    public TimedRunner(int tries, int intervalSleep) {
        this.tries = tries;
        this.intervalSleep = intervalSleep;
    }

    protected abstract boolean run();

    public boolean executeWithTimeout() {
        boolean result = false;
        for (int i = 0; i < tries; i++) {
            result = run();
            if (result) return result;
            try {
                Thread.sleep(intervalSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException("Thread was interrupted.", e);
            }
        }
        return result;
    }

}

