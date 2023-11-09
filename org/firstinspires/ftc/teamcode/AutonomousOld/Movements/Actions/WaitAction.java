package org.firstinspires.ftc.teamcode.AutonomousOld.Movements.Actions;

import org.firstinspires.ftc.teamcode.AutonomousOld.Movements.*;

public class WaitAction extends Action {
    public WaitAction(long waitTime) {
        type = ActionType.WAIT;

        timeLong = waitTime;
    }
}
