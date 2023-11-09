package org.firstinspires.ftc.teamcode.AutonomousNew;

import org.firstinspires.ftc.teamcode.MainOp;
import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.*;
import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.Thread;
import java.lang.reflect.Field;

public abstract class BaseOp extends MainOp {
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
    public BaseOp op;
    public Gamepad gamepad;

    public AutonomousController(BaseOp activeOp, Gamepad activeGamepad) {
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
