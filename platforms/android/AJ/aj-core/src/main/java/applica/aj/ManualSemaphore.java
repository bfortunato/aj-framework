package applica.aj;

/**
 * Created by bimbobruno on 10/03/16.
 */
public class ManualSemaphore extends Semaphore {

    private boolean isGreen = false;

    public ManualSemaphore() {
        super(null);

        runAction(new Runnable() {
            @Override
            public void run() {
                int timeInterval = 10;
                int elapsedTime = 0;
                while (!isGreen) {
                    try {
                        Thread.sleep(timeInterval);
                        elapsedTime += timeInterval;
                    } catch (InterruptedException e) {}
                }
            }
        });
    }

    public void green() {
        isGreen = true;
    }
}
