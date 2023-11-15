package org.firstinspires.ftc.teamcode.AutonomousNew.Ops;

import org.firstinspires.ftc.teamcode.AutonomousNew.BaseOp;

import org.firstinspires.ftc.teamcode.AutonomousNew.Camera.PossiblePosition;
import org.firstinspires.ftc.teamcode.AutonomousNew.Util.Alliance;

import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Movements.Actions.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.Func;

@Autonomous(name = "Blue Backstage", group = "New Autonomous")
public class Blue_Backstage extends BaseOp {
    public static final Action[] move_left = {
            // Strafe into Position
            new MoveAction(MoveDirection.LEFT, 225, 0.2),

            // Go to Stripe
            new MoveAction(MoveDirection.FORWARD, 1200, 0.7),
            new TurnAction(90, 0.4),
            new MoveAction(MoveDirection.BACKWARD, 1260, 0.7),
            new MoveAction(MoveDirection.LEFT, 300, 0.35),

            // Set down Pixels
            new GamepadAction(GamepadButton.b),
            new WaitAction(500),
            new GamepadAction(GamepadButton.y),

            // Recollect Yellow Pixel
            new WaitAction(300),
            new MoveAction(MoveDirection.BACKWARD, 190, 0.2),
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
            new TurnAction(180, 0.7),
            new MoveAction(MoveDirection.FORWARD, 675, 0.6),

            // Go to Correct Backboard Position
            new MoveAction(MoveDirection.LEFT, 400, 0.3),

            // Move closer to Backboard
            new MoveAction(MoveDirection.FORWARD, 200, 0.2),

            // Drop Pixel
            new GamepadAction(GamepadButton.y),

            // Move Away from Backboard
            new MoveAction(MoveDirection.BACKWARD, 400, 0.25),

            // Reset Arm (No Pause)
            new GamepadAction(GamepadButton.a, true),

            // Turn to Start Position
            new TurnAction(90, 0.4),

            // Move to Parking Area
            new MoveAction(MoveDirection.BACKWARD, 875, 0.6),
            new MoveAction(MoveDirection.LEFT, 800, 0.3),
    };

    public static final Action[] move_center = {
            // Strafe into Position
            new MoveAction(MoveDirection.LEFT, 450, 0.4),

            // Go to Stripe
            new MoveAction(MoveDirection.FORWARD, 1450, 0.9),

            // Set down Pixels
            new GamepadAction(GamepadButton.b),
            new WaitAction(500),
            new GamepadAction(GamepadButton.y),

            // Recollect Yellow Pixel
            new WaitAction(300),
            new MoveAction(MoveDirection.BACKWARD, 175, 0.2),
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
            new TurnAction(-90, 0.4),
            new MoveAction(MoveDirection.FORWARD, 1750, 1),

            // Go to Correct Backboard Position
            new MoveAction(MoveDirection.RIGHT, 400, 0.3),

            // Move closer to Backboard
            new MoveAction(MoveDirection.FORWARD, 200, 0.3),

            // Drop Pixel
            new GamepadAction(GamepadButton.y),

            // Move Away from Backboard
            new MoveAction(MoveDirection.BACKWARD, 400, 0.35),

            // Turn to Start Position
            new TurnAction(90, 0.5),

            // Reset Arm (No Pause)
            new GamepadAction(GamepadButton.a, true),

            // Move to Parking Area
            new MoveAction(MoveDirection.BACKWARD, 1400, 0.9),
            new MoveAction(MoveDirection.LEFT, 800, 0.3),
    };

    public static final Action[] move_right = {
            // Strafe into Position
            new MoveAction(MoveDirection.LEFT, 425, 0.4),

            // Go to Stripe
            new MoveAction(MoveDirection.FORWARD, 1400, 0.9),
            new TurnAction(90, 0.4),
            new MoveAction(MoveDirection.FORWARD, 200, 0.15),

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
            new TurnAction(180, 0.7),
            new MoveAction(MoveDirection.FORWARD, 1850, 1),

            // Go to Correct Backboard Position
            new MoveAction(MoveDirection.RIGHT, 550, 0.3),

            // Move closer to Backboard
            new MoveAction(MoveDirection.FORWARD, 100, 0.3),

            // Drop Pixel
            new GamepadAction(GamepadButton.y),

            // Move Away from Backboard
            new MoveAction(MoveDirection.BACKWARD, 400, 0.35),

            // Turn to Start Position
            new TurnAction(90, 0.5),

            // Reset Arm (No Pause)
            new GamepadAction(GamepadButton.a, true),

            // Move to Parking Area
            new MoveAction(MoveDirection.BACKWARD, 1650, 0.9),
            new MoveAction(MoveDirection.LEFT, 600, 0.3),
    };

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

    public void runnerCustomAction() {
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