package org.firstinspires.ftc.teamcode.Components.Mechanisms.Drivetrains.TankDrivetrains;

import org.firstinspires.ftc.teamcode.Components.Mechanisms.Drivetrains.Drivetrain;
import com.qualcomm.robotcore.hardware.DcMotor;


import org.firstinspires.ftc.teamcode.Universal.Math.Pose;
import org.firstinspires.ftc.teamcode.Universal.UniversalConstants;
import org.firstinspires.ftc.teamcode.Universal.UniversalFunctions;
import org.firstinspires.ftc.teamcode.Universal.Math.Vector2;

/**
 * Created by Frank Portman on 5/21/2018
 */
public abstract class TankDT extends Drivetrain {
    public double       ENC_PER_INCH;
    public double       DISTANCE_BETWEEN_WHEELS;
    public double       turnMult = 1
            ,
                        angleBetween,
                        directionMult = 1,
                        cos,
                        maxTurn = 0.75,
                        leftPow,
                        rightPow;
    public boolean      turn = false;
    public int          leftEncVal = 0,
                        rightEncVal = 0;
    public Pose         position;
    public DcMotor[]    leftMotors,
                        rightMotors;
    public Direction    direction;
    public ControlState controlState;
    public FCTurnState  turnState;
    public Vector2 destination;
    public Vector2 turnVector = new Vector2();

    public TankDT() {
        leftPow = 0;
        rightPow = 0;
        direction = Direction.FOR;
        controlState = ControlState.ARCADE;
        turnState = FCTurnState.FAST;
    }

    //Different control systems
    public enum ControlState{
        ARCADE,
        TANK,
        FIELD_CENTRIC,
        CHEESY
    }

    //Two algorithms for turning in field-centric mode
    public enum FCTurnState{
        SMOOTH,
        FAST
    }
    public void newFieldCentric(Vector2 destinationVect, Vector2 angle, double threshold) {
        if(destinationVect.magnitude() > threshold)
            destinationVect.setFromPolar(1, destinationVect.angle());
        else
            destinationVect.scalarMultiply(1.0 / threshold);
        angleBetween = UniversalFunctions.normalizeAngleRadians(destinationVect.angle(), angle.angle());
        double tempTurnMult = 0;
        switch (direction) {
            case FOR:
                directionMult = 1;
                if(Math.sin(angleBetween) < 0) {
                    setLeftPow(Math.cos(angleBetween) < 0 ? -1 : 1);
                    setRightPow(Math.cos(angleBetween) > 0 ? -1 : 1);
                }
                else{
                    double cos = Math.cos(angleBetween);
                    tempTurnMult = Math.abs(cos) + 1;
                    rightPow = directionMult * (destinationVect.magnitude() - tempTurnMult * turnMult * cos);
                    leftPow = directionMult * (destinationVect.magnitude() + tempTurnMult * turnMult * cos);
                    setLeftPow();
                    setRightPow();
                }
                break;
            case BACK:
                directionMult = -1;
                if(Math.sin(angleBetween) > 0) {
                    setLeftPow(Math.cos(angleBetween)< 0 ? 1 : -1);
                    setRightPow(Math.cos(angleBetween) > 0 ? 1 : -1);
                }
                else{
                    double cos = Math.cos(angleBetween);
                    tempTurnMult = Math.abs(cos) + 1;
                    rightPow = directionMult * (destinationVect.magnitude() - tempTurnMult * turnMult * cos);
                    leftPow = directionMult * (destinationVect.magnitude() + tempTurnMult * turnMult * cos);
                    setRightPow();
                    setLeftPow();
                }
                break;
        }
    }
    //Basic Tele-Op driving functionality
    public void teleOpLoop(Vector2 leftVect, Vector2 rightVect, Vector2 angle){
        switch(controlState) {
            case ARCADE:
                turnMult = 1 - leftVect.magnitude() * (1 - maxTurn);
                leftPow = leftVect.y + turnMult * rightVect.x;
                rightPow = leftVect.y - turnMult * rightVect.x;
                break;

            case FIELD_CENTRIC:
                angleBetween = UniversalFunctions.normalizeAngleRadians(leftVect.angle(), angle.angle());
                if (leftVect.magnitude() < UniversalConstants.Triggered.STICK) {
                    leftPow = 0;
                    rightPow = 0;
                } else {
                    switch (direction) {
                        case FOR:
                            if (Math.sin(angleBetween) < 0 && turn) {
                                direction = Direction.BACK;
                                directionMult *= -1;
                                turn = false;
                            } else if (Math.sin(angleBetween) >= 0)
                                turn = true;
                            break;

                        case BACK:
                            if (Math.sin(angleBetween) > 0 && turn) {
                                direction = Direction.FOR;
                                turn = false;
                                directionMult *= -1;
                            } else if (Math.sin(angleBetween) <= 0)
                                turn = true;
                            break;
                    }

                    cos = Math.cos(angleBetween);
                    switch (turnState) {
                        case FAST:
                            turnMult = Math.abs(cos) + 1;
                            leftPow = directionMult * (leftVect.magnitude() - turnMult * cos);
                            rightPow = directionMult * (leftVect.magnitude() + turnMult * cos);
                            break;

                        case SMOOTH:
                            if (cos > 0) {
                                leftPow = directionMult * leftVect.magnitude();
                                rightPow = directionMult * -Math.cos(2 * angleBetween) * leftVect.magnitude();
                            } else {
                                rightPow = directionMult * leftVect.magnitude();
                                leftPow = directionMult * -Math.cos(2 * angleBetween) * leftVect.magnitude();
                            }
                            break;
                    }
                }
                break;

            case TANK:
                leftPow = rightVect.y;
                rightPow = leftVect.y;
                break;
            case CHEESY:

                break;
        }
        setLeftPow(leftPow);
        setRightPow(rightPow);
    }

