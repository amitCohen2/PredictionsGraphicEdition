
package SystemLogic.Location.imp;
public class Point {
    int row;
    int colum;

    public Point(int row, int colum){
        this.colum = colum;
        this.row = row;
    }

    public int getRow(){
        return this.row;
    }

    public int getColum(){
        return this.colum;
    }
}
