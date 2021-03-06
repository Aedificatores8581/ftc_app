package org.firstinspires.ftc.teamcode.Components.Sensors;

import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created  by Frank Portman on 6/8/2018
 */
public class MagneticLimitSwitch {
    private DigitalChannel limitSwitch;

    //initializes the switch
    public void init(HardwareMap hardwareMap, String name) {
        limitSwitch = hardwareMap.digitalChannel.get(name);
        limitSwitch.setMode(DigitalChannel.Mode.INPUT);
    }

    //returns a boolean representing whether the switch is activated
    public boolean isActivated(){
        return limitSwitch.getState();
    }
    public String toString(){
        return isActivated() ? "active" : "inactive";
    }
}
