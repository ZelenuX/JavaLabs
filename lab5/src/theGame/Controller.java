package theGame;

import java.io.IOException;
import java.security.InvalidParameterException;

public class Controller {
    private BallsField field, backup;
    protected double allPassedSeconds;
    protected double curPlatformSpeed;
    private int needGodHelps;
    protected int platformDirection;//-1, 0, 1
    private boolean needRestart;
    protected double fullTime;

    protected void init(BallsField field){
        if (field == null)
            throw new InvalidParameterException();
        this.field = field;
        backup = field.recreate();
        field.addBall(0.5, field.lowestBallStartPos() + (double)1 / field.getRows(),
                -(double)field.getCols() / 3, (double)field.getRows() / 3, 0.75);
        field.setPlatformMaxSpeed(1);
        curPlatformSpeed = 0.5;
        allPassedSeconds = 0;
        needGodHelps = 0;
        platformDirection = 0;
        needRestart = false;
        fullTime = 0;
    }
    protected Controller(){}
    public Controller(BallsField field){
        init(field);
    }
    public void nextMoment(double passedSeconds) throws WinException, FailException, IOException {
        if (needRestart){
            backup.setUI(field.getUI());
            init(backup);
            return;
        }
        field.setPlatformSpeed(curPlatformSpeed * platformDirection);
        field.nextMoment(passedSeconds);
        allPassedSeconds += passedSeconds;
        if ((int)allPassedSeconds > (int)(allPassedSeconds - passedSeconds)) {
            field.multiplyBallsSpeed(1.01);
            curPlatformSpeed *= 1.01;
        }
        int tmp = needGodHelps;
        for (int j = needGodHelps; j > 0; --j)
            for (int i = 1; i < 10; ++i)
                field.addBall(i * 0.1, (field.highestBallStartPos() - field.lowestBallStartPos()) * 0.25 + field.lowestBallStartPos(),
                    curPlatformSpeed * 0.2 * (i - 5) * field.getCols(), curPlatformSpeed * field.getCols(), 0.5);
        needGodHelps -= tmp;
        fullTime += passedSeconds;
    }
    public void platformMoveRight(){
        platformDirection = 1;
    }
    public void platformMoveLeft(){
        platformDirection = -1;
    }
    public void stopPlatform(){
        platformDirection = 0;
    }
    public void godHelp(){
        ++needGodHelps;
    }
    public double getScore(){
        return field.getScore();
    }
    public void restart(){
        needRestart = true;
    }
    public double getFullTime(){
        return fullTime;
    }
}
