package org.firstinspires.ftc.teamcode.AutonomousOld.Ops;

import org.firstinspires.ftc.teamcode.AutonomousOld.BaseOp;
import org.firstinspires.ftc.teamcode.Movements.*;
import org.firstinspires.ftc.teamcode.Movements.Actions.*;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous(name = "Old Auto 2", group = "Old Autonomous")
public class Op2 extends BaseOp {
    public static final Action[] movements = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 1450, 1.25),
            new GamepadAction(GamepadButton.b),
            new WaitAction(150),
            new GamepadAction(GamepadButton.y),

            // ACTIONS HERE
    };

    public Op2() {
        super(movements);
    }
}