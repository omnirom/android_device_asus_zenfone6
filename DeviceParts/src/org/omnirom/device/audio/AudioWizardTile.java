package org.omnirom.device.audio;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import org.omnirom.device.R;

public class AudioWizardTile extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();

        getQsTile().setIcon(Icon.createWithResource(this, R.drawable.ic_audio_wizard));
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setPackage("com.asus.maxxaudio.audiowizard");
        sendBroadcast(intent);
    }
}