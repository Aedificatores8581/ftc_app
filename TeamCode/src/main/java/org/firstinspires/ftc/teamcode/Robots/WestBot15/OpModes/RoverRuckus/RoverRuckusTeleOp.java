package org.firstinspires.ftc.teamcode.Robots.WestBot15.OpModes.RoverRuckus;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Components.Mechanisms.Drivetrains.TankDrivetrains.TankDT;
import org.firstinspires.ftc.teamcode.Components.Mechanisms.RoverRuckus.AExtendotm;
import org.firstinspires.ftc.teamcode.Components.Sensors.TouchSensor;
import org.firstinspires.ftc.teamcode.Robots.WestBot15.WestBot15;
import org.firstinspires.ftc.teamcode.Universal.UniversalConstants;
import org.firstinspires.ftc.teamcode.Universal.UniversalFunctions;

@TeleOp (name = "Comp Tele Op")
public class RoverRuckusTeleOp extends WestBot15 {
    ExtensionState extensionState = ExtensionState.NON_RESETTING;
    double prevTime = 0;
    public void init(){
        isAutonomous = false;
        usingIMU = false;
        super.init();
        activateGamepad1();
        activateGamepad2();
    }
    public void start(){
        super.start();
        prevTime = UniversalFunctions.getTimeInSeconds();
    }
    public void loop(){
        drivetrain.maxSpeed = 0.7;
        updateGamepad1();
        updateGamepad2();
        drivetrain.turnMult = 1;
        if(!gamepad1.left_stick_button&&aextendo.getExtensionLength() > 10 ) {
            drivetrain.turnMult = (1.0 - 2.0/3.0 * (aextendo.getExtensionLength()-10) / (aextendo.MAX_EXTENSION_LENGTH-10));
        }
        drivetrain.leftPow = gamepad1.right_trigger - gamepad1.left_trigger - leftStick1.x * drivetrain.turnMult;
        drivetrain.rightPow = gamepad1.right_trigger - gamepad1.left_trigger + leftStick1.x * drivetrain.turnMult;
        drivetrain.setLeftPow();
        drivetrain.setRightPow();
        aextendo.aextendTM(rightStick1.y);
        lift.setPower(gamepad2.left_stick_y);
        /*
        if(gamepad1.a)
            extensionState = ExtensionState.RESETTING;
        switch (extensionState) {
            case NON_RESETTING:
                aextendo.aextendTM(rightStick1.y);
                if (gamepad1.left_bumper)
                    aextendo.articulateUp();
                else
                    aextendo.articulateDown();
                break;
            case RESETTING:
                aextendo.aextendTM(-1);
                aextendo.articulateUp();
                if (aextendo.isRetracted())
                    extensionState = ExtensionState.NON_RESETTING;
        }
        if(leftStick2.magnitude() > UniversalConstants.Triggered.STICK)
            lift2_0.lift(leftStick2.y);

        else if(gamepad2.dpad_up) {
            lift2_0.lift(1);
            mineralContainer.articulateUp();
        }
        else if(gamepad2.dpad_down) {
            lift2_0.lift(-1);
            mineralContainer.articulateDown();
        }
        if(gamepad1.left_trigger > UniversalConstants.Triggered.TRIGGER)
            intaek.dispense();
        if(gamepad1.right_trigger > UniversalConstants.Triggered.TRIGGER)
            mineralContainer.openCage();
        else
            mineralContainer.closeCage();*/
        telemetry.addData("extensionLength", aextendo.getExtensionLength());
        telemetry.addData("extension encoder val", aextendo.encoder.currentPosition);
        telemetry.addData("top", lift.topPressed());
        telemetry.addData("bottom", lift.bottomPressed());
        prevTime = UniversalFunctions.getTimeInSeconds();
    }
    enum ExtensionState{
        RESETTING,
        NON_RESETTING
    }
    enum state1{
        MIN,
        MAX
    }
    enum state2{
        DISP,
        THING1,
        THING2
    }
}
