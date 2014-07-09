package com.patrickmckinnon.homeaudio;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private static final Logger LOG = new Logger(MainActivity.class);

    private static final Input AV1 = new Input("AV1");
    private static final Input AV2 = new Input("AV2");
    private static final Input COAXIAL1 = new Input("COAXIAL1");

    private static final Speaker MASTER = new Speaker("Master", 1, -500, -100);
    private static final Speaker BATHROOM = new Speaker("Bathroom", 2, -500, -100);
    private static final Speaker OFFICE = new Speaker("Office", 1, -500, -150);

    private static final Receiver RX_A1000 = new Receiver(
        "192.168.1.79", "RX-A1000",
        new Input[] { AV1, AV2 },
        new Speaker[] { MASTER, BATHROOM }
    );
    private static final Receiver R_N500 = new Receiver(
        "192.168.1.77", "R-N500",
        new Input[] { COAXIAL1 },
        new Speaker[] { OFFICE }
    );

    private static final Source CHROMECAST = new Source("Chromecast", AV1);
    private static final Source PC = new Source("PC", AV2, COAXIAL1);

    private static final Source[] SOURCES = new Source[] { CHROMECAST, PC };
    private static final Speaker[] SPEAKERS = new Speaker[] { MASTER, BATHROOM, OFFICE };
    private static final Receiver[] RECEIVERS = new Receiver[] { RX_A1000, R_N500 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final int DEFAULT_VOLUME = 30;
        private ToggleButton[] mSpeakerToggleButtons;
        private SeekBar mSeekBar;

        private Map<Source, Speaker[]> mSpeakerMap = new HashMap<Source, Speaker[]>();
        private Map<Source, Receiver[]> mReceiverMap = new HashMap<Source, Receiver[]>();

        public PlaceholderFragment() {
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if(mSpeakerToggleButtons != null) {
                for(int i = 0; i < mSpeakerToggleButtons.length; ++i) {
                    outState.putBoolean("speaker" + i, mSpeakerToggleButtons[i].isChecked());
                }
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mSeekBar = (SeekBar) rootView.findViewById(R.id.volume);
            if(savedInstanceState == null) {
                mSeekBar.setProgress(DEFAULT_VOLUME);
            }
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    onVolumeChanged();
                }
            });

            mSpeakerToggleButtons = new ToggleButton[SPEAKERS.length];
            LinearLayout speakersView = (LinearLayout) rootView.findViewById(R.id.speakers);
            for(int i = 0; i < SPEAKERS.length; ++i) {
                ToggleButton toggle = new ToggleButton(getActivity());
                final Speaker speaker = SPEAKERS[i];
                mSpeakerToggleButtons[i] = toggle;
                toggle.setTextOff(speaker.name);
                toggle.setTextOn(speaker.name);
                toggle.setChecked(savedInstanceState != null && savedInstanceState.getBoolean("speaker" + i, false));
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        onSpeakerEnabled(speaker, b);
                    }
                });
                speakersView.addView(toggle);
            }

            String[] sourceNames = new String[SOURCES.length];
            for(int i = 0; i < SOURCES.length; ++i) {
                sourceNames[i] = SOURCES[i].name;
            }

            ArrayAdapter<String> sourcesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sourceNames);
            Spinner sourcesSpinner = (Spinner) rootView.findViewById(R.id.sources);
            sourcesSpinner.setAdapter(sourcesAdapter);
            sourcesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    onSourceSelected(SOURCES[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }
            });

            return rootView;
        }

        private boolean isSpeakerEnabled(int speakerIndex) {
            return  mSpeakerToggleButtons != null &&
                    speakerIndex < mSpeakerToggleButtons.length &&
                    mSpeakerToggleButtons[speakerIndex].getVisibility() == View.VISIBLE &&
                    mSpeakerToggleButtons[speakerIndex].isChecked()
                    ;
        }

        private void onVolumeChanged() {
            if(mSeekBar != null) {
                for(int i = 0; i < SPEAKERS.length; ++i) {
                    Speaker speaker = SPEAKERS[i];
                    if(isSpeakerEnabled(i)) {
                        speaker.setVolume(getActivity(), (double) mSeekBar.getProgress());
                    }
                    else {
                        speaker.mute(getActivity(), true);
                    }
                }
            }
        }

        private void onSourceSelected(Source source) {
            LOG.v("onSourceSelected: " + source.name );
            for(Input input : source.inputs) {
                input.select(getActivity());
            }

            // Mute unused receivers
            for(Receiver receiver : RECEIVERS) {
                if(!Arrays.asList(getReceivers(source)).contains(receiver)) {
                    receiver.mute(getActivity());
                }
            }

            if(mSpeakerToggleButtons != null) {
                List<Speaker> activeSpeakers = Arrays.asList(getSpeakers(source));
                for(int i = 0; i < SPEAKERS.length; ++i) {
                    Speaker speaker = SPEAKERS[i];
                    if(activeSpeakers.contains(speaker)) {
                        mSpeakerToggleButtons[i].setVisibility(View.VISIBLE);
                    }
                    else {
                        mSpeakerToggleButtons[i].setVisibility(View.GONE);
                    }
                }
            }

            onVolumeChanged();
        }

        private void onSpeakerEnabled(Speaker speaker, boolean enabled) {
            LOG.v("onSpeakerEnabled(" + speaker.name + "): " + enabled);
            if(enabled) {
                speaker.setVolume(getActivity(), mSeekBar.getProgress());
            }
            else {
                speaker.mute(getActivity(), true);
            }
        }

        private Receiver[] getReceivers(Source source) {
            Receiver[] receivers = mReceiverMap.get(source);
            if(receivers == null) {
                Set<Receiver> receiverSet = new HashSet<Receiver>();
                for(Input input : source.inputs) {
                    receiverSet.add(input.receiver);
                }
                receivers = receiverSet.toArray(new Receiver[receiverSet.size()]);
                mReceiverMap.put(source, receivers);
            }

            return receivers;
        }
        private Speaker[] getSpeakers(Source source) {
            Speaker[] speakers = mSpeakerMap.get(source);
            if(speakers == null) {
                List<Speaker> speakerList = new ArrayList<Speaker>();
                for(Receiver receiver : getReceivers(source)) {
                    for(Speaker speaker : receiver.speakers) {
                        speakerList.add(speaker);
                    }
                }
                speakers = speakerList.toArray(new Speaker[speakerList.size()]);
                mSpeakerMap.put(source, speakers);
            }

            return speakers;
        }
    }

}
