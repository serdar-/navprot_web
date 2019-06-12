/**
 ************************************************************************
 ********************** Composed by: Bar�� ULUTA� ***********************
 ********************** RobotList.java **********************************
 ************************************************************************
 */

package prc.navprot.utils;

import javax.swing.DefaultListModel;

public class RobotList {
    private DefaultListModel robotList = new DefaultListModel();
    private Point3D transitionVector;
    private Point3D rotationVector;
    
    
    /** Creates a new instance of RobotList */
    public RobotList() {
    }
    /** Gets the size of the RobotList */
    public int getSize(){
        return robotList.getSize();
    }
    /** Gets the transition parameters the RobotList */
    public Point3D getTransitionVector(){
        return transitionVector;
    }
    /** Gets the transition parameters of the RobotList */
    public Point3D setTransitionVector(Point3D set){
        return transitionVector=set;
    }
    /** Gets the transition parameters the RobotList */
    public Point3D getRotationVector(){
        return rotationVector;
    }
    /** Gets the transition parameters of the RobotList */
    public Point3D setRotationVector(Point3D set){
        return rotationVector=set;
    }
    /** Adds a new Robot3D to the RobotList */
    public void addRobot( Robot3D newRobot ){
        robotList.addElement( newRobot );        
    }
    /** Gets the robotList of the RobotList*/
    public DefaultListModel getRobotList(){
        return robotList;
    }
    /** Gets the goal position of the Robots as
     * augmented vector with all x es stored then all y s and then all z s
     */
    public AlgebraicVector getGoalPositions(){
        AlgebraicVector goalVect = new AlgebraicVector( 3 * this.getSize() );
        
        for( int i = 0; i < this.getSize() ; i++){
           
             
            Point3D dummy =(((Robot3D)(robotList.getElementAt( i ))).getGoalPosition());
            //System.out.println("dummy "+ dummy.data[0] +" " +dummy.data[1]+" "+dummy.data[2]);
            goalVect.data[ i ] = dummy.data[0];
            
            goalVect.data[ this.getSize() + i ] = dummy.data[1];
            
            goalVect.data[ 2 * this.getSize() + i ] = dummy.data[2];
        } 
        return goalVect;
        
    }
    /** Gets the positions of the of all robots by augmented vector
     * with first all x es then y s and lastly all z s
     */
    public AlgebraicVector getPositions(){
        AlgebraicVector posVect = new AlgebraicVector( 3 * this.getSize() );
        
        for( int i = 0; i < this.getSize(); i++){
            Point3D dummy =((Robot3D)(robotList.getElementAt( i ))).getPosition();
            
            posVect.data[ i ] = dummy.data[0];
            
            posVect.data[ this.getSize() + i ] = dummy.data[1];
            
            posVect.data[ 2 * this.getSize() + i ] = dummy.data[2];
            }  
        return posVect;
        
    }
    /** Gets the goal angles of the Robots as
     * augmented vector with all phis and thetas stored.
     */
    public AlgebraicVector getGoalAngles(){
        AlgebraicVector prevPos = new AlgebraicVector( 2*this.getSize()+1 );
        for( int i = 1; i < this.getSize()-1; i++ ){
                prevPos.data[ 2*i + 1 ] = ((Robot3D)(this.getRobotList().elementAt(i))).getGoalTheta();
                prevPos.data[ 2*i + 2 ] = ((Robot3D)(this.getRobotList().elementAt(i))).getGoalPhi();
        }
        return prevPos;
    }
    /** Get the radiuses of all vectors as an augmented vector */
    public AlgebraicVector getRadius(){
        AlgebraicVector radVect = new AlgebraicVector( this.getSize());
        for( int i = 0; i < (radVect.getDimension()); i++){
            radVect.data[i] = ((Robot3D)(robotList.getElementAt( i ))).getRadius();
        }
        return radVect;
    }
    
    /** Gets the robots' position as an augmented vector with x y z of first robot
     * and then x y z of second and so on.
     */
    public AlgebraicVector getNormalPositions(){
        AlgebraicVector normalPosVect = new AlgebraicVector( 3 * this.getSize() );
        
        for( int i = 0; i <  this.getSize(); i++ ){
            Point3D dummy =((Robot3D)(this.getRobotList().getElementAt( i ))).getPosition();
            
            normalPosVect.data[ 3*i + 0 ] = dummy.data[0];
            
            normalPosVect.data[ 3*i + 1 ] = dummy.data[1];
            
            normalPosVect.data[ 3*i + 2 ] = dummy.data[2];
            }  
        return normalPosVect;
        
    }
    /** Gets the robots' goal positions as an augmented vector with
     * x y z of goal position of first robot then x y z of goal position  of
     * second robot and so on.
     */
    public AlgebraicVector getNormalGoalPositions(){
        AlgebraicVector normalGoalVect = new AlgebraicVector( 3 * this.getSize() );
        
        for( int i = 0; i < this.getSize() ; i++){
           
             
            Point3D dummy =(((Robot3D)(this.getRobotList().getElementAt( i ))).getGoalPosition());
            //System.out.println("dummy "+ dummy.data[0] +" " +dummy.data[1]+" "+dummy.data[2]);
            normalGoalVect.data[ 0 + 3 * i ] = dummy.data[0];
            
            normalGoalVect.data[ 1 + 3 * i ] = dummy.data[1];
            
            normalGoalVect.data[ 2 + 3 * i ] = dummy.data[2];
        } 
        
        return normalGoalVect;
        
    }
    
    
    
