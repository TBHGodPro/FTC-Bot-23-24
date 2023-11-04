package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.ActionType;
import org.firstinspires.ftc.teamcode.Movements.Action;
import org.firstinspires.ftc.teamcode.Movements.MoveDirection;
import org.firstinspires.ftc.teamcode.Movements.GamepadButton;
import org.firstinspires.ftc.teamcode.Movements.GamepadDynamicInput;

class WaitAction extends Action {
    public WaitAction(long waitTime) {
        type = ActionType.WAIT;

        timeLong = waitTime;
    }
}

class MoveAction extends Action {
    public MoveAction(MoveDirection moveDirection, int moveSteps, double moveTime) {
        type = ActionType.MOVE;

        timeDouble = moveTime;

        direction = moveDirection;
        steps = moveSteps;
    }
}

class TurnAction extends Action {
    public TurnAction(double turnDegrees, double turnTime) {
        type = ActionType.TURN;

        timeDouble = turnTime;

        degrees = turnDegrees;
    }
}

class GamepadAction extends Action {
    public GamepadAction(GamepadButton gamepadButton, boolean isActive) {
        type = ActionType.GAMEPAD;

        isDynamic = false;

        button = gamepadButton;
        active = isActive;
    }

    public GamepadAction(GamepadDynamicInput gamepadInput, float inputValue) {
        type = ActionType.GAMEPAD;

        isDynamic = true;

        input = gamepadInput;
        value = inputValue;
    }
}

public class MovementManager {
    public Action[] actions = {
            // ACTIONS HERE

            new MoveAction(MoveDirection.FORWARD, 900, 0.5),
            new MoveAction(MoveDirection.LEFT, 500, 0.5),
            new MoveAction(MoveDirection.BACKWARD, 900, 0.5),
            new TurnAction(70, 1),
            new GamepadAction(GamepadButton.b, true),
            new WaitAction(150),
            new GamepadAction(GamepadButton.b, false),
            new GamepadAction(GamepadDynamicInput.right_trigger, 0.5f),
            new WaitAction(2000),
            new GamepadAction(GamepadDynamicInput.right_trigger, 0f),

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
