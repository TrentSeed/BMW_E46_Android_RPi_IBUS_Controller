package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import com.trentseed.bmw_rpi_ibus_controller.common.VoiceCommand;

/**
 * https://developer.android.com/training/wearables/apps/voice.html
 */
public class FragmentVoiceCommand extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 0;
    ImageView imgVoiceCommand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.voice, container, false);
        imgVoiceCommand = rootView.findViewById(R.id.imgBtnExit);
        imgVoiceCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        return rootView;
    }

    /**
     * Create an intent that can start the Speech Recognizer activity
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**
     * This callback is invoked when the Speech Recognizer returns.
     * This is where you process the intent and extract the speech text from the intent.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra( RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            String result = VoiceCommand.processSpokenText(spokenText);
            ((ActivityMain)this.getActivity()).showToast(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
