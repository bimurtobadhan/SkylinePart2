package rtree;

/**
 * Created by bimurto on 12/25/2015.
 */
public class POIData {
    public double x,y, a,b,c,d,type;

    public POIData(){

    }

    @Override
    public String toString() {
        return "x: "+ x +"y: "+ y+ "a: "+ a + "b: "+ b+ "c: "+ c + "d : "+ d +"type: "+ type;
    }
}
