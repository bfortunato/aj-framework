package applica.framework.android.ui.animations;

import android.widget.ImageView;

import applica.framework.android.utils.Box;

/**
 * Created by bimbobruno on 01/02/16.
 */
public interface SlideshowTransition {

    void execute(Box<ImageView> front, Box<ImageView> back);
    void init(Box<ImageView> front, Box<ImageView> back);
}
