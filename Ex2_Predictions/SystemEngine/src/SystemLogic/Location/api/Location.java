package SystemLogic.Location.api;

import SystemLogic.Location.imp.Point;
import SystemLogic.grid.imp.Grid;

public interface Location {
    void setLocation(Point location);
    Point getLocation();
    void updateLocation(Grid grid);


}
