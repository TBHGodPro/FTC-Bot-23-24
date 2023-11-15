package org.firstinspires.ftc.teamcode.AutonomousNew.Ops;

import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;

import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.PossiblePosition;
import org.firstinspires.ftc.teamcode.AutonomousNew.Util.Alliance;

import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Movements.Actions.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Func;

@Autonomous(name = "Red Backstage", group = "New Autonomous")
public class Red_Backstage extends BaseOp {
    public static final Action[] move_left = {
            // Go to Stripe
            new MoveAction(MoveDirection.FORWARD, 1400, 1.2),
            new TurnAction(-90, 0.6),
            new MoveAction(MoveDirection.FORWARD, 150, 0.15),

            // Set down Pixels
            new GamepadAction(GamepadButton.b),
            new WaitAction(250),
            new GamepadAction(GamepadButton.y),

            // Recollect Yellow Pixel
            new WaitAction(300),
            new MoveAction(MoveDirection.BACKWARD, 200, 0.2),
            new GamepadAction(GamepadButton.x),

            // Lift Arm
            new GamepadAction(GamepadDynamicInput.right_trigger, 0.5f),
            new WaitAction(400),
            new GamepadAction(GamepadDynamicInput.right_trigger, 0),

            // Lift Wrist
            new GamepadAction(GamepadButton.right_bumper, true),
            new WaitAction(850),
            new GamepadAction(GamepadButton.right_bumper, false),

            // Go to Backboard
            new TurnAction(180, 1),
            new MoveAction(MoveDirection.FORWARD, 1850, 1.3),

            // Go to Correct Backboard Position
            new MoveAction(MoveDirection.LEFT, 450, 0.3),

            // Move closer to Backboard
            new MoveAction(MoveDirection.FORWARD, 150, 0.3),

            // Drop Pixel
            new GamepadAction(GamepadButton.y),

            // Move Away from Backboard
            new MoveAction(MoveDirection.BACKWARD, 400, 0.35),

            // Turn to Start Position
            new TurnAction(-90, 0.7),

            // Move to Parking Area
            new MoveAction(MoveDirection.BACKWARD, 1550, 1.2),
            new MoveAction(MoveDirection.RIGHT, 600, 0.5),
    };

    public static final Action[] move_center = {};

    public static final Action[] move_right = {};

    public PossiblePosition position;

    public void runOP() {
        // Prop Detection

        // - Give 1.75 Seconds
        sleep(1_750);

        // - Image Detected
        position = manager.processor.position;

        // - Turn Off Camera (Save CPU Cycles)
        manager.close();

        // Movement

        // - Get Movements
        MovementManager movements = new MovementManager(move_left);

        switch (position) {
            case LEFT: {
                movements = new MovementManager(move_left);

                break;
            }

            case CENTER: {
                movements = new MovementManager(move_center);

                break;
            }

            case RIGHT: {
                movements = new MovementManager(move_right);

                break;
            }
        }

        // - MOVE

        new MovementRunner(this, movements, gamepad).run();
    }

    public void setupExtraTelemetry() {
        telemetry.addLine();

        telemetry.addLine("--- Autonomous ---");

        telemetry.addLine();

        telemetry.addData("Decided Position", new Func<String>() {
            @Override
            public String value() {
                return position == null ? ("DECIDING (" + manager.processor.position + ")") : position.name();
            }
        });
    }

    public Alliance getAlliance() {
        return Alliance.RED;
    };
}