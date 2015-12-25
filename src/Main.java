import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import rtree.*;


public class Main {


    Random rand = new Random();
    String filename = null;
    RTree tree = null;

    List <Trip> trips3d = new ArrayList<>();
    int count = 0;
    public Main(){
//        try{
//            tree = new RTree( "DataSet/treefile4co_ordinate");
//        } catch (RTreeException e) {
//            e.printStackTrace();
//        }

       // rtreeFileMaker3d();
        //readAll2();
        //createCoOrdinateFile();
        //generateTreeFile();
        //readAll();
//        Ellipse ellipse = new Ellipse(10,0,-10,0,30);
//        try {
//            tree.rangeQuery(ellipse);
//        } catch (RTreeException e) {
//            e.printStackTrace();
//        }

        //readFile();

        testfor3d();
    }

    private void createCoOrdinateFile() {
        try {
            tree = new RTree(2, 4, "DataSet/treefile4co_ordinate");
        } catch (RTreeException e) {
            e.printStackTrace();
        }

        int counter=0, type;// nomatchX=0, nomatchY=0;
        double x1,y1;
        File file = new File("DataSet/Input.txt");
        try {
            //writer = new FileWriter(new File("DataSet/Input.txt"));
            Scanner input = new Scanner(file);
            while(input.hasNext()){
                counter = input.nextInt();
                x1 = input.nextDouble();
                y1 = input.nextDouble();
                int a = input.nextInt();
                int b = input.nextInt();
                int c = input.nextInt();
                type = input.nextInt();

                HyperPoint min = new HyperPoint(new double[]{x1,y1});
                HyperPoint max = new HyperPoint(new double[]{x1,y1});
                HyperBoundingBox rect = new HyperBoundingBox(min, max);
                rect.type = type;
                rect.a = a;
                rect.b = b;
                rect.c = c;
                //System.out.println(rect);
                try {
                    tree.insert(new Integer(counter), rect);
                } catch (RTreeException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
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
            HyperPoint a = new HyperPoint(new double[]{0,0});
            HyperPoint b = new HyperPoint(new double[]{1000,1000});
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


    public void readFile(){
        int counter=0, type, nomatchX=0, nomatchY=0;
        double x1,x2,y1,y2;
        int c=0;
        Random random = new Random();
        FileWriter writer = null;
        File file = new File("DataSet/CalPOIXY.txt");
        try {
            writer = new FileWriter(new File("DataSet/Input.txt"));
            Scanner input = new Scanner(file);
            while(input.hasNext()){
                counter = input.nextInt();
                x1 = input.nextDouble();
                x2 = input.nextDouble();
                y1 = input.nextDouble();
                y2 = input.nextDouble();
                type = input.nextInt();
                if(x1 != x2) nomatchX++;
                if(y1 != y2) nomatchY++;
                float a = (float)random.nextInt(50)/10 + (float).1;
                float b = (float)random.nextInt(100)/10 + (float).1;
                float d = (float)random.nextInt(50)/10 + (float).1;
                c++;
                // writer.write(x1+" "+y1+" "+a+" "+b+" "+type+"\n");
                writer.write(counter+" "+x1+" "+y1+" "+a+" "+b+" "+d+" "+type+"\n");
                // writer.write(x1+" "+y1+" "+type+"\n");
            }
            writer.close();
            System.out.println(counter+ " "+nomatchX+" "+nomatchY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    String fileName = "DataSet/3d/treeFile";

    RTree mytree[] = new RTree[63];
    int dimension = 3;
    int maxLoad = 4;
    HyperBoundingBox maxBox = null;
    public void rtreeFileMaker3d(){
        for(int i=0;i<63;i++){
            String name = fileName + i;
            try {
                mytree[i] = new RTree(dimension,maxLoad,name);
            } catch (RTreeException e) {
                e.printStackTrace();
            }
        }
        Scanner input = null;
        try {
            input = new Scanner(new File("DataSet/Input.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while(input.hasNext()){
            int counter = input.nextInt();
            double x = input.nextDouble();
            double y = input.nextDouble();
            double z = input.nextDouble();
            double w = input.nextDouble();
            double u = input.nextDouble();
            int type = input.nextInt();
            HyperPoint min = new HyperPoint(new double[]{z,w,u});
            HyperPoint max = new HyperPoint(new double[]{z,w,u});
            HyperBoundingBox rect = new HyperBoundingBox(min, max);
            rect.x = x;
            rect.y = y;
            rect.type = type;
            try {
                mytree[type].insert(new Integer(counter), rect);
            } catch (RTreeException e) {
                e.printStackTrace();
            }

        }



    }


    public void readAll2(){

        for(int i=0;i<63;i++){

            String name = fileName + i;
            try {
                mytree[i] = new RTree(name);
            } catch (RTreeException e) {
                e.printStackTrace();
            }

            skylineExecute3d(mytree[i]);

//            try {
//                HyperPoint a = new HyperPoint(new double[]{0,0,0});
//                HyperPoint b = new HyperPoint(new double[]{11,11,11});
//                HyperBoundingBox box = new HyperBoundingBox(a, b);
//                System.out.println(box);
//                Vector<HyperBoundingBox> v = mytree[i].retrieve(box);
////			for(int i=0;i<v.length;i++){
////				System.out.println(v);
////			}
//            } catch (RTreeException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            Scanner inp = new Scanner(System.in);
            inp.next();
        }


    }


    public List<HyperBoundingBox> skylineExecute3d(RTree tree){
       // System.out.println("Entered");
        double MAX_COORDINATE_VALUE_X = 10;
        double MAX_COORDINATE_VALUE_Y = 10;
        double MAX_COORDINATE_VALUE_Z = 10;
        HyperPoint origin = new HyperPoint(new double[]{0, 0, 0}) ;
        HyperPoint limit = new HyperPoint(new double[]{MAX_COORDINATE_VALUE_X, MAX_COORDINATE_VALUE_Y, MAX_COORDINATE_VALUE_Z}) ;
        HyperBoundingBox searchRegion = new HyperBoundingBox(origin, limit, false);
        List<HyperBoundingBox> skylineEntries = new ArrayList<>();
        HashSet <HyperBoundingBox> set = new HashSet<>();
        Stack<HyperPoint> todoList = new Stack<>();

        List<HyperBoundingBox> list = new ArrayList<>();

        try {
            HyperBoundingBox box1 = tree.boundedNNSearch(origin,searchRegion);
            list.add(box1);
        } catch (RTreeException e) {
            e.printStackTrace();
        }



        HyperBoundingBox firstNN = list.get(0);
        skylineEntries.add(firstNN);
        set.add(firstNN);
        //System.out.println(firstNN);
        todoList.push(new HyperPoint(new double[]{firstNN.getPMin().getCoord(0) - .0001, MAX_COORDINATE_VALUE_Y, MAX_COORDINATE_VALUE_Z}));
        todoList.push(new HyperPoint(new double[]{MAX_COORDINATE_VALUE_X, firstNN.getPMin().getCoord(1) - .0001, MAX_COORDINATE_VALUE_Z}));
        todoList.push(new HyperPoint(new double[]{MAX_COORDINATE_VALUE_X, MAX_COORDINATE_VALUE_Y , firstNN.getPMin().getCoord(2) - .0001}));


        while (!todoList.empty()) {
            HyperPoint p = todoList.pop();
            HyperBoundingBox box = new HyperBoundingBox(origin, p, false);
            List<HyperBoundingBox> nnl = new ArrayList<>();
            try {
                HyperBoundingBox temp = tree.boundedNNSearch(origin, box);
                if(temp!=null)
                    nnl.add(temp);
            } catch (RTreeException e) {
                e.printStackTrace();
            }
            if (!nnl.isEmpty()) {
                HyperBoundingBox nn = nnl.remove(0);
                if(!set.contains(nn)) {
                    skylineEntries.add(nn);
                    set.add(nn);
                }
//                System.out.println(nn);
                todoList.push(new HyperPoint(new double[]{nn.getPMin().getCoord(0) - .001, p.getCoord(1), p.getCoord(2)}));
                todoList.push(new HyperPoint(new double[]{p.getCoord(0), nn.getPMin().getCoord(1) -.001, p.getCoord(2)}));
                todoList.push(new HyperPoint(new double[]{p.getCoord(0), p.getCoord(1) , nn.getPMin().getCoord(2) -.001}));
            }
        }

//        skylineEntries.sort(new Comparator<HyperBoundingBox>() {
//            @Override
//            public int compare(HyperBoundingBox o1, HyperBoundingBox o2) {
//                if(o1.getPMin().getCoord(0) < o2.getPMin().getCoord(0))
//                    return -1;
//                else {
//                    if(o1.getPMin().getCoord(0) > o2.getPMin().getCoord(0))
//                        return 1;
//                    else
//                        return 0;
//                }
//            }
//        });

//        System.out.println("SkylineEntries: ");
//        for(int i=0;i<skylineEntries.size();i++){
//            System.out.println(skylineEntries.get(i));
//        }
//        System.out.println(skylineEntries.size());
        return skylineEntries;
    }

    double minTripDist;

    public void testfor3d(){
        HyperPoint origin = new HyperPoint(new double[]{50, 50, 0}) ;
//        List<HyperPoint> s = skyline(origin, tree);
//        System.out.println("All: ");
//        for(int i=0;i<s.size();i++){
//            System.out.println(s.get(i));
//        }
        long start = System.currentTimeMillis();
        for(int j=0;j<1;j++){

            trips3d.clear();
            ArrayList <Integer> seq = new ArrayList<>();
            int POItype = 3;
            System.out.println("Start");
            while(POItype != 0){
                int a = rand.nextInt(63);
                if(seq.contains(new Integer(a))){
                    continue;
                }
                seq.add(a);
                POItype--;
            }
            System.out.println("ENd "+ seq.size());
            for(int i=0;i<seq.size();i++){
                System.out.println(seq.get(i));
            }
//            seq.add(28);
//            seq.add(4);
//            seq.add(60);
//            seq.add(50);
            STPQ3d(seq, new Trip());
            //System.out.println("Result");
            for(int i=0;i<trips3d.size();i++){
                System.out.println(trips3d.get(i));
            }
        }
        long end = System.currentTimeMillis();
        long timeTaken = end - start;
        System.out.println(timeTaken);

        //skyline5d();
    }


    public void STPQ3d(ArrayList<Integer> seq, Trip t){
        if(seq.isEmpty()){
            trips3d.add(new Trip(t));
            return;
        }


        int type = seq.remove(0);
        String filename = "DataSet/3d/treeFile" + type;
        RTree tree = null;
        try {
            tree = new RTree(filename);
        } catch (RTreeException e) {
            e.printStackTrace();
        }

        List <HyperBoundingBox> list = skylineExecute3d(tree);
        for(int i=0;i<list.size();i++){
            HyperBoundingBox p = list.get(i);
            t.add(p);
            STPQ3d(seq,t);
            t.remove(p);
        }
        seq.add(0,type);
    }


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new Main();
    }



    private class Trip{
        //        HyperPoint start;
//        HyperPoint dest;
        List <HyperBoundingBox> seq;

        public Trip(){
            seq = new ArrayList<>();
        }

        public Trip(Trip t){
            seq = new ArrayList<>();
            //for(int i=0;i<t.seq.size();i++){
            seq.addAll(t.seq);
            //}
        }

        public void add(HyperBoundingBox o){
            seq.add(o);
        }

        public void remove(HyperBoundingBox o){
            seq.remove(o);
        }

        @Override
        public String toString() {
            String s = "";
            for(int i=0;i<seq.size();i++){
                s += seq.get(i) + " ";
            }
            return s;
        }

//        @Override
//        public boolean equals(Trip obj) {
//            return super.equals(obj);
//        }
    }

}
