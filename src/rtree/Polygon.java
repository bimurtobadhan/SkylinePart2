package rtree;

/**
 * Created by bimurto on 24-Dec-15.
 */
public class Polygon {
    double x[];
    double y[];
    public Polygon()
    {
        x = new double[4];
        y = new double[4];
    }
    public void set(double x[],double y[])
    {
        for(int i=0;i<4;i++)
        {
            this.x[i]=x[i];
            this.y[i]=y[i];
        }
    }
    public void copyPoints(double x[],double y[])
    {
        for(int i=0;i<4;i++)
        {
            this.x[i]=x[i];
            this.y[i]=y[i];
        }
    }
    public void copyPoints(HyperBoundingBox mbr)
    {

        double x1 = mbr.getPMin().getCoord(0);
        double y1 = mbr.getPMin().getCoord(1);
        double x2 = mbr.getPMax().getCoord(0);
        double y2 = mbr.getPMax().getCoord(1);
        x[0]=x1;
        y[0]=y1;
        x[1]=x1;
        y[1]=y2;
        x[2]=x2;
        y[2]=y2;
        x[3]=x2;
        y[3]=y1;

    }
    public boolean intersect(Polygon poly2)
    {
        //check poly1's edges as a seperator
        for(int i=0;i<4;i++)
        {
            int j = (i+1)%4;
            double x1=this.x[i];
            double y1=this.y[i];
            double x2=this.x[j];
            double y2=this.y[j];

            double normx = y2 - y1;
            double normy = x1 - x2;

            double minA = 0;
            double maxA = 0;

            for(int k=0;k<4;k++)
            {
                double projected = normx*this.x[k]+normy*this.y[k];
                if( k==0 || projected < minA )
                    minA = projected;
                if( k==0 || projected > maxA)
                    maxA = projected;
            }

            double minB = 0;
            double maxB = 0;
            for(int k=0;k<4;k++)
            {
                double projected = normx*poly2.x[k]+normy*poly2.y[k];
                if( k==0 || projected < minB )
                    minB = projected;
                if( k==0 || projected > maxB)
                    maxB = projected;
            }

            if (maxA < minB || maxB < minA)
                return false;
        }

        //check poly2's edges as a seperator
        for(int i=0;i<4;i++)
        {
            int j = (i+1)%4;
            double x1=poly2.x[i];
            double y1=poly2.y[i];
            double x2=poly2.x[j];
            double y2=poly2.y[j];

            double normx = y2 - y1;
            double normy = x1 - x2;

            double minA = 0;
            double maxA = 0;

            for(int k=0;k<4;k++)
            {
                double projected = normx*this.x[k]+normy*this.y[k];
                if( k==0 || projected < minA )
                    minA = projected;
                if( k==0 || projected > maxA)
                    maxA = projected;
            }

            double minB = 0;
            double maxB = 0;
            for(int k=0;k<4;k++)
            {
                double projected = normx*poly2.x[k]+normy*poly2.y[k];
                if( k==0 || projected < minB )
                    minB = projected;
                if( k==0 || projected > maxB)
                    maxB = projected;
            }

            if (maxA < minB || maxB < minA)
                return false;
        }
        return true;
    }
    public boolean intersect(HyperBoundingBox mbr)
    {
        Polygon poly2 = new Polygon();

        double x1 = mbr.getPMin().getCoord(0);
        double y1 = mbr.getPMin().getCoord(1);
        double x2 = mbr.getPMax().getCoord(0);
        double y2 = mbr.getPMax().getCoord(1);

        poly2.x[0]=x1;
        poly2.y[0]=y1;
        poly2.x[1]=x1;
        poly2.y[1]=y2;
        poly2.x[2]=x2;
        poly2.y[2]=y2;
        poly2.x[3]=x2;
        poly2.y[3]=y1;

        return intersect(poly2);
    }


}
