package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Gamepad;
import java.lang.Thread;
import java.lang.reflect.Field;

import org.firstinspires.ftc.teamcode.MainOp;

import org.firstinspires.ftc.teamcode.Movements.*;

@Autonomous(name = "AutoOp")
public class AutoOp extends MainOp {
    AutonomousController controller;

    @Override
    public void runOpMode() {
        gamepad = new Gamepad();

        controller = new AutonomousController(this, gamepad);

        controller.start();

        super.runOpMode();
    }
}

class AutonomousController extends Thread {
    public AutoOp op;
    public Gamepad gamepad;

    private double turn_mult = 12.825;

    AutonomousController(AutoOp activeOp, Gamepad activeGamepad) {
        op = activeOp;
        gamepad = activeGamepad;
    }

    public void run() {
        MovementManager movements = new MovementManager();

        movements.resetActions();

        op.shouldOpenHandAtIntake = false;

        op.isHandClosed = true;

        op.waitForStart();

        while (movements.getCurrentAction() != null && op.opModeIsActive()) {
            Action action = movements.getCurrentAction();

            switch (action.type) {
                case WAIT: {
                    try {
                        Thread.sleep(action.timeLong);
                    } catch (Exception e) {
                    }

                    break;
                }

                case MOVE: {
                    switch (action.direction) {
                        case FORWARD: {
                            op.setWheelTargets(action.timeDouble, (int) (action.steps * op.backLeft_forward_correction),
                                    (int) (action.steps * op.backRight_forward_correction), action.steps, action.steps);

                            break;
                        }
                        case BACKWARD: {
                            op.setWheelTargets(action.timeDouble,
                                    -((int) (action.steps * op.backLeft_forward_correction)),
                                    -((int) (action.steps * op.backRight_forward_correction)), -action.steps,
                                    -action.steps);

                            break;
                        }

                        case LEFT: {
                            op.setWheelTargets(action.timeDouble, (int) (action.steps * op.backLeft_strafe_correction),
                                    -((int) (action.steps * op.backRight_strafe_correction)), -action.steps,
                                    action.steps);

                            break;
                        }
                        case RIGHT: {
                            op.setWheelTargets(action.timeDouble,
                                    -((int) (action.steps * op.backLeft_strafe_correction)),
                                    (int) (action.steps * op.backRight_strafe_correction), action.steps,
                                    -action.steps);

                            break;
                        }
                    }

                    while (op.wheelSetPositionTargetTime != null && op.opModeIsActive()) {
                    }

                    break;
                }

                case TURN: {
                    op.setWheelTargets(action.timeDouble, (int) (action.degrees * turn_mult),
                            -((int) (action.degrees * turn_mult)),
                            (int) (action.degrees * turn_mult), -((int) (action.degrees * turn_mult)));

                    while (op.wheelSetPositionTargetTime != null && op.opModeIsActive()) {
                    }

                    break;
                }

                case GAMEPAD: {
                    try {
                        Field field;

                        if (action.isDynamic) {
                            field = gamepad.getClass().getDeclaredField(action.input.name());

                            field.set(gamepad, action.value);
                        } else {
                            field = gamepad.getClass().getDeclaredField(action.button.name());

                            if (action.active == null) {
                                field.set(gamepad, true);

                                try {
                                    Thread.sleep(movements.button_tap_timeout);
                                } catch (Exception e) {
                                }

                                field.set(gamepad, false);
                            } else {
                                field.set(gamepad, action.active);
                            }
                        }
                    } catch (Exception e) {
                    }

                    break;
                }
            }

            movements.setCurrentActionCompleted();
        }
    }
}