package org.firstinspires.ftc.teamcode.Universal.Testers;

import android.os.Environment;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Robots.SensorBot.SensorBot;
import org.firstinspires.ftc.teamcode.Universal.TelemetryLogger;

import java.io.IOException;

/**
 * To variables (x and y) increase and different rates, and TelemetryLogger
 * writes that to a log file.
 * */

@Disabled
@Autonomous(name = "Telemetry Logger Tester: Non-Threaded", group = "Tele Test")
public class TelemetryLoggerTester extends OpMode {

    private TelemetryLogger logger;
    private int x, y;
    long baseTimeMillis; // Current System time when the start method finishes running


    @Override
    public void init () {
        telemetry.addLine(Environment.getExternalStorageDirectory().getName());
        x = 0;
        y = 0;

        try {
            logger = new TelemetryLogger();
            logger.writeToLogInCSV("Millis", "X", "Y");
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
        }
    }

    @Override
    public void start() {
        baseTimeMillis = System.currentTimeMillis();
    }

    @Override
    public void loop() {
        telemetry.addData( "Milliseconds", System.currentTimeMillis() - baseTimeMillis);
        telemetry.addData("X", x);
        telemetry.addData("Y", y);
        try {
            logger.writeToLogInCSV(System.currentTimeMillis() - baseTimeMillis, x, y);
        } catch(IOException e) {
            telemetry.addLine(e.getMessage());
        }

        x += 1;
        y += 2;
    }

    @Override
    public void stop() {
        super.stop();
        try {
            logger.close();
        } catch (IOException e) {
            telemetry.addLine(e.getMessage());
        }
    }
}
