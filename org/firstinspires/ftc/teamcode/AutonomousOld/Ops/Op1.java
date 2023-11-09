package org.firstinspires.ftc.teamcode.AutonomousOld.Ops;

import org.firstinspires.ftc.teamcode.AutonomousOld.BaseOp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;
import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.Actions.*;

@Autonomous(name = "Old Auto 1", group = "Old Autonomous")
public class Op1 extends BaseOp {
    public static final Action[] movements = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 900, 0.5),
            new MoveAction(MoveDirection.LEFT, 500, 0.5),
            new MoveAction(MoveDirection.BACKWARD, 900, 0.5),
            new TurnAction(70, 1),
            new GamepadAction(GamepadButton.b),
            new WaitAction(100),
            new GamepadAction(GamepadButton.y),
            new GamepadAction(GamepadDynamicInput.right_trigger, 0.5f),
            new WaitAction(2000),
            new GamepadAction(GamepadDynamicInput.right_trigger, 0f),

            // ACTIONS HERE
    };

    public Op1() {
        super(movements);
    }
}