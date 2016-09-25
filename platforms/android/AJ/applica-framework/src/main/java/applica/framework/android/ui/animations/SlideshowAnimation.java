package applica.framework.android.ui.animations;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import applica.framework.android.utils.Box;

/**
 * Created by bimbobruno on 01/02/16.
 */
public class SlideshowAnimation implements Animation {

    class NextTimerClass extends TimerTask {
        @Override
        public void run() {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int next = imageResources[index % imageResources.length];
                    back.value.setImageResource(next);
                    transition.execute(front, back);
                    index++;

                    Log.i("SlideshowAnimation", String.format("Current index: %d", index));
                }
            });
        }
    }

    private Activity context;
    private Box<ImageView> front;
    private Box<ImageView> back;
    private int[] imageResources;
    private long delay = 3000;
    private SlideshowTransition transition = new FadeSlideshowTransition();
    private Timer timer;
    private int index = 0;

    public SlideshowAnimation(Activity context, ImageView front, ImageView back) {
        this.context = context;
        this.front = new Box(front);
        this.back = new Box(back);
    }

    public Box<ImageView> getFront() {
        return front;
    }

    public void setFront(Box<ImageView> front) {
        this.front = front;
    }

    public Box<ImageView> getBack() {
        return back;
    }

    public void setBack(Box<ImageView> back) {
        this.back = back;
    }

    public int[] getImageResources() {
        return imageResources;
    }

    public void setImageResources(int[] imageResources) {
        this.imageResources = imageResources;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public SlideshowTransition getTransition() {
        return transition;
    }

    public void setTransition(SlideshowTransition transition) {
        this.transition = transition;
    }

    @Override
    public void init() {
        front.value.setImageResource(imageResources[0]);
        back.value.setImageBitmap(null);
        index = 1;
        transition.init(front, back);
    }

    @Override
    public void start() {
        if (timer != null) {
            stop();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new NextTimerClass(), delay, delay);
    }

    @Override
    public void stop() {
        timer.cancel();
        timer = null;
    }
}
