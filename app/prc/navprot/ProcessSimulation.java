/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prc.navprot;

import java.io.*;

import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.*;

import com.avaje.ebean.Ebean;
import models.Task;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.biojava.bio.structure.*;
import org.biojava.bio.structure.io.CAConverter;
import org.biojava.bio.structure.io.PDBFileParser;
import org.jmat.data.Matrix;
import prc.navprot.utils.AlgebraicVector;
import prc.navprot.utils.Point3D;
import prc.navprot.utils.Robot3D;
import prc.navprot.utils.RobotList;

/**
 *
 * @author Serdar
 */
public class ProcessSimulation {

    public RobotList robotList = new RobotList(); //List that holds all robots
    public boolean isLoop = false; // For sequential simulation, not valid for this case
    public boolean animate = false; // For simulation loop
    public int countProcess = 0;
    public int k; // k parameter of artificial potential function
    public Inner test; // Thread for simulation
    public double sphericalError = 0.001; // Allowed angle error to finish the simulation
    public double linkLength = 3.8; // Link length for linked simulation
    public double hStatic = 0.1; // Step size
    public double errorStatic = 0.75; //Error
    public int numOfCollisions = 0; //Num of collisions during simulation
    public double prevRadius; // For radius checking
    public Vector <Double> phiVect,thetaVect,phiFunc,cartesian,avgLinkDist;
    public double totalRmsd;
    public double rMSD; // Current rmsd, for checking if simulation is complete
    String resultsFileName; // Input coordinates file
    String jobDirectory; // Directory where torsion and result files are
    Atom[] initialCA; // Alpha-carbon atoms
    Structure resultPDB; // Structure object for writing results to PDB file
    BufferedWriter out;
    Calendar beginTime; // Start time of simulation
    Calendar endTime; // End time of simulation
    String timePassed; // Simulation time
    Date startTime; // Simulation start time
    Date stopTime; // Simulation stop time
    Task ownTask; // Data base entity of the job



    public void startSimulation(){

        double[] newPositions;
        double[] spPosDum;
        AlgebraicVector newP = new AlgebraicVector(0);
        AlgebraicVector spPos = new AlgebraicVector(0);
        boolean isCol = true;
        boolean isNoise = false;

        while (animate == true) { // Change this while to if, if you want to use Thread
            Task thisTask = Ebean.find(Task.class, ownTask.id);
            if(thisTask.isCancelled){
                animate = false; // Stop simulation
                NewOdeSolverForTorsion.removeAll();
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            System.out.println("Process Si.." + countProcess + "\nk= " + k);
            isCol = NewOdeSolverForTorsion.odeSolverCombo(robotList, k, linkLength, isNoise);
//            avgLinkDist.add(NewOdeSolverForTorsion.sumOfLinkDistances);
            double rmsd = RMSDeviationAccToNative();
            totalRmsd += rmsd;
            rMSD = rmsd;
            phiFunc.add(rmsd);
            countProcess++;
            System.out.println("Average rmsd up to now: " + (totalRmsd / countProcess));
            writeToFile(robotList);
//            double sumbeReceived = 0, sumbeReceivedC = 0;
//            for (int n = 3; n <= 2 * robotList.getSize(); n++) {
//                if (n != 4) {
//                    sumbeReceived += NewOdeSolverForTorsion.distanceWillBeReceivedTorsion[n];
//                }
//            }
//            for (int n = 2; n < robotList.getSize(); n++) {
//                sumbeReceivedC += NewOdeSolverForTorsion.distanceWillBeReceivedCartesian[n];
//            }
//            System.out.println("Total distance will be received in cartesian: " + sumbeReceivedC);
//            System.out.println("Total distance will be received in torsion angles: " + sumbeReceived);

            spPosDum = new double[2 * robotList.getSize() + 1];
            spPos = new AlgebraicVector(2 * robotList.getSize() + 1);
            newPositions = new double[3 * robotList.getSize()];
            newP = new AlgebraicVector(3 * robotList.getSize());

            for (int i = 1; i < robotList.getSize() - 1; i++) {
                spPosDum[2 * i + 1] = ((Robot3D) (robotList.getRobotList().elementAt(i))).getTheta();
                spPosDum[2 * i + 2] = ((Robot3D) (robotList.getRobotList().elementAt(i))).getPhi();
            }
            for (int i = 0; i < 3 * robotList.getSize(); i++) {
                newPositions[i] = ((robotList.getNormalPositions()).data)[i];
            }
            newP.set(newPositions);
            spPos.set(spPosDum);
//        } turn this on if you want to use Thread

//        boolean testForSphericalNear = isSphericalNear();
        addModel(robotList); // Add snapshot to result PDB as model

        if(robotList.getNormalGoalPositions().equalsCartesian( newP, errorStatic) || rMSD < 0.50){
//            canBegin(); Turn this on if you want to use Thread
            animate = false; // Turn this off if you want to use Thread
//            showTime(testForSphericalNear);
            NewOdeSolverForTorsion.removeAll(); // This is very important! If you don't write this, next run will fail!
            closeFile();
            try {
                FileUtils.writeStringToFile(new File(jobDirectory, "result.pdb"), resultPDB.toPDB());
            } catch( IOException e ){
                e.printStackTrace();
            }
            endTime = Calendar.getInstance();
            ownTask.setEndTime(endTime.getTime());
            ownTask.save();
            System.out.println("Finished robot simulation.");
        }

    }// Turn this off if you want to use Thread
    }
    }
    /**
     * Starts the simulation by taking the torsion file name
     * @param fileName
     */
    public void start(String fileName){

        File selectedFile = new File(jobDirectory,fileName);
        takeCompleteTorsion(selectedFile);
        resultPDB = new StructureImpl();
        resultPDB.setNmr(true);
        phiFunc = new Vector();
        beginTime = Calendar.getInstance();
        ownTask.setStartTime(beginTime.getTime());
        ownTask.save();
        openFile();
        NewOdeSolverForTorsion.removeAll(); // Reset all variables before calculation, important!
        NewOdeSolverForTorsion.setXYZCoordinates(robotList);
        NewOdeSolverForTorsion.setGoalXYZCoordinates(robotList);
        setK();
//        canBegin(); Turn this on if you want to use Thread
        animate = true;
        startSimulation(); // Turn this off if you want to use Thread
    }
    /**
     * For controling if simulation has ended
     */
    private void canBegin() {

        if (animate == false) {

            animate = true;
            test = new Inner("Process");
            test.start();

        } else {
            animate = false;
        }
    }
    /**
     *This thread is used for simulation purposes
     */
    private class Inner extends Thread {
        Inner(String name) {
            super(name);
        }
        @Override
        public void run() {
            while (animate) {
                startSimulation();
            }
        }
    }

