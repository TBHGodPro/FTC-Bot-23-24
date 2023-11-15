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
    public static final Action[] move_left = {};

    public static final Action[] move_center = {};

    public static final Action[] move_right = {
                // Strafe into Posi
                new MoveAction(MoveDirection.LEFT, 425, 0

                // Go to St
                new MoveAction(MoveDirection.FORWARD, 1400, 0
                new TurnAction(90, 0
                new MoveAction(MoveDirection.FORWARD, 150, 0.

                // Set down Pi
                new GamepadAction(GamepadButton
                new WaitAction(2
                new GamepadAction(GamepadButton

                // Recollect Yellow P
                new WaitAction(3
                new MoveAction(MoveDirection.BACKWARD, 200, 0
                new GamepadAction(GamepadButton

                // Lift
                new GamepadAction(GamepadDynamicInput.right_trigger, 0.
                new WaitAction(4
                new GamepadAction(GamepadDynamicInput.right_trigger,

                // Lift W
                new GamepadAction(GamepadButton.right_bumper, tr
                new WaitAction(8
                new GamepadAction(GamepadButton.right_bumper, fal

                // Go to Backb
                new TurnAction(180, 0
                new MoveAction(MoveDirection.FORWARD, 1850,

                // Go to Correct Backboard Posi
                new MoveAction(MoveDirection.RIGHT, 550, 0

                // Move closer to Backb
                new MoveAction(MoveDirection.FORWARD, 150, 0

                // Drop P
                new GamepadAction(GamepadButton

                // Move Away from Backb
                new MoveAction(MoveDirection.BACKWARD, 400, 0.

                // Turn to Start Posi
                new TurnAction(90, 0

                // Reset Arm (No Pa
                new GamepadAction(GamepadButton.a, tr

                // Move to Parking 
                new MoveAction(MoveDirection.BACKWARD, 1550, 0
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