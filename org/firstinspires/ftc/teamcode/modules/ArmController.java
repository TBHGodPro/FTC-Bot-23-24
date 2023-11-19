package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotor.RunMode;
import com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior;

public class ArmController {
    // --- Constants ---

    public static final double armNonLinearity = 2; // 1 = linear

    public static final int armManualInterval = 15;

    public static final int armMaxPos = 2000;

    public static final int armSpeed = 1100;

    public static final double wristPosInterval = 0.0035;

    public static final double wristMinRange = 0.25;
    public static final double wristMaxRange = 1;

    public static final double handOpenPos = 0.65;
    public static final double handClosedPos = 1;

    // -----------------

    public final Gamepad gamepad;

    public final DcMotorEx arm;
    public static final DcMotorEx.Direction armDirection = DcMotorEx.Direction.REVERSE;
    public static final ZeroPowerBehavior armZeroPowerBehavior = ZeroPowerBehavior.BRAKE;
    public int armPos = 0;

    public final Servo wrist;
    public static final Servo.Direction wristDirection = Servo.Direction.REVERSE;
    public double wristPos = 1;

    public final Servo hand;
    public static final Servo.Direction handDirection = Servo.Direction.FORWARD;
    public boolean isHandClosed = false;

    public ArmController(Gamepad gamepad, DcMotorEx arm, Servo wrist, Servo hand) {
        this.gamepad = gamepad;

        this.arm = arm;
        this.wrist = wrist;
        this.hand = hand;
    }

    public void init() {
        arm.setDirection(armDirection);
        arm.setZeroPowerBehavior(armZeroPowerBehavior);
        arm.setMode(RunMode.STOP_AND_RESET_ENCODER);

        wrist.setDirection(wristDirection);
        wrist.scaleRange(wristMinRange, wristMaxRange);

        hand.setDirection(handDirection);
    }

    public void prep() {
        arm.setTargetPosition(armPos);

        arm.setMode(RunMode.RUN_TO_POSITION);

        arm.setVelocity(armSpeed);
    }

    public void update() {
        double armPower = 0;

        armPower += Math.pow(gamepad.right_trigger, armNonLinearity);
        armPower -= Math.pow(gamepad.left_trigger, armNonLinearity);

        if (armPos + armPower * armManualInterval <= armMaxPos) {
            armPos += (int) (armPower * armManualInterval);
        }

        arm.setTargetPosition(armPos);
    }
}
