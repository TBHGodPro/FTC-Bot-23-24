package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.teamcode.MainOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Camera.*;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.Thread;
import java.lang.reflect.Field;

import org.firstinspires.ftc.teamcode.Camera.*;

public abstract class AutoOp extends MainOp {
    public AutonomousController controller;

    public OpenCVManager manager;

    @Override
    public void runOpMode() {
        gamepad = new Gamepad();

        controller = new AutonomousController(this, gamepad);

        manager = new OpenCVManager(hardwareMap.get(WebcamName.class, "Webcam 1"));

        manager.create();

        controller.start();

        super.runOpMode();
    }

    public abstract void runLoop();
}

class AutonomousController extends Thread {
    public AutoOp op;
    public Gamepad gamepad;

    private double turn_mult = 12.825;

    public AutonomousController(AutoOp activeOp, Gamepad activeGamepad) {
        op = activeOp;
        gamepad = activeGamepad;
    }

    public void run() {
        op.shouldOpenHandAtIntake = false;

        op.isHandClosed = true;

        op.waitForStart();

        waitTime(500);

        while (op.opModeIsActive()) {
            op.runLoop();
        }

        op.manager.close();
    }

    private void waitTime(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {

        }
    }
}
