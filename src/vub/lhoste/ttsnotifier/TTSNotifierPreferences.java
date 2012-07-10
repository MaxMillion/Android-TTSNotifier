package vub.lhoste.ttsnotifier;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;

public class TTSNotifierPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Context ctx = this;
		addPreferencesFromResource(R.xml.preferences);
		if (!TTSDispatcher.isTtsBetaInstalled(this)) {
			Toast.makeText(this, "Install 'Text-To-Speech Extended' from the Play store to improve TTS quality", Toast.LENGTH_LONG).show();
		}
		Intent svc = new Intent(this, TTSNotifierService.class);
		startService(svc);
		// Assign the buttons
		Preference customPref;
		customPref = (Preference) findPreference("btnTestTTS");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				TTSNotifierService.waitForSpeechInitialised();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				TTSNotifierService.setLanguage(prefs.getBoolean("cbxChangeLanguage", false), prefs.getString("txtLanguage", "English"));
				TTSNotifierService.speak(TTSNotifierService.myLanguage.getTxtTest(), false);
				return true;
			}
		});
		customPref = (Preference) findPreference("btnInstallTTSBeta");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				try {
					Uri u = Uri.parse("market://search?q=pname:com.google.tts");
					Intent i = new Intent(Intent.ACTION_VIEW, u);
					startActivityForResult(i, 0);
					System.exit(0);
				} catch (Exception e) {
					Toast.makeText(ctx, "Failed to launch the Play store. Please install the 'Text-To-Speech Extended' application.", Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TTSNotifierService.setLanguage(prefs.getBoolean("cbxChangeLanguage", false), prefs.getString("txtLanguage", "English"));
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		TTSNotifierService.setLanguage(prefs.getBoolean("cbxChangeLanguage", false), prefs.getString("txtLanguage", "English"));
	}
}