       /** Gets the robots' goal positions as an augmented vector with
     * x y z of goal position of first robot then x y z of goal position  of
     * second robot and so on.
     */
    public AlgebraicVector[] computeCartesianDistances(){
        
        int size = this.getSize();
        AlgebraicVector[] dist = new AlgebraicVector[size];
        for (int i=0; i< size; i++)
            dist[i]= new AlgebraicVector(size);
        
        
        for( int i = 0; i < size ; i++){
            for (int j=0; j< size; j++){
           
                //Point3D goal =(((Robot3D)(robotList.getElementAt( i ))).getGoalPosition());
                Point3D goal =(((Robot3D)(robotList.getElementAt( i ))).getPosition());
                Point3D cur =((Robot3D)(robotList.getElementAt( j ))).getPosition();

              //System.out.println("dummy "+ dummy.data[0] +" " +dummy.data[1]+" "+dummy.data[2]);
               dist[i].data[j ] = Math.pow( (goal.data[0]-cur.data[0])*(goal.data[0]-cur.data[0])
                             + (goal.data[1]-cur.data[1])*(goal.data[1]-cur.data[1])
                             + (goal.data[2]-cur.data[2])*(goal.data[2]-cur.data[2]),0.5);
            }
        }
        
        return dist;
        
    }

     /** Gets the robots' goal positions as an augmented vector with
     * x y z of goal position of first robot then x y z of goal position  of
     * second robot and so on.
     */
    public AlgebraicVector[] computeCartesianDistances2(){

        int size = this.getSize();
        AlgebraicVector[] dist = new AlgebraicVector[size];
        for (int i=0; i< size; i++)
            dist[i]= new AlgebraicVector(size);


        for( int i = 0; i < size ; i++){
            for (int j=0; j< size; j++){

                //Point3D goal =(((Robot3D)(robotList.getElementAt( i ))).getGoalPosition());
                Point3D goal =(((Robot3D)(this.getRobotList().getElementAt( i ))).getPosition());
                Point3D cur =((Robot3D)(this.getRobotList().getElementAt( j ))).getPosition();

              //System.out.println("dummy "+ dummy.data[0] +" " +dummy.data[1]+" "+dummy.data[2]);
               dist[i].data[j ] = Math.pow( (goal.data[0]-cur.data[0])*(goal.data[0]-cur.data[0])
                             + (goal.data[1]-cur.data[1])*(goal.data[1]-cur.data[1])
                             + (goal.data[2]-cur.data[2])*(goal.data[2]-cur.data[2]),0.5);
            }
        }

        return dist;

    }
   
    
    
    public Point3D getCenterOfGravity(){
        double totalX=0, totalY=0, totalZ=0,x, y, z;
        for(int i = 0; i < this.getSize(); i++ ){
            totalX += ((Robot3D)(this.robotList.getElementAt(i))).getPosition().data[0];
            totalY += ((Robot3D)(this.robotList.getElementAt(i))).getPosition().data[1];
            totalZ += ((Robot3D)(this.robotList.getElementAt(i))).getPosition().data[2];
            
        }
        x = totalX/this.getSize();
        y = totalY/this.getSize();
        z = totalZ/this.getSize();
        Point3D result = new Point3D(x,y,z);
        
        return result;
    }

    public Point3D getCenterOfGravityOfNative(){
        double totalX=0, totalY=0, totalZ=0,x, y, z;
        for(int i = 0; i < this.getSize(); i++ ){
            totalX += ((Robot3D)(this.robotList.getElementAt(i))).getGoalPosition().data[0];
            totalY += ((Robot3D)(this.robotList.getElementAt(i))).getGoalPosition().data[1];
            totalZ += ((Robot3D)(this.robotList.getElementAt(i))).getGoalPosition().data[2];

        }
        x = totalX/this.getSize();
        y = totalY/this.getSize();
        z = totalZ/this.getSize();
        Point3D result = new Point3D(x,y,z);

        return result;
    }
    
    public void removeAllElements(RobotList robotList){
        
        removeAllElements(robotList);
    } 
    
   
    
    
    
}
