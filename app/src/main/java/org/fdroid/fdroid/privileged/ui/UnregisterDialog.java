package org.fdroid.fdroid.privileged.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.fdroid.fdroid.privileged.R;

public class UnregisterDialog extends Activity {
    private static final String TAG = "UnregisterDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String packageId = getPackageName();
        Log.d(TAG, "onCreate: " + packageId);
        if (Build.VERSION.SDK_INT < 21) {
            finish();
            return;
        }
        final DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm == null || !dpm.isDeviceOwnerApp(packageId)) {
            finish();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.removal_warning_title))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(String.format(getString(R.string.removal_warning), getString(R.string.app_name)))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            dpm.clearDeviceOwnerApp(packageId);
                            setResult(RESULT_OK);
                        } catch (
                                SecurityException e) {
                            Log.e(TAG, "Couldn't remove device owner!", e);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                })
                .show();
    }
}
