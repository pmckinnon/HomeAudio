package com.patrickmckinnon.homeaudio;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by prm on 1/21/14.
 */
public class YamahaApi {
    private static final Logger LOG = new Logger(YamahaApi.class);
    public class Command {
        private Boolean mPartyMode;
        private Boolean mMuteMain;
        private Boolean mMuteZone2;
        private Integer mMainDb;
        private Integer mZone2Db;
        private String mInput;

        public Command setInput(String input) {
            mInput = input;
            return this;
        }

        public Command partyMode(Boolean enabled) {
            mPartyMode = enabled;
            return this;
        }

        public Command muteMain(boolean enabled) {
            mMuteMain = enabled;
            return this;
        }

        public Command setMainVolume(int dB) {
            mMainDb = dB;
            return this;
        }

        public Command muteZone(int zone, boolean enabled) {
            if(zone == 0) {
                muteMain(enabled);
            }
            else if(zone == 1) {
               mMuteZone2 = enabled;
            }

            return this;
        }

        public Command setZoneVolume(int zone, int dB) {
            if(zone == 0) {
                setMainVolume(dB);
            }
            else if(zone == 1) {
                mZone2Db = dB;
            }

            return this;
        }

        private boolean includeSystem() {
           return mPartyMode != null;
        }

        private boolean includeMainVolume() {
            return mMuteMain != null || mMainDb != null;
        }

        private boolean includeMainZone() {
            return includeMainVolume() || mInput != null;
        }

        private boolean includeZone2Volume() {
            return mMuteZone2 != null || mZone2Db != null;
        }

        private boolean includeZone2() {
            return includeZone2Volume();
        }

        private String bool(boolean on){
            return on ? "On" : "Off";
        }

        private String mute(boolean value) {
           return "<Mute>" + (value ? "On" : "Off") + "</Mute>";
        }

        private String volume(Integer dB) {
            return mute(false) + "<Lvl><Val>" + dB + "</Val><Exp>1</Exp><Unit>dB</Unit></Lvl>";
        }

        private String buildXML() {
            String xml = "<YAMAHA_AV cmd=\"PUT\">";

            if(includeSystem()) {
                xml += "<System>";

                if(mPartyMode != null) {
                    xml += "<Party_Mode><Mode>" + bool(mPartyMode) + "</Mode></Party_Mode>";
                }

                xml += "</System>";
            }

            if(includeMainZone()) {
                xml += "<Main_Zone>";

                if(includeMainVolume()){
                    xml += "<Volume>";

                    if(mMuteMain != null) {
                        xml += mute(mMuteMain);
                    } else if(mMainDb != null) {
                        xml += volume(mMainDb);
                    }

                    xml += "</Volume>";
                }

                if(mInput != null) {
                    xml += "<Input><Input_Sel>" + mInput + "</Input_Sel></Input>";
                }

                xml += "</Main_Zone>";
            }

            if(includeZone2()) {
                xml += "<Zone_2>";

                if(includeZone2Volume()) {
                    xml += "<Volume>";

                    if(mMuteZone2 != null) {
                        xml += mute(mMuteZone2);
                    } else if(mZone2Db != null) {
                        xml += volume(mZone2Db);
                    }

                    xml += "</Volume>";
                }

                xml += "</Zone_2>";
            }

            xml += "</YAMAHA_AV>";

            //return "<YAMAHA_AV cmd=\"GET\"><Main_Zone><Basic_Status>GetParam</Basic_Status></Main_Zone></YAMAHA_AV>";
            return xml;
        }

        public void execute(Context context) {
            String xml = buildXML();
            LOG.v("XML: " + xml);
            context.startService(
                new Intent(context, YamahaApiService.class)
                    .putExtra(YamahaApiService.EXTRA_IP, mIp)
                    .putExtra(YamahaApiService.EXTRA_XML, xml)
            );
        }
    }
    String mIp;

    public YamahaApi(String ip) {
        mIp = ip;
    }

    public Command command() {
        return new Command();
    }
}
