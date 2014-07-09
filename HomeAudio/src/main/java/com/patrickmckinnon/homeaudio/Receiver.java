package com.patrickmckinnon.homeaudio;

import android.content.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by prm on 1/21/14.
 */
public class Receiver {
    private static final Logger LOG = new Logger(Receiver.class);
    public final String ip;
    public final String name;
    public final Input[] inputs;
    public final Speaker[] speakers;
    public Input mActiveInput;
    private YamahaApi mApi;

    public Receiver(String ip, String name, Input[] inputs, Speaker[] speakers) {
        this.ip = ip;
        this.name = name;
        this.inputs = inputs;
        this.speakers = speakers;

        for(Input input : inputs) {
            input.receiver = this;
        }

        for(Speaker speaker : speakers) {
            speaker.receiver = this;
        }
    }

    public YamahaApi api() {
        if(mApi == null) {
            mApi = new YamahaApi(ip);
        }
        return mApi;
    }

    public void mute(Context context) {
        LOG.v("mute");
        YamahaApi.Command cmd = api().command();

        for(int i = 0; i < speakers.length; ++i) {
            cmd.muteZone(i, true);
        }

        cmd.execute(context);
    }

    public void setInput(Context context, Input input) {
        LOG.v("setInput: " + input.name);
        YamahaApi.Command cmd = api().command();

        if(speakers.length > 1) {
            cmd.partyMode(true);
        }

        cmd.setInput(input.name);
        cmd.execute(context);
    }

    public void setSpeakerVolume(Context context, Speaker speaker, int dB) {
        LOG.v("enable: " + speaker.name);

        int index = Arrays.asList(speakers).indexOf(speaker);
        if(index >= 0) {
            api().command().setZoneVolume(index, dB).execute(context);
        }
    }

    public void muteSpeaker(Context context, Speaker speaker, boolean enabled) {
        LOG.v((enabled ? "mute" : "unmute") + ": " + speaker.name);

        int index = Arrays.asList(speakers).indexOf(speaker);
        if(index >= 0) {
            api().command().muteZone(index, enabled).execute(context);
        }
    }
}
