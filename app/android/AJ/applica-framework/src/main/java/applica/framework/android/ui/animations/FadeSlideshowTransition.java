package applica.framework.android.ui.animations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import applica.framework.android.utils.Box;

/**
 * Created by bimbobruno on 01/02/16.
 */
public class FadeSlideshowTransition implements SlideshowTransition {
    @Override
    public void execute(Box<ImageView> front, Box<ImageView> back) {
        ObjectAnimator frontAlpha = ObjectAnimator.ofFloat(front.value, View.ALPHA, 1);
        frontAlpha.setDuration(500);
        frontAlpha.setInterpolator(new LinearInterpolator());

        ObjectAnimator backAlpha = ObjectAnimator.ofFloat(back.value, View.ALPHA, 0);
        backAlpha.setDuration(500);
        backAlpha.setInterpolator(new LinearInterpolator());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(frontAlpha, backAlpha);
        set.start();

        Box.switchValues(front, back);
    }

    @Override
    public void init(Box<ImageView> front, Box<ImageView> back) {
        front.value.setAlpha(1);
        back.value.setAlpha(0);
    }
}
