package org.fdroid.fdroid.privileged.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.fdroid.DeviceAdminReceiver;
import org.fdroid.fdroid.privileged.R;


public class SettingsActivity extends Activity {
    private BroadcastReceiver receiver;
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingsactivity);

        TextView commandTextView = findViewById(R.id.command_text);
        commandTextView.setText(getString(R.string.device_owner_command,
                getApplicationContext().getPackageName(), DeviceAdminReceiver.class.getName()));
        final Button button = findViewById(R.id.button);

        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.removal_warning_title))
                .setMessage(String.format(getString(R.string.removal_warning), getString(R.string.app_name)))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            try {
                                String packageId = getPackageName();
                                DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                                if (dpm != null) {
                                    dpm.clearDeviceOwnerApp(packageId);
                                }
                            } catch (SecurityException e) {
                                Log.e(TAG, "Couldn't remove device owner!", e);
                            }
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).create();

        setButtonState(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.app.action.DEVICE_OWNER_CHANGED");


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: " + intent.toString());
                setButtonState(button);
            }
        };
        registerReceiver(receiver, filter);
    }

    private void setButtonState(Button button) {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            if (Build.VERSION.SDK_INT >= 21) {
                String packageId = getApplicationContext().getPackageName();
                if (dpm.isDeviceOwnerApp(packageId)) {
                    button.setEnabled(true);
                    button.setText(R.string.remove_device_owner);
                } else {
                    button.setEnabled(false);
                    button.setText(R.string.device_owner_not_set);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
