package com.patrickmckinnon.homeaudio;

/**
 * Created by prm on 1/21/14.
 */
public class Source {
    public final String name;
    public final Input[] inputs;

    public Source(String name, Input ... inputs) {
        this.name = name;
        this.inputs = inputs;
    }
}
