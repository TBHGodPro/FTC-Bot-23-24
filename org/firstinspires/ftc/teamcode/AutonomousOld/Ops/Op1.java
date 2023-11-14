package org.firstinspires.ftc.teamcode.AutonomousOld.Ops;

import org.firstinspires.ftc.teamcode.AutonomousOld.BaseOp;
import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Movements.Actions.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Old Auto 1", group = "Old Autonomous")
public class Op1 extends BaseOp {
    public static final Action[] movements = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 1450, 1.2),
            new TurnAction(90, 0.5),
            new GamepadAction(GamepadButton.b),
            new WaitAction(150),
            new GamepadAction(GamepadButton.y),

            // ACTIONS HERE
    };

    public Op1() {
        super(movements);
    }
}