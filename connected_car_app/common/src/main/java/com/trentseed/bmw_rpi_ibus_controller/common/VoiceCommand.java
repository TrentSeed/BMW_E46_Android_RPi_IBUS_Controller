package com.trentseed.bmw_rpi_ibus_controller.common;

import android.util.Log;

public class VoiceCommand {

    /** cache the last command **/
    private static String LAST_COMMAND = "";

    /**
     * Process the spoken text input provided by the user.
     * TODO: Improve algorithm - this is an initial proof-of-concept!
     * @param spokenText
     */
    public static String processSpokenText(String spokenText){
        String userOutput = "Sorry, I did not catch that.";
        Log.d("BMW", "SPOKEN TEXT: " + spokenText);

        // determine action from spoken text
        if(spokenText.contains("debug")) {
            userOutput = spokenText.replace("debug ", "");
        }else if(spokenText.contains("again") && !LAST_COMMAND.contains("again")){
            processSpokenText(LAST_COMMAND);
        }else if(spokenText.contains("all windows") || spokenText.contains("every window")
                || spokenText.contains("each window")){
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
        }else if(spokenText.contains("front windows")){
            if(spokenText.contains("down")){
                IBUSWrapper.windowDriverFront(true);
                IBUSWrapper.windowPassengerFront(true);
                userOutput = "Rolling down the front windows!";
            }else{
                IBUSWrapper.windowDriverFront(false);
                IBUSWrapper.windowPassengerFront(false);
                userOutput = "Rolling up the front windows!";
            }
        }else if(spokenText.contains("back windows") || spokenText.contains("rear windows")){
            if(spokenText.contains("down")){
                IBUSWrapper.windowDriverRear(true);
                IBUSWrapper.windowPassengerRear(true);
                userOutput = "Rolling down the rear windows!";
            }else{
                IBUSWrapper.windowDriverRear(false);
                IBUSWrapper.windowPassengerRear(false);
                userOutput = "Rolling up the rear windows!";
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
        }else if(spokenText.contains("lights")){
            if(spokenText.contains("interior")){
                if(spokenText.contains("off")){
                    IBUSWrapper.turnInteriorLightsOff();
                    userOutput = "Turning off interior lights!";
                }else if(spokenText.contains("on")){
                    IBUSWrapper.turnInteriorLightsOn();
                    userOutput = "Turning on interior lights!";
                }
            }else if(spokenText.contains("exterior")){

            }
        }else if(spokenText.contains("trunk")){
            if(spokenText.contains("open") || spokenText.contains("pop")){
                IBUSWrapper.openTrunk();
                userOutput = "Opening the trunk!";
            }
        }else if(spokenText.contains("unlock")){
            IBUSWrapper.unlockCar();
            userOutput = "Unlocking the car!";
        }else if(spokenText.contains("lock") || spokenText.contains("click click")){
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
        }else if(spokenText.contains("hazards")){
            IBUSWrapper.toggleHazards();
            userOutput = "Toggling the hazard lights!";
        }else if(spokenText.contains("seat")){
            if(spokenText.contains("upper")){
                if(spokenText.contains("back")){
                    IBUSWrapper.moveDriverSeatUpperPortion(false);
                    userOutput = "Moving the upper driver seat back!";
                }else if(spokenText.contains("forward")){
                    IBUSWrapper.moveDriverSeatUpperPortion(true);
                    userOutput = "Moving the upper driver seat forward!";
                }
            }else{
                if(spokenText.contains("back")){
                    IBUSWrapper.moveDriverSeat(false);
                    userOutput = "Moving the driver seat back!";
                }else if(spokenText.contains("forward")){
                    IBUSWrapper.moveDriverSeat(true);
                    userOutput = "Moving the driver seat forward!";
                }
            }
        }else{
            userOutput = "Sorry, I didn't catch: " + spokenText;
        }

        // cache last command
        LAST_COMMAND = spokenText;

        // return response to user
        return userOutput;
    }

}
