package vub.lhoste.ttsnotifier;

import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.google.tts.TextToSpeechBeta;

public class TTSDispatcher {

	private volatile static boolean useOriginal = false;
	private volatile static TextToSpeech myTtsO = null;
	private volatile static TextToSpeech.OnInitListener ttsInitListenerO = null;
	private volatile static TextToSpeechBeta myTtsB = null;
	private volatile static TextToSpeechBeta.OnInitListener ttsInitListenerB = null;
	
	public TTSDispatcher(Context context) {
		if (!isTtsInstalled(context)) {
			Toast.makeText(context, "No TTS engine found! Install Text-To-Speech Extended from the Play store", Toast.LENGTH_LONG).show();
		}
		if (isTtsBetaInstalled(context)) {
			try {
				ttsInitListenerB = new TextToSpeechBeta.OnInitListener() {
						@Override
						public void onInit(int arg0, int arg1) {
							Log.v("TTSNotifierService", "TTS INIT DONE");
							TTSNotifierService.setLanguageTts(TTSNotifierService.myLanguage.getLocale());
							myTtsB.speak("", 0, null);
							TTSNotifierService.ttsReady = true;
						}
					};
					myTtsB = new TextToSpeechBeta(context, ttsInitListenerB);
					useOriginal = false;
			} catch (java.lang.ExceptionInInitializerError e) { e.printStackTrace(); }
		} else {
			try {
				ttsInitListenerO = new TextToSpeech.OnInitListener() {
						@Override
						public void onInit(int arg0) {
							Log.v("TTSNotifierService", "TTS INIT DONE");
							TTSNotifierService.setLanguageTts(TTSNotifierService.myLanguage.getLocale());
							TTSNotifierService.myTts.speak("", 0, null);
							TTSNotifierService.ttsReady = true;
						}
					};
				myTtsO = new TextToSpeech(context, ttsInitListenerO);
				useOriginal = true;
			} catch (java.lang.ExceptionInInitializerError e) { e.printStackTrace(); }
		}
	}
	


	public static boolean isTtsBetaInstalled(Context ctx) {
		try {
			ctx.createPackageContext("com.google.tts", 0);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	public static boolean isTtsInstalled(Context ctx) {
		try {
			ctx.createPackageContext("com.google.tts", 0);
		} catch (NameNotFoundException e1) {
			try {
				String classToLoad = "android.speech.tts.TextToSpeech";
			    Class<?> c = Class.forName(classToLoad, false, ctx.getClass().getClassLoader());
			    Class[] args = new Class[1];
		        } catch (Exception e) {
		        	return false;
				}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public void speak(String str, int i, HashMap<String, String> object) {
		if (useOriginal) {
			myTtsO.speak(str, i, object);
		} else {
			myTtsB.speak(str, i, object);
		}
	}
	public boolean isSpeaking() {
		if (useOriginal) {
			return myTtsO.isSpeaking();
		} else {
			return myTtsB.isSpeaking();
		}
	}
	public void setLanguage(Locale languageShortName) {
		if (useOriginal) {
			myTtsO.setLanguage(languageShortName);
		} else {
			myTtsB.setLanguage(languageShortName);
		}
	}
	public void stop() {
		if (useOriginal) {
			myTtsO.stop();
		} else {
			myTtsB.stop();
		}
	}
	public void shutdown() {
		if (useOriginal) {
			myTtsO.shutdown();
			myTtsO = null;
		} else {
			myTtsB.shutdown();
			myTtsB = null;
		}
	}

}
