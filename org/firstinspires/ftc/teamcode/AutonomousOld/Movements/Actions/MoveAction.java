package org.firstinspires.ftc.teamcode.AutonomousOld.Movements.Actions;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;

public class MoveAction extends Action {
    public MoveAction(MoveDirection moveDirection, int moveSteps, double moveTime) {
        type = ActionType.MOVE;

        timeDouble = moveTime;

        direction = moveDirection;
        steps = moveSteps;
    }
}