    /**
     * Set database entity of the job
     * @param task
     */
    public void setTask(Task task){
        this.ownTask = task;
    }
    /**
     * Sets the file name which results are written
     * @param name
     */
    public void setResultFileName(String name){

        this.resultsFileName = name;

    }

    /**
     * Sets job directory for writing and reading the files
     * @param jobDirName
     */
    public void setJobDirectory(String jobDirName){

        this.jobDirectory = jobDirName;

    }

    /**
     * Sets alpha-carbon array, for writing snapshots of simulation to pdb file
     * @param pdbName
     */
    public void setCAs(String pdbName, String chainName) {

        PDBFileParser parser = new PDBFileParser();
        Atom[] caArray;
        try {
            caArray = StructureTools.getAtomCAArray(parser.parsePDBFile(new BufferedReader(new FileReader(new File(jobDirectory, pdbName)))).getChainByPDB(chainName));
            this.initialCA = caArray;
        } catch(IOException|StructureException e){
            e.printStackTrace();
        }
    }

    /**
     * Adds current robotlist coordinates to result PDB
     * @param robotlist
     */
    private void addModel( RobotList robotlist ){

        Atom[] newCAarray = initialCA.clone();
        Chain newChain = new ChainImpl();
        List<Chain> chainList = new ArrayList<Chain>();
        for(int i = 0; i< robotlist.getSize(); i++){
            double newX = ((Robot3D)robotlist.getRobotList().elementAt(i)).getPosition().data[0];
            double newY = ((Robot3D)robotlist.getRobotList().elementAt(i)).getPosition().data[1];
            double newZ = ((Robot3D)robotlist.getRobotList().elementAt(i)).getPosition().data[2];
            newCAarray[i].setX(newX);
            newCAarray[i].setY(newY);
            newCAarray[i].setZ(newZ);
        }
        Group[] group = StructureTools.cloneGroups(newCAarray);
        for(int i = 0; i<group.length; i++ ){
            newChain.addGroup(group[i]);
        }
        chainList.add(CAConverter.getCAOnly(newChain));
        resultPDB.addModel(chainList);
    }

