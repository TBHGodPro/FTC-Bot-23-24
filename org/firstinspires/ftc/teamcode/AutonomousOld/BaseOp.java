package org.firstinspires.ftc.teamcode.AutonomousOld;

import org.firstinspires.ftc.teamcode.MainOp;
import org.firstinspires.ftc.teamcode.Movements.*;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.Thread;
import java.lang.reflect.Field;

public class BaseOp extends MainOp {
    public Action[] movements;

    public AutonomousController controller;

    public BaseOp(Action[] movements) {
        this.movements = movements;
    }

    @Override
    public void runOpMode() {
        gamepad = new Gamepad();

        controller = new AutonomousController(this, gamepad);

        controller.start();

        super.runOpMode();
    }

    public void setupExtraTelemetry() {
        return;
    }
}

class AutonomousController extends Thread {
    public BaseOp op;
    public Gamepad gamepad;

    private double turn_mult = 12.825;

    AutonomousController(BaseOp activeOp, Gamepad activeGamepad) {
        op = activeOp;
        gamepad = activeGamepad;
    }

    public void run() {
        MovementManager movements = new MovementManager(op.movements);

        movements.resetActions();

        op.shouldOpenHandAtIntake = false;

        op.isHandClosed = true;

        op.waitForStart();

        waitTime(500);

        new MovementRunner(op, movements, gamepad).run();

        waitTime(500);
        
        gamepad.a = true;

        waitTime(250);
        
        gamepad.a = false;
    }

    private void waitTime(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {

        }
    }
}