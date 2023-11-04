package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.ActionType;
import org.firstinspires.ftc.teamcode.Movements.Action;
import org.firstinspires.ftc.teamcode.Movements.MoveDirection;

class MoveAction extends Action {
    public MoveAction(MoveDirection moveDirection, int moveSteps, double moveTime) {
        type = ActionType.MOVE;

        time = moveTime;

        direction = moveDirection;
        steps = moveSteps;
    }
}

class TurnAction extends Action {
    public TurnAction(double turnDegrees, double turnTime) {
        type = ActionType.TURN;

        time = turnTime;

        degrees = turnDegrees;
    }
}

public class MovementManager {
    public Action[] actions = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 2000, 1.5),
            new TurnAction(45, 2)

            // ACTIONS HERE
    };

    private int currentActionIndex = 0;

    public void resetActions() {
        currentActionIndex = 0;
    }

    public Action getCurrentAction() {
        return currentActionIndex >= actions.length ? null : actions[currentActionIndex];
    }

    public void setCurrentActionCompleted() {
        currentActionIndex += 1;
    }
}
