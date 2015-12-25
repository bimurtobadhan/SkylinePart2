import java.util.*;
import java.util.List;

import rtree.*;


public class Main {


    Random rand = new Random();
    String filename = null;
    RTree tree = null;
    int count = 0;
    public Main(){
        try {
            tree = new RTree("treefile");
        } catch (RTreeException e) {
            e.printStackTrace();
        }

        //generateTreeFile();
        //readAll();
        Ellipse ellipse = new Ellipse(10,0,-10,0,30);
        try {
            tree.rangeQuery(ellipse);
        } catch (RTreeException e) {
            e.printStackTrace();
        }
    }



    public void generateTreeFile(){
        generateTreeFileHelper(1,1);    //1st quadrant
        generateTreeFileHelper(-1,1);   //2nd quadrant
        generateTreeFileHelper(-1,-1);  //3rd quadrant
        generateTreeFileHelper(1,-1);   //4th quadrant
    }

    private void generateTreeFileHelper(int a,int b){
        Random random = new Random();
        for(int i=0;i<100;i++){
            double x = random.nextDouble()*100*a;
            double y = random.nextDouble()*100*b;
            //double z = random.nextDouble()*10;
            HyperPoint min = new HyperPoint(new double[]{x,y});
            HyperPoint max = new HyperPoint(new double[]{x,y});
            HyperBoundingBox rect = new HyperBoundingBox(min, max);
            //System.out.println(rect);
            try {
                tree.insert(new Integer(i), rect);
            } catch (RTreeException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void readAll(){
        try {
            HyperPoint a = new HyperPoint(new double[]{-100,-100});
            HyperPoint b = new HyperPoint(new double[]{100,100});
            HyperBoundingBox box = new HyperBoundingBox(a, b);
            System.out.println(box);
            Vector<HyperBoundingBox> v = tree.retrieve(box);
//			for(int i=0;i<v.length;i++){
//				System.out.println(v);
//			}
        } catch (RTreeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //Main m =
        new Main();
        //new RTreeFileMaker3d();
//        HyperPoint o = new HyperPoint(new double[]{500,500,0});
//        HyperPoint d = new HyperPoint(new double[]{700,700,0});
//        Skyline s = new Skyline(3,o,d,"DataSet/Algo1/3d/RTREEFIL3D0");
//        s.skylineExecute();
    }

}
