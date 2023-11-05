package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.AutoOp;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Movements.Actions.*;

@Autonomous(name = "Auto Op 2")
public class Op2 extends AutoOp {
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

    public Op2() {
        super(movements);
    }
}
