package github.gold_block.util;

public final class EventGuard {

    private EventGuard() {
    }

    public static void run(Runnable handler) {
        try {
            handler.run();
        } catch (Throwable throwable) {
            System.err.println("twilight_dusk: event handler failed");
            throwable.printStackTrace();
        }
    }
}
