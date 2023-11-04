package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.ActionType;
import org.firstinspires.ftc.teamcode.Movements.Action;
import org.firstinspires.ftc.teamcode.Movements.MoveDirection;
import org.firstinspires.ftc.teamcode.Movements.GamepadButton;

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

class GamepadAction extends Action {
    public GamepadAction(GamepadButton gamepadButton, boolean isActive) {
        type = ActionType.GAMEPAD;

        button = gamepadButton;
        active = isActive;
    }
}

public class MovementManager {
    public Action[] actions = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 900, 0.5),
            new MoveAction(MoveDirection.LEFT, 500, 0.5),
            new MoveAction(MoveDirection.BACKWARD, 900, 0.5),
            new TurnAction(70, 1),
            new GamepadAction(GamepadButton.b, true)

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