    /**
     * In the following methods output data for simulation is created and 
     * robot positions saved in every step.
     */
    public void openFile(){
        try{
//            out = new BufferedWriter(new FileWriter(new File(jobDirectory,"results.txt")));
//            java.util.Date date = Calendar.getInstance().getTime();
//            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy-hhmmss");
//            String resultFileName = selectedFileName;
            out = new BufferedWriter(new FileWriter(new File(jobDirectory, resultsFileName)));
            out.flush();
        }catch(Exception e){
            System.out.println("Error in opening the file!");
        }
    }

    public void closeFile(){
        try{
            out.write("END OF FILE");
            out.newLine();
//            out.write( timePassed );
            out.newLine();
            out.write( "Number of Collisions = " + numOfCollisions );
            out.newLine();
            out.close();
        }catch( Exception e ){
            System.out.println("Error closing the file!");
        }
    }

    public void writeToFile( RobotList f ){

        double[] positions = ((AlgebraicVector)f.getNormalPositions()).data;
        double[] goalPositions = ((AlgebraicVector)f.getNormalGoalPositions()).data;
        double r;
        try{
            for( int i = 0; i < f.getSize() ; i++ ){
                r  = ((Robot3D)f.getRobotList().getElementAt(i)).getRadius();
                out.write( positions[i*3+0] + " " + positions[i*3+1] + " " + positions[i*3+2] + " "
                        + goalPositions[i*3+0] + " " + goalPositions[i*3+1]
                        + " " + goalPositions[i*3+2] + " " +r
                        + " linkLength " + ((Robot3D)f.getRobotList().getElementAt(i)).getLinkDistance()
                        +" hStatic " + hStatic
                        + " errorStatic " + errorStatic
                        + " SphericalError " + sphericalError
                        + " k " + k
                        + " ProcessCount " + countProcess);

                if((i+1)==f.getSize())
                    out.write(" Finished Step!");
                out.newLine();
            }

        }catch(Exception e){
            System.out.println("Error in writeToFile!");
        }
    }

    /**
     *This method is used to replace robots according to superposition
     */
    public void moveCentroidToOriginWithNative(Point3D meanR,Point3D meanN,RobotList next){

        for(int y=0;y<next.getRobotList().size();y++){
            ((Robot3D)next.getRobotList().elementAt(y)).getPosition().data[0]-=meanR.data[0];
            ((Robot3D)next.getRobotList().elementAt(y)).getPosition().data[1]-=meanR.data[1];
            ((Robot3D)next.getRobotList().elementAt(y)).getPosition().data[2]-=meanR.data[2];
            ((Robot3D)next.getRobotList().elementAt(y)).getGoalPosition().data[0]-=meanN.data[0];
            ((Robot3D)next.getRobotList().elementAt(y)).getGoalPosition().data[1]-=meanN.data[1];
            ((Robot3D)next.getRobotList().elementAt(y)).getGoalPosition().data[2]-=meanN.data[2];
        }
    }

    /**
     *This method is used to duplicate robotList and to perturb each one with different rates of change seperately.
     */
    public RobotList copyRobotList(){

        RobotList list=new RobotList();
        int c=0;
        while(c<robotList.getSize()){
            Robot3D dummy=new Robot3D();
            dummy=((Robot3D)robotList.getRobotList().elementAt(c)).cloneRobot();
            list.addRobot(dummy);
            c++;
        }
        return list;
    }

