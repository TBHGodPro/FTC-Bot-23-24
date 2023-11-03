package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Gamepad;
import java.lang.Thread;

import org.firstinspires.ftc.teamcode.MainOp;

@Autonomous(name = "AutoOp")
public class AutoOp extends MainOp {
    GamepadController controller;

    @Override
    public void runOpMode() {
        gamepad = new Gamepad();

        gamepad.left_stick_y = 0.3f;

        controller = new GamepadController(this, gamepad);

        controller.start();

        super.runOpMode();
    }
}

class GamepadController extends Thread {
    public AutoOp op;
    public Gamepad gamepad;

    GamepadController(AutoOp activeOp, Gamepad activeGamepad) {
        op = activeOp;
        gamepad = activeGamepad;
    }

    public void run() {
        op.waitForStart();

        while (op.opModeIsActive()) {
            gamepad.left_stick_y = -0.3f;

            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }
    }
}