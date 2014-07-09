package com.patrickmckinnon.homeaudio;

import android.content.Context;

/**
 * Created by prm on 1/21/14.
 */
public class Speaker {
    public final Integer zone;
    public final String name;
    private int mMinDb;
    private int mMaxDb;
    public Receiver receiver;
    public boolean mEnabled;

    public Speaker(String name, Integer zone, int minDb, int maxDb) {
        mMinDb = minDb;
        mMaxDb = maxDb;
        this.zone = zone;
        this.name = name;
    }

    private int percentToDB(double volume){
        if(volume > 100.0)
            volume = 100.0;
        else if(volume < 0.0)
            volume = 0.0;

        int dB = (int) (mMinDb + (mMaxDb - mMinDb) * (volume / 100.0));
        dB = (dB / 10) * 10;

        Logger.TMP("db: " + dB);
        return dB;
    }

    public void setVolume(Context context, double volume) {
        Logger.TMP("set volume: " + volume + ": " + percentToDB(volume));
        receiver.setSpeakerVolume(context, this, percentToDB(volume));
    }

    public void mute(Context context, boolean enabled) {
        receiver.muteSpeaker(context, this, enabled);
    }
}