    /**
     *This method is used to calculate root mean square valuation
     */
    public double RMSDeviationAccToNative(){

        RobotList dummyA=copyRobotList();
        int size=dummyA.getRobotList().getSize();
        org.jmat.data.Matrix X=new org.jmat.data.Matrix(3,size);
        org.jmat.data.Matrix Y=new org.jmat.data.Matrix(3,size);
        org.jmat.data.Matrix C=new org.jmat.data.Matrix(3,3);
        org.jmat.data.Matrix V=new org.jmat.data.Matrix(3,3);
        org.jmat.data.Matrix TransformM=new org.jmat.data.Matrix(3,3);
        org.jmat.data.Matrix S=new org.jmat.data.Matrix(3,3);
        org.jmat.data.Matrix U=new org.jmat.data.Matrix(3,3);
        org.jmat.data.Matrix D=new org.jmat.data.Matrix(3,3);

        Point3D meanA=dummyA.getCenterOfGravity();
        Point3D meanN=dummyA.getCenterOfGravityOfNative();

        moveCentroidToOriginWithNative(meanA,meanN,dummyA);

        for(int i=0;i<size;i++){
            X.set(0, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getPosition().data[0]);
            X.set(1, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getPosition().data[1]);
            X.set(2, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getPosition().data[2]);
            Y.set(0, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getGoalPosition().data[0]);
            Y.set(1, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getGoalPosition().data[1]);
            Y.set(2, i,((Robot3D)dummyA.getRobotList().elementAt(i)).getGoalPosition().data[2]);
        }
        C=(Matrix) X.times(Y.transpose());
        double sign=Math.signum(C.determinant());
        U=(Matrix) C.svd().getU();
        V=(Matrix) C.svd().getV();

        for(int j=0;j<3;j++){
            for(int s=0;s<3;s++){
                if(j==s){
                    D.set(j, s, 1.0);
                }
                else {
                    D.set(j, s, 0);
                }
            }
        }
        D.set(2, 2, sign);

        TransformM=(Matrix) V.times(D.times(U.transpose()));

        X=(Matrix) TransformM.times(X);
        double sum=0;
        for(int i=0;i<size;i++){
            sum+=Math.pow((X.get(0, i))-(Y.get(0, i)),2)+Math.pow((X.get(1, i))-(Y.get(1, i)),2)+
                    Math.pow((X.get(2, i))-(Y.get(2, i)),2);
        }
        sum/=size;
        double RMSValue=Math.sqrt(sum);
        System.out.println("\nrmsd:"+RMSValue);

        return RMSValue;

    }
    /**
     *This method is used to get input for linked simulation
     */
    public void takeCompleteTorsion( File selectedFile ){
        try{
            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            String str;
            Robot3D toAdd;
            String[] values;
            double[] input = new double[9];
            double[] inputText = new double[5];
            Point3D initPos, goalPos;
            double radius = 0.8, theta = Math.PI, phi = 0.0, goalTheta = Math.PI, goalPhi = 0.0;

            while ((str = dis.readLine()) != null) {

                toAdd = new Robot3D();
                System.out.println( str );
                values = str.split( " " );
                toAdd = new Robot3D();
                if( values.length == 6 || values.length == 4){

                    if( values.length == 6 ){
                        if( !isLoop ){
                            input[7] = 0.8;
                            prevRadius = input[7];
                        }else{
                            input[7] = prevRadius;
                        }


                        input[1] = Double.parseDouble( values[0] );
                        input[2] = Double.parseDouble( values[1] );
                        input[3] = Double.parseDouble( values[2] );
                        input[4] = Double.parseDouble( values[3] );
                        input[5] = Double.parseDouble( values[4] );
                        input[6] = Double.parseDouble( values[5] );

                    }
                    if( values.length == 4 ){
                        inputText[1] = Double.parseDouble( values[0] );
                        theta = inputText[1];
                        inputText[2] = Double.parseDouble( values[1] );
                        phi = inputText[2];
                        inputText[3] = Double.parseDouble( values[2] );
                        goalTheta = inputText[3];
                        inputText[4] = Double.parseDouble( values[3] );
                        goalPhi = inputText[4];

                    }

                    initPos = new Point3D( input[1], input[2], input[3] );
                    goalPos = new Point3D( input[4], input[5], input[6] );
                    radius = input[7];

                    toAdd.setRadius( radius );
                    toAdd.setPosition( initPos );
                    toAdd.setGoalPosition( goalPos );
                    toAdd.setTheta( theta );
                    toAdd.setGoalTheta( goalTheta );
                    toAdd.setPhi( phi );
                    toAdd.setGoalPhi( goalPhi );
                    toAdd.setLinkDistance(linkLength);
                    robotList.addRobot( toAdd );
                }
            }
            dis.close();
        }catch( IOException e ){
            e.printStackTrace();
        }

        if( robotList.getSize() == 0 ){
            takePartialTorsion(selectedFile);
        }

    }

