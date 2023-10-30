
package SystemLogic.grid.imp;
import SystemLogic.Location.api.Location;
import SystemLogic.Location.imp.LocationImpl;
import SystemLogic.Location.imp.Point;
import SystemLogic.definition.entity.EntityDefinition;
import SystemLogic.execution.instance.enitty.EntityInstance;

import java.util.ArrayList;
import java.util.Random;


public class Grid {
    private int numOfRows;
    private int numOfColumns;
    private EntityInstance[][] entityMatrix; // The matrix to store entities

    private ArrayList<Point> AvailablePoints;

    public Grid(int rows, int columns) {
        this.numOfRows = rows;
        this.numOfColumns = columns;
        this.entityMatrix = new EntityInstance[rows][columns]; // Initialize the entity matrix
        AvailablePoints = new ArrayList<>();
        for(int i =0; i<rows; i++){
            for(int j=0; j < columns; j++){
                Point point = new Point(i,j);
                AvailablePoints.add(point);
            }
        }
    }
    public void deepCopy(EntityInstance[][] copyEntityMatrix) {
        int copyRows = copyEntityMatrix.length;
        int copyColumns = copyEntityMatrix[0].length;

        for (int i = 0; i < numOfRows && i < copyRows; i++) {
            for (int j = 0; j < numOfColumns && j < copyColumns; j++) {
                entityMatrix[i][j] = copyEntityMatrix[i][j];
            }
        }
    }
    public void  clearAll(){
        AvailablePoints.clear();
        for(int i =0; i<numOfRows; i++){
            for(int j=0; j < numOfColumns; j++){
                Point point = new Point(i,j);
                AvailablePoints.add(point);
                entityMatrix[i][j]= null;
            }
        }
    }
    public Location getAvailableLocation() {
        Random random = new Random();

        // Check if there are available points left
        if (AvailablePoints.isEmpty()) {
            // No available points left
            return null;
        }

        // Generate a random index to select a point
        int randomIndex = random.nextInt(AvailablePoints.size());

        // Get the randomly selected point
        Point randomPoint = AvailablePoints.get(randomIndex);

        // Remove the selected point from the list of available points
        //AvailablePoints.remove(randomIndex);

        // Create a Location instance based on the selected point
        Location location = new LocationImpl();
        location.setLocation(randomPoint);

        return location;
    }
    public void addEntity(EntityInstance entityInstance) {
        try{
            Point entityLocationPoint = entityInstance.getLocation().getLocation();
            entityMatrix[entityLocationPoint.getRow()][entityLocationPoint.getColum()] = entityInstance;

            // Remove the point from AvailablePoints
            AvailablePoints.remove(entityLocationPoint);
        }  catch (ArrayIndexOutOfBoundsException e){
        System.out.println(e.getMessage());
    }


}

    public void clearLocation(Location location) {
        Point locationPoint = location.getLocation();
        try{
            entityMatrix[locationPoint.getRow()][locationPoint.getColum()] = null;

            // Add the point back to AvailablePoints if it's not already in the list
            if (!AvailablePoints.contains(locationPoint)) {
                AvailablePoints.add(locationPoint);
            }
        }  catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }

    }
    public Point getAvailablePoint(Point pointLocation) {
        ArrayList<Point> emptyPoints = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) { // Changed the condition to exclude the central point
                    int col = getRoundCol(pointLocation.getColum() + j);
                    int row = getRoundRow(pointLocation.getRow() + i);

                    // Check if the row and column are within valid bounds
                    if (row >= 0 && row < numOfRows && col >= 0 && col < numOfColumns) {
                        if (entityMatrix[row][col] == null) {
                            Point emptyPoint = new Point(row, col);
                            emptyPoints.add(emptyPoint);
                        }
                    }
                }
            }
        }

        // Check if there are any empty points
        if (!emptyPoints.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(emptyPoints.size());
            return emptyPoints.get(randomIndex);
        } else {
            // If no empty points are available, return null or handle it as needed
            return null;
        }
    }




    public  void updateEntityOnGrid(Point oldLocation,Point LocationNewLocation){
        try {
            entityMatrix[LocationNewLocation.getRow()][LocationNewLocation.getColum() ] = entityMatrix[oldLocation.getRow() ][oldLocation.getColum() ];
            entityMatrix[oldLocation.getRow()][oldLocation.getColum() ] = null;
        }
        catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e.getMessage());
        }
    }

    public int getRoundRow(int row){
        if(row <0){
            return numOfRows-1+ row;
        }
        if(row > numOfRows-1){
            return row - numOfRows-1;
        }
        else{
            return row;
        }
    }

    public int getRoundCol(int col){
        if(col <0){
            return numOfColumns-1+ col;
        }
        if(col > numOfColumns-1){
            return col - numOfColumns-1;
        }
        else{
            return col;
        }
    }
    public int getNumOfRows() {
        return numOfRows;
    }

    public int getNumOfColumns() {
        return numOfColumns;
    }

    public EntityInstance[][] getEntityMatrix() {
        return entityMatrix;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public void setNumOfColumns(int numOfColumns) {
        this.numOfColumns = numOfColumns;
    }

    public void setEntityAt(int row, int column, EntityInstance entity) {
        if (row >= 0 && row < numOfRows && column >= 0 && column < numOfColumns) {
            entityMatrix[row][column] = entity;
        }
    }

    public EntityInstance getEntityAt(int row, int column) {
        if (row >= 0 && row < numOfRows && column >= 0 && column < numOfColumns) {
            return entityMatrix[row][column];
        }
        return null;
    }

    public EntityInstance findEntitysAround(Location source, EntityDefinition target, double depth){
        try{
        int colNumber =source.getLocation().getColum();
        int rowNumber = source.getLocation().getRow();
            depth = Math.round(depth);
        int counter =0;
        for(int i =0; i < numOfRows; i++){
            for(int j=0; j< numOfColumns;j++){

                if((Math.abs(i-rowNumber )== depth && Math.abs(j-colNumber) <= depth) ||
                Math.abs((j-colNumber) )== depth && Math.abs(i-rowNumber) <= depth){
                    if(entityMatrix[i][j] != null){
                         if(entityMatrix[i][j].getEntityDefinition().getName().equals(target.getName()) ){
                            return entityMatrix[i][j];
                            }
                        }
                    }
            }
        }
        return null;
    }   catch (ArrayIndexOutOfBoundsException | NullPointerException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
}