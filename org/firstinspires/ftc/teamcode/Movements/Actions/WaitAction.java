package org.firstinspires.ftc.teamcode.Movements.Actions;

import org.firstinspires.ftc.teamcode.Movements.*;

public class WaitAction extends Action {
    public WaitAction(long waitTime) {
        type = ActionType.WAIT;

        timeLong = waitTime;
    }
}
