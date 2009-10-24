package vub.lhoste.ttsnotifier;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import com.google.tts.TextToSpeechBeta;

public class TTSNotifierPreferences extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeechBeta.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, TTSNotifierService.MY_TTS_DATA_CHECK_CODE);
		Intent svc = new Intent(this, TTSNotifierService.class);
		startService(svc);
		//Get the custom preference
		Preference customPref = (Preference) findPreference("btnTestTTS");
		customPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				TTSNotifierService.waitForSpeechInitialised();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				TTSNotifierService.setLanguage(prefs.getBoolean("cbxChangeLanguage", false), prefs.getString("txtLanguage", "English"));
				TTSNotifierService.speak(TTSNotifierService.myLanguage.getTxtTest(), false);
				return true;
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TTSNotifierService.MY_TTS_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeechBeta.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				TTSNotifierService.myTts = new TextToSpeechBeta(this, TTSNotifierService.ttsInitListener);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeechBeta.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
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