    public void teleOpLoop(Vector2 leftVect, Vector2 rightVect, double angle){
        Vector2 angleVect = new Vector2();
        angleVect.setFromPolar(1, angle);
        teleOpLoop(leftVect, rightVect, angleVect);
    }
    public void mildSpicyDrive(Vector2 leftVect, double leftBumper, double rightBumper){
        turnMult = 1 - leftVect.magnitude() * (1 - maxTurn);
        Vector2 turnVect = new Vector2(rightBumper - leftBumper, 0);
        leftPow = leftVect.y + leftVect.x * turnVect.x;
        rightPow = leftVect.y - leftVect.x * turnVect.x;
        setLeftPow();
        setRightPow();
    }

    public void driveToPoint(double x, double y, Vector2 angle, Direction dir, double threshold){
        Vector2 destinationVect = new Vector2(x, y);
        destinationVect.x -= position.x;
        destinationVect.y -= position.y;
        direction = dir;
        newFieldCentric(destinationVect, angle, threshold);
    }

    //x and y must be in inches
    public synchronized void driveToPointCircular(double x, double y, Direction dir){
        double radius = (x*x + y*y)/(2 * x);
        double xOffset = Math.signum(radius) * Math.abs(radius);
        radius = Math.abs(radius);
        double angle = Math.atan2(y, x - xOffset);
        double r = angle * (DISTANCE_BETWEEN_WHEELS + 2 * radius);
        double l = angle * (DISTANCE_BETWEEN_WHEELS - 2 * radius);
        directionMult = 1;
        switch(dir){
            case BACK:
                directionMult = -1;
                break;
        }
        double max = directionMult * UniversalFunctions.maxAbs(l, r);
        setRightPow(r / max);
        setLeftPow(l / max);
    }

    public synchronized void driveToPointCircular(double x, double y, Direction dir, double maxSpeed){
        this.maxSpeed = maxSpeed;

    }

    public synchronized void updateLocation(double leftChange, double rightChange){
        leftChange /= ENC_PER_INCH;
        rightChange /= ENC_PER_INCH;
        double angle = 0;
        double radius = 0;
        turnVector = new Vector2();
        if (rightChange == leftChange)
            turnVector.setFromPolar(rightChange, position.angle);
        else {
            radius = DISTANCE_BETWEEN_WHEELS / 2 * (leftChange + rightChange) / (rightChange - leftChange);
            angle = (rightChange - leftChange) / (DISTANCE_BETWEEN_WHEELS);
            turnVector.x = radius * Math.cos(angle) - radius;
            turnVector.y = radius * Math.sin(angle);
        }
            /*radius = Math.abs(radius);
            drivetrain.turnVector.setFromPolar(radius, angle);
            drivetrain.turnVector.setFromPolar(radius - drivetrain.turnVector.x, angle);
            */
        //if (Math.min(leftChange, rightChange) == -UniversalFunctions.maxAbs(leftChange, rightChange))
        //drivetrain.turnVector.x *= -1;
        turnVector.rotate(position.angle);
        position.x += turnVector.x / 2;
        position.y += turnVector.y / 2;
        position.angle += angle / 2;
    }
    
