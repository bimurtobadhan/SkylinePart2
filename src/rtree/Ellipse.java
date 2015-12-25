package rtree;

/**
 * Created by bimurto on 24-Dec-15.
 */
public class Ellipse {
    double x1,y1,x2,y2; //foci

    double a; //half of major axis
    public Ellipse(){ a = Double.MAX_VALUE; }

    public Ellipse(float x1, float x2, float y1, float y2, float a)
    {
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        this.a=a;
    }

    public Ellipse(HyperPoint focus1, HyperPoint focus2, float a)
    {
        double x1 = focus1.getCoord(0);
        double y1 = focus1.getCoord(1);
        double x2 = focus2.getCoord(0);
        double y2 = focus2.getCoord(1);

        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        this.a=a;
    }

    public void set(float x1, float x2, float y1, float y2, float a)
    {
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        this.a=a;
    }
    public Polygon findBoundingBox()
    {
        Polygon box = new Polygon();
        double f = ( Math.sqrt( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) ) )/2.0;
        double b = Math.sqrt( a*a - f*f );
        double ux = (x2-x1)/(2.0*f);
        double uy = (y2-y1)/(2.0*f);
        double vx = uy;
        double vy = -ux;
        double ax,ay,bx,by,cx,cy,dx,dy;
        ax = x1 - (a-f)*ux - b*vx;
        ay = y1 - (a-f)*uy - b*vy;

        bx = ax + (2*b*vx);
        by = ay + (2*b*vy);

        cx = bx + (2*a*ux);
        cy = by + (2*a*uy);

        dx = cx - (2*b*vx);
        dy = cy - (2*b*vy);

        box.x[0]=ax;
        box.x[1]=bx;
        box.x[2]=cx;
        box.x[3]=dx;
        box.y[0]=ay;
        box.y[1]=by;
        box.y[2]=cy;
        box.y[3]=dy;

        return box;
    }

    public boolean intersect(HyperBoundingBox mbr)
    {
        Polygon poly = findBoundingBox();
        return poly.intersect(mbr);
    }

    public boolean inside(HyperPoint point)
    {
        double x = point.getCoord(0);
        double y = point.getCoord(1);
        double d1, d2;
        d1 = Math.sqrt( Math.pow(x1-x,2) + Math.pow(y1-y,2) );
        d2 = Math.sqrt( Math.pow(x2-x,2) + Math.pow(y2-y,2) );
        if( d1 + d2 <= 2*a ) return true;
        else return false;
    }

    public boolean inside(HyperBoundingBox mbr)
    {
        double x1 = mbr.getPMin().getCoord(0);
        double y1 = mbr.getPMin().getCoord(1);
        double x2 = mbr.getPMax().getCoord(0);
        double y2 = mbr.getPMax().getCoord(1);

        HyperPoint point;
        point = new HyperPoint(new double[]{x1,y1});
        if(!inside(point))return false;

        point = new HyperPoint(new double[]{x1,y2});
        if(!inside(point))return false;

        point = new HyperPoint(new double[]{x2,y2});
        if(!inside(point))return false;

        point = new HyperPoint(new double[]{x2,y1});
        if(!inside(point))return false;

        return true;

    }
}
