package org.firstinspires.ftc.teamcode.Movements;

import org.firstinspires.ftc.teamcode.Movements.*;

public class MovementManager {
    public Action[] actions = {};

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

    public MovementManager(Action[] opActions) {
        actions = opActions;
    }

    public long button_tap_timeout = 500;
}
