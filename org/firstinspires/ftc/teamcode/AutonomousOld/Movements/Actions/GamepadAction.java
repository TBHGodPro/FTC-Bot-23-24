package org.firstinspires.ftc.teamcode.AutonomousOld.Movements.Actions;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;

public class GamepadAction extends Action {
    public GamepadAction(GamepadButton gamepadButton, boolean isActive) {
        type = ActionType.GAMEPAD;

        isDynamic = false;

        button = gamepadButton;
        active = isActive;
    }

    public GamepadAction(GamepadButton gamepadButton) {
        type = ActionType.GAMEPAD;

        isDynamic = false;

        button = gamepadButton;
    }

    public GamepadAction(GamepadDynamicInput gamepadInput, float inputValue) {
        type = ActionType.GAMEPAD;

        isDynamic = true;

        input = gamepadInput;
        value = inputValue;
    }
}
