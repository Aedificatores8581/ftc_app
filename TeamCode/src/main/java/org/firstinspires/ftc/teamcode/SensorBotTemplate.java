package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by The Saminator on 07-22-2017.
 */
@Disabled
public abstract class SensorBotTemplate extends OpMode {
    static final int NAVX_I2C_PORT = 0;


    TouchSensor touch;
    DcMotor left, right;

    GyroSensor gyro;
    //ColorSensor cs;
    BNO055IMU imu;

    protected void setLeftPow(double pow) {
        left.setPower(pow);
    }

    protected void setRightPow(double pow) {
        right.setPower(pow);
    }

    public void init() {
        left = hardwareMap.dcMotor.get("lm");
        right = hardwareMap.dcMotor.get("rm");

        //cs = hardwareMap.colorSensor.get("cs");
        //gyro = hardwareMap.gyroSensor.get()

        left.setDirection(DcMotorSimple.Direction.REVERSE);
        right.setDirection(DcMotorSimple.Direction.FORWARD);
        //ods = hardwareMap.opticalDistanceSensor.get("ods");
        //touch = hardwareMap.touchSensor.get("ts");

        //navx = AHRS.getInstance(hardwareMap.deviceInterfaceModule.get(""), NAVX_I2C_PORT, AHRS.DeviceDataType.kQuatAndRawData);
    }

    @Override
    public void stop() {
        setLeftPow(0.0);
        setRightPow(0.0);

    }

}
