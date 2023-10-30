package SystemLogic.Timer.imp;

public class TimeMeasurement {
    private double startTimeInSeconds;
    private double currTimeInSeconds;
    private double totalTime;
    private double prevTotalTime;

    public TimeMeasurement() {
        this.startTimeInSeconds = 0;
        this.currTimeInSeconds = 0;
        this.totalTime = 0;
        this.prevTotalTime = 0;
    }

    public void start() {
        startTimeInSeconds = getCurrentTimeInSeconds();
    }
    public double checkElapsedTime() {
        if (startTimeInSeconds == 0) {
            throw new IllegalStateException("The timer has not been started.");
        }
        currTimeInSeconds = getCurrentTimeInSeconds();
        totalTime = currTimeInSeconds - startTimeInSeconds + prevTotalTime;
        return currTimeInSeconds - startTimeInSeconds;
    }

    public double getCurrentTimeInSeconds() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis / 1000; // Convert milliseconds to seconds
    }

    public void stop() {
        currTimeInSeconds = getCurrentTimeInSeconds();
        totalTime = currTimeInSeconds - startTimeInSeconds + prevTotalTime;
        prevTotalTime = totalTime;
    }

    public double   getTotalTime() {
        return totalTime;
    }
}