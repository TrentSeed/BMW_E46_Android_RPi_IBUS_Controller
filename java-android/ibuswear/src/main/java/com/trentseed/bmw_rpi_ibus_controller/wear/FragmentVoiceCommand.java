package com.trentseed.bmw_rpi_ibus_controller.wear;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.List;

/**
 * https://developer.android.com/training/wearables/apps/voice.html
 */
public class FragmentVoiceCommand extends Fragment {

    private static final int SPEECH_REQUEST_CODE = 0;
    ImageView imgVoiceCommand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.voice, container, false);
        imgVoiceCommand = (ImageView) rootView.findViewById(R.id.imgBtnExit);
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
            processSpokenText(results);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Process the spoken text input provided by the user.
     * TODO: Improve algorithm - this is an initial proof-of-concept!
     * @param results
     */
    private void processSpokenText(List<String> results){
        String spokenText = results.get(0);
        String userOutput = "Sorry, I did not catch that.";
        Log.d("BMW", "SPOKEN TEXT: " + spokenText);

        // determine action from spoken text
        if(spokenText.contains("debug")) {
            userOutput = spokenText.replace("debug ", "");
        }else if(spokenText.contains("all windows") || spokenText.contains("every window")){
            if(spokenText.contains("down")){
                IBUSWrapper.windowDriverFront(true);
                IBUSWrapper.windowDriverRear(true);
                IBUSWrapper.windowPassengerFront(true);
                IBUSWrapper.windowPassengerRear(true);
                userOutput = "Rolling down all windows!";
            }else{
                IBUSWrapper.windowDriverFront(false);
                IBUSWrapper.windowDriverRear(false);
                IBUSWrapper.windowPassengerFront(false);
                IBUSWrapper.windowPassengerRear(false);
                userOutput = "Rolling up all windows!";
            }
        }else if(spokenText.contains("window")){
            if(spokenText.contains("driver") || spokenText.contains("drivers")){
                if(spokenText.contains("rear") || spokenText.contains("back")){
                    if(spokenText.contains("down")){
                        IBUSWrapper.windowDriverRear(true);
                        userOutput = "Rolling down the rear driver window!";
                    }else{
                        IBUSWrapper.windowDriverRear(false);
                        userOutput = "Rolling up the rear driver window!";
                    }
                }else{
                    if(spokenText.contains("down")){
                        IBUSWrapper.windowDriverFront(true);
                        userOutput = "Rolling down the driver window!";
                    }else{
                        IBUSWrapper.windowDriverFront(false);
                        userOutput = "Rolling up the driver window!";
                    }
                }
            }else if(spokenText.contains("passenger") || spokenText.contains("passengers")){
                if(spokenText.contains("rear") || spokenText.contains("back")){
                    if(spokenText.contains("down")) {
                        IBUSWrapper.windowPassengerRear(true);
                        userOutput = "Rolling down the rear passenger window!";
                    }else{
                        IBUSWrapper.windowPassengerRear(false);
                        userOutput = "Rolling up the rear passenger window!";
                    }
                }else{
                    if(spokenText.contains("down")) {
                        IBUSWrapper.windowPassengerFront(true);
                        userOutput = "Rolling down the passenger window!";
                    }else{
                        IBUSWrapper.windowPassengerFront(false);
                        userOutput = "Rolling up the passenger window!";
                    }
                }
            }
        }else if(spokenText.contains("unlock")){
            IBUSWrapper.unlockCar();
            userOutput = "Unlocking the car!";
        }else if(spokenText.contains("lock")){
            IBUSWrapper.lockCar();
            userOutput = "Locking the car!";
        }else if(spokenText.contains("sunroof")){
            if(spokenText.contains("open")){
                IBUSWrapper.toggleSunroof(true);
                userOutput = "Opening the sunroof!";
            }else{
                IBUSWrapper.toggleSunroof(false);
                userOutput = "Closing the sunroof!";
            }
        }else if(spokenText.contains("volume") || spokenText.contains("turn it")){
            if(spokenText.contains("up")){
                if(spokenText.contains("way")){
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    IBUSWrapper.volumeUp();
                    userOutput = "Turning it way up!";
                }else {
                    IBUSWrapper.volumeUp();
                    userOutput = "Turning it up!";
                }
            }else if(spokenText.contains("down")){
                if (spokenText.contains("way")){
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    IBUSWrapper.volumeDown();
                    userOutput = "Turning it way down!";
                }else{
                    IBUSWrapper.volumeDown();
                    userOutput = "Turning it down!";
                }
            }
        }else if(spokenText.contains("seat")){
            if(spokenText.contains("back")){
                IBUSWrapper.moveDriverSeat(false);
                userOutput = "Moving the driver seat back!";
            }else if(spokenText.contains("forward")){
                IBUSWrapper.moveDriverSeat(true);
                userOutput = "Moving the driver seat forward!";
            }
        }else{
            userOutput = "Sorry, I didn't catch: " + spokenText;
        }

        // display response to user
        ((ActivityMain)this.getActivity()).showToast(userOutput);
    }

}