    //Sets the power of the left motor(s)
    public abstract void setLeftPow(double pow);

    //Sets the power of the right motor(s)
    public abstract void setRightPow(double pow);

    //Sets the power of the left motor(s) to the leftPow variable
    public void setLeftPow(){
        setLeftPow(leftPow);
    }

    //Sets the power of the right motor(s) to the rightPow variable
    public void setRightPow(){
        setRightPow(rightPow);
    }

    //Sets the maximum speed of the drive motors
    public void setSpeed(double speed){
        maxSpeed = speed;
    }

    //turns the front of the robot to the specified direction
    public void turnToFace(Vector2 currentAngle, Vector2 desiredAngle){;
        angleBetween = UniversalFunctions.normalizeAngleRadians(desiredAngle.angle() + Math.PI / 2, currentAngle.angle());
        double tempTurnMult = 0;
        switch (direction) {
            case FOR:
                directionMult = 1;
                if(Math.sin(angleBetween) < 0) {
                    setLeftPow(Math.cos(angleBetween) < 0 ? -1 : 1);
                    setRightPow(Math.cos(angleBetween) > 0 ? -1 : 1);
                }
                else{
                    double cos = Math.cos(angleBetween);
                    tempTurnMult = Math.abs(cos) + 1;
                    rightPow = directionMult * -tempTurnMult * turnMult * cos;
                    leftPow = directionMult * tempTurnMult * turnMult * cos;
                    setLeftPow();
                    setRightPow();
                }
                break;
            case BACK:
                directionMult = -1;
                if(Math.sin(angleBetween) > 0) {
                    setLeftPow(Math.cos(angleBetween)< 0 ? 1 : -1);
                    setRightPow(Math.cos(angleBetween) > 0 ? 1 : -1);
                }
                else{
                    double cos = Math.cos(angleBetween);
                    tempTurnMult = Math.abs(cos) + 1;
                    rightPow = directionMult * -tempTurnMult * turnMult * cos;
                    leftPow = directionMult * tempTurnMult * turnMult * cos;
                    setRightPow();
                    setLeftPow();
                }
                break;
        }
    }
    public void turnToFace(Vector2 currentAngle, double desiredAngle){
        Vector2 desiredAng = new Vector2();
        desiredAng.setFromPolar(1, desiredAngle);
        turnToFace(currentAngle, desiredAng);
    }

    //returns a boolean representing whether the drivetrain is facing a given direction
    public boolean isFacing(Vector2 currentAngle, Vector2 desiredAngle, double tolerance){
        return UniversalFunctions.withinTolerance(-tolerance, currentAngle.angleBetween(desiredAngle), tolerance);
    }

    //returns a boolean representing whether the drivetrain is facing a given direction
    public boolean isFacingBack(Vector2 currentAngle, Vector2 desiredAngle, double tolerance){
        return UniversalFunctions.withinTolerance(-tolerance, UniversalFunctions.normalizeAngleRadians(currentAngle.angleBetween(desiredAngle) + Math.PI), tolerance);
    }

    //Turns the back of the robot to the specified direction
    public void turnToFaceBack(Vector2 currentAngle, Vector2 desiredAngle, double turnSpeed){
        angleBetween = UniversalFunctions.normalizeAngleRadians(currentAngle.angleBetween(desiredAngle) + Math.PI);
        turnSpeed *= Math.sin(angleBetween);
        leftPow = -turnSpeed;
        rightPow = turnSpeed;
    }
    public void stop(){
        setLeftPow(0);
        setRightPow(0);
    }
    public abstract double averageLeftEncoders();
    public abstract double averageRightEncoders();
}