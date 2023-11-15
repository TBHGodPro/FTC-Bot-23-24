package org.firstinspires.ftc.teamcode.Movements;

import java.lang.reflect.Field;

import org.firstinspires.ftc.teamcode.MainOp;

import com.qualcomm.robotcore.hardware.Gamepad;

public class MovementRunner {
    public MainOp op;
    public MovementManager movements;
    public Gamepad gamepad;

    private double turn_mult = 12.825;
    private long button_tap_timeout = 500;

    private double backLeft_forward_correction = 0.99;
    private double backRight_forward_correction = 1.00;

    private double backLeft_strafe_correction = 0.85;
    private double backRight_strafe_correction = 0.8;

    public MovementRunner(MainOp op, MovementManager movements, Gamepad gamepad) {
        this.op = op;

        this.movements = movements;

        this.gamepad = gamepad;
    }

    public void run() {
        while (movements.getCurrentAction() != null && op.opModeIsActive()) {
            Action action = movements.getCurrentAction();

            switch (action.type) {
                case WAIT: {
                    waitTime(action.timeLong);

                    break;
                }

                case MOVE: {
                    switch (action.direction) {
                        case FORWARD: {
                            op.setWheelTargets(action.timeDouble, (int) (action.steps * backLeft_forward_correction),
                                    (int) (action.steps * backRight_forward_correction), action.steps, action.steps);

                            break;
                        }
                        case BACKWARD: {
                            op.setWheelTargets(action.timeDouble,
                                    -((int) (action.steps * backLeft_forward_correction)),
                                    -((int) (action.steps * backRight_forward_correction)), -action.steps,
                                    -action.steps);

                            break;
                        }

                        case LEFT: {
                            op.setWheelTargets(action.timeDouble, (int) (action.steps * backLeft_strafe_correction),
                                    -((int) (action.steps * backRight_strafe_correction)), -action.steps,
                                    action.steps);

                            break;
                        }
                        case RIGHT: {
                            op.setWheelTargets(action.timeDouble,
                                    -((int) (action.steps * backLeft_strafe_correction)),
                                    (int) (action.steps * backRight_strafe_correction), action.steps,
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

                                waitTime(button_tap_timeout);

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

    private void waitTime(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {

        }
    }
}