    /**
     *This method is used to get input for partial linked simulation
     */
    public void takePartialTorsion( File selectedFile ){
        try{
            FileInputStream fis = new FileInputStream(selectedFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            String str;
            Robot3D toAdd;
            String[] values;
            double[] input = new double[9];
            double[] inputText = new double[5];
            Point3D initPos, goalPos;
            double radius = 0.8, theta = 0.0, phi = 0.0, goalTheta = 0.0, goalPhi = 0.0;

            while ((str = dis.readLine()) != null) {

                toAdd = new Robot3D();
                System.out.println( str );
                values = str.split( " " );


                if( values.length == 7 || values.length == 5 ){

                    if( values.length == 7 ){
                        input[7] = 0.8; // Carbon radius
                        input[1] = Double.parseDouble( values[0] );
                        input[2] = Double.parseDouble( values[1] );
                        input[3] = Double.parseDouble( values[2] );
                        input[4] = Double.parseDouble( values[3] );
                        input[5] = Double.parseDouble( values[4] );
                        input[6] = Double.parseDouble( values[5] );
                        input[8] = Double.parseDouble( values[6] );

                    }
                    if( values.length == 5 ){
                        inputText[1] = Double.parseDouble( values[0] );
                        theta = inputText[1];
                        inputText[2] = Double.parseDouble( values[1] );
                        phi = inputText[2];
                        inputText[3] = Double.parseDouble( values[2] );
                        goalTheta = inputText[3];
                        inputText[4] = Double.parseDouble( values[3] );
                        goalPhi = inputText[4];
                        input[8] = Double.parseDouble( values[4] );
                    }

                    initPos = new Point3D( input[1], input[2], input[3] );
                    goalPos = new Point3D( input[4], input[5], input[6] );
                    radius = input[7];
                    toAdd.setSeeRad( input[8] );
                    toAdd.setRadius( radius );
                    toAdd.setPosition( initPos );

                    toAdd.setGoalPosition( goalPos );
                    toAdd.setTheta( theta );
                    toAdd.setGoalTheta( goalTheta );
                    toAdd.setPhi( phi );
                    toAdd.setGoalPhi( goalPhi );
                    robotList.addRobot( toAdd );
                }
            }
            dis.close();
        }catch( IOException e ){
            e.printStackTrace();
        }
    }
    /**
     *This method is used to check if simulation reached allowable final angle configuration
     */

    /**
     * Sets k parameter for simulation
     */
    public void setK(){

        int size = robotList.getSize();
        k = Math.round(size*(size+1)/2)+2;
        if( k%2 != 0 )
            k++;

    }
    public boolean isSphericalNear(){

        boolean testForSphericalNear = true;
        double sumThetaError = 0.0;
        double sumPhiError = 0.0;
        double testPhi, testTheta, testGoalPhi, testGoalTheta;
        for( int i = 1; i < robotList.getSize()-1; i++ ){
            testPhi = ((Robot3D)(robotList.getRobotList().elementAt(i))).getPhi();
            testTheta = ((Robot3D)(robotList.getRobotList().elementAt(i))).getTheta();
            testGoalTheta = ((Robot3D)(robotList.getRobotList().elementAt(i))).getGoalTheta();
            testGoalPhi = ((Robot3D)(robotList.getRobotList().elementAt(i))).getGoalPhi();
            double thetaerr=(Math.abs( ((int)(100000*(Math.abs(testTheta-testGoalTheta))) % (int)(100000*2*Math.PI)) / 100000.0 ));
            double pierr=(Math.abs( ((int)(100000*(Math.abs(testPhi-testGoalPhi))) % (int)(100000*2*Math.PI)) / 100000.0 ));
            sumThetaError += thetaerr;
            sumPhiError += pierr;
            if( thetaerr > sphericalError ||
                    pierr> sphericalError  ){
                testForSphericalNear = false;
            }
        }
        System.out.println("Mean Dif.Theta = " + sumThetaError/(robotList.getSize()-1)+"  "+ "Mean Dif.Phi = " + sumPhiError/(robotList.getSize()-2) );

        return testForSphericalNear;

    }

    /**
     *This method is used to show simulation time
     */
    public void showTime( boolean testForSphericalNear ){
        endTime = Calendar.getInstance();
        int beginTimeInH = beginTime.get(Calendar.HOUR_OF_DAY);
        int beginTimeInMin = beginTime.get(Calendar.MINUTE);
        int beginTimeInS = beginTime.get(Calendar.SECOND);
        int endTimeInH = endTime.get(Calendar.HOUR_OF_DAY);
        int endTimeInMin = endTime.get(Calendar.MINUTE);
        int endTimeInS = endTime.get(Calendar.SECOND);

        System.out.println(" Begin = " + beginTimeInH + ":" + beginTimeInMin + ":"+ beginTimeInS );
        System.out.println(" End = " + endTimeInH + ":" + endTimeInMin + ":"+ endTimeInS );
        long timeInMs = endTime.getTimeInMillis() - beginTime.getTimeInMillis();
        long passHour = timeInMs / (60*60*1000);
        long passMin = (timeInMs - 60*60*1000*passHour) / (60*1000);
        long passSec = (timeInMs  - 60*60*1000*passHour - 60*1000*passMin)/ (1000);
        timePassed = " Passed Time = " + passHour + " hours "+passMin+" minutes "+ passSec + " seconds " + "  testForSphericalNear = " + testForSphericalNear;
        System.out.println( timePassed );
    }
}
