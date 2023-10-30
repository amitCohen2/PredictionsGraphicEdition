package SystemLogic.Location.imp;

import SystemLogic.Location.api.Location;
import SystemLogic.grid.imp.Grid;

import java.util.ArrayList;
import java.util.Random;

public class LocationImpl implements Location {

    private Point entityLocation;
    @Override
    public void setLocation(Point location){
        this.entityLocation = location;
    }
    @Override
    public Point getLocation() {
        return this.entityLocation;
    }

    @Override
    public void updateLocation(Grid grid) {
       Point availablePoint = grid.getAvailablePoint(this.entityLocation);

        if(availablePoint!= null){
            grid.updateEntityOnGrid(entityLocation,availablePoint);
        }

    }

    public Point getRandomPoint(ArrayList<Point> availablePoints) {
        if (availablePoints.isEmpty()) {
            return null; // No available points
        }

        Random random = new Random();
        int randomIndex = random.nextInt(availablePoints.size()); // Generate a random index
        return availablePoints.get(randomIndex); // Get the point at the random index
    }
}
