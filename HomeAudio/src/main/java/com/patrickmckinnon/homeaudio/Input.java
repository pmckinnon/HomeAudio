package com.patrickmckinnon.homeaudio;

import android.content.Context;

/**
 * Created by prm on 1/21/14.
 */
public class Input {
    public final String name;
    public Receiver receiver;

    public Input(String name) {
        this.name = name;
    }

    public void select(Context context) {
        receiver.setInput(context, this);
    }
}
