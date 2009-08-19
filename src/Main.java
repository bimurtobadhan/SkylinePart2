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

    double startX = 50;
    double startY = 50;
    double endX = 500;
    double endY = 500;
    int added = 0;
    int notAdded = 0;
    POIData data[] = new POIData[104770+1];
    List <Trip> trips3d = new ArrayList<>();
    List <Trip> trip3dpart2 = new ArrayList<>();
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

        initialize();

        testfor3d();



    }

    public void initialize(){
        int counter,type;
        double x1,y1;
        File file = new File("DataSet/Input.txt");
        try {
            //writer = new FileWriter(new File("DataSet/Input.txt"));
            Scanner input = new Scanner(file);
            while(input.hasNext()){
                counter = input.nextInt();
                x1 = input.nextDouble();
                y1 = input.nextDouble();
                double a = input.nextDouble();
                double b = input.nextDouble();
                double c = input.nextDouble();
                type = input.nextInt();
                int i = counter;
                //for(int i=0;i<=104770;i++){
                    data[i] = new POIData();
                    data[i].x = x1;
                    data[i].y = y1;
                    data[i].a = a;
                    data[i].b = b;
                    data[i].c = c;
                    data[i].type = type;
                //}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("DOne");
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
//            System.out.println(skylineEntries.get(i) +" "+skylineEntries.get(i).getData() );
//        }
//        System.out.println(skylineEntries.size());
        return skylineEntries;
    }

    //double minTripDist;

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
            //STPQ3d(seq, new Trip());
            STPQ3d2(seq, new Trip());
            //System.out.println("Result");
            System.out.println("Size: "+ trips3d.size());
//            for(int i=0;i<trips3d.size();i++){
//                System.out.println(trips3d.get(i));
//            }

            System.out.println("MINTRIPDIST: " + minTripDist);

            Ellipse ellipse = new Ellipse(50,500, 50, 500, (float) minTripDist/2);
            System.out.println("Started Part2");
            STPQ3dPart2(ellipse,seq);
        }
        System.out.println("ResultCOunt: "+ (trips3d.size()+trip3dpart2.size()));
        long end = System.currentTimeMillis();
        long timeTaken = end - start;
        System.out.println(timeTaken);

        //skyline5d();
    }

    private void STPQ3dPart2(Ellipse ellipse, ArrayList <Integer> seq ) {
        RTree cordTree = null;
        Vector<HyperBoundingBox> v = null;
        trip3dpart2.clear();
        try {
            cordTree = new RTree("DataSet/treefile4co_ordinate");
            v = cordTree.rangeQuery(ellipse);
        } catch (RTreeException e) {
            e.printStackTrace();
        }

        List<HyperBoundingBox> list [] = new ArrayList[seq.size()];
        for(int i=0;i<seq.size();i++){
            list[i] = new ArrayList<>();
        }

        for(int i=0;i<v.size();i++){
            int d = v.get(i).getData();
            int type = data[d].type;
            if(seq.contains(type)){
                int index = seq.indexOf(type);
                list[index].add(v.get(i));
            }
        }

        for(int i=0;i<seq.size();i++){
            System.out.println(list[i].size());
        }

        STPQ2Part2_aux(list, seq, new Trip(), 0);
        System.out.println(trip3dpart2.size());
    }

    public void STPQ2Part2_aux(List<HyperBoundingBox> skylist[], ArrayList<Integer> seq, Trip t, int counter){
        if(seq.isEmpty()){
            double x = t.calculateDistance(startX, startY, endX, endY);
            //System.out.println(x + " "+ minTripDist);
            if(x < minTripDist){
                added++;
                System.out.println("Added "+added);
                trip3dpart2.add(new Trip(t));
            }
            notAdded++;
            System.out.println("NOt Added "+notAdded);
            return;
        }

        int type = seq.remove(0);
//        String filename = "DataSet/3d/treeFile" + type;
//        RTree tree = null;
//        try {
//            tree = new RTree(filename);
//        } catch (RTreeException e) {
//            e.printStackTrace();
//        }

        List <HyperBoundingBox> list = skylist[counter];
        //System.out.println(list.size());
        for(int i=0;i<list.size();i++){
            int p = list.get(i).getData();
            t.add(p);
            STPQ2Part2_aux(skylist, seq, t, counter+1);
            t.remove(p);
        }
        seq.add(0, type);
    }


    public void STPQ3d2(ArrayList<Integer> seq, Trip t){
        List<HyperBoundingBox> list [] = new ArrayList[seq.size()];
        for(int i=0;i<seq.size();i++){
            int type = seq.get(i);
            String filename = "DataSet/3d/treeFile" + type;
            RTree tree = null;
            try {
                tree = new RTree(filename);
            } catch (RTreeException e) {
                e.printStackTrace();
            }

            list[i] = skylineExecute3d(tree);
            System.out.println("list "+ i +" "+ list[i].size());
        }

        STPQ2_aux(list,seq,t,0);
    }

    double minTripDist = Double.MAX_VALUE;
    public void STPQ2_aux(List<HyperBoundingBox> skylist[], ArrayList<Integer> seq, Trip t, int counter){
        if(seq.isEmpty()){
            trips3d.add(new Trip(t));
            double x = t.calculateDistance(startX, startY, endX, endY);
            //System.out.println(x + " "+ minTripDist);
            if(x < minTripDist){
                minTripDist = x;
            }
            return;
        }

        int type = seq.remove(0);
//        String filename = "DataSet/3d/treeFile" + type;
//        RTree tree = null;
//        try {
//            tree = new RTree(filename);
//        } catch (RTreeException e) {
//            e.printStackTrace();
//        }

        List <HyperBoundingBox> list = skylist[counter];
        //System.out.println(list.size());
        for(int i=0;i<list.size();i++){
            int p = list.get(i).getData();
            t.add(p);
            STPQ2_aux(skylist, seq, t, counter+1);
            t.remove(p);
        }
        seq.add(0,type);
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
        //System.out.println(list.size());
        for(int i=0;i<list.size();i++){
            int p = list.get(i).getData();
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
        List <Integer> seq;

        public Trip(){
            seq = new ArrayList<>();
        }

        public Trip(Trip t){
            seq = new ArrayList<>();
            //for(int i=0;i<t.seq.size();i++){
            seq.addAll(t.seq);
            //}
        }

        public double calculateDistance(double x1, double y1, double x2, double y2){
            double x = (x1 - data[seq.get(0)].x) * (x1 - data[seq.get(0)].x);
            double y = (y1 - data[seq.get(0)].y) * (y1 - data[seq.get(0)].y);
            double sum = Math.sqrt(x + y);
            for(int i=1;i<seq.size();i++){
                x = Math.pow((data[seq.get(i)].x - data[seq.get(i-1)].x),2) ;
                y = Math.pow((data[seq.get(i)].y - data[seq.get(i-1)].y),2) ;
                sum += Math.sqrt(x + y);
            }
            int last = seq.size()-1;
            x = Math.pow((data[seq.get(last)].x - x2),2) ;
            y = Math.pow((data[seq.get(last)].y - y2), 2) ;
            sum += Math.sqrt(x + y);

            return sum;
        }

        public void add(Integer o){
            seq.add(o);
        }

        public void remove(Integer o){
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
