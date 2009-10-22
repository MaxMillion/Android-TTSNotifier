package vub.lhoste.ttsnotifier;

import com.google.tts.TextToSpeechBeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TTSNotifierStartServiceActivity extends Activity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		try {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeechBeta.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, TTSNotifierService.MY_TTS_DATA_CHECK_CODE);
			Intent svc = new Intent(this, TTSNotifierService.class);
			startService(svc);
		}
		catch (Exception e) { }
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

	@Override protected void onDestroy() {
		super.onDestroy();
		Intent svc = new Intent(this, TTSNotifierService.class);
		stopService(svc);
	}
}
