/**
 *************************************************************************
 ********************** Composed by: Bar�� ULUTA� ************************
 ********************** Robot3D.java *************************************
 *************************************************************************
 */
package prc.navprot.utils;

public class Robot3D{
    private Point3D pos = null;
    private Point3D goalPos = null;
//    private Color3f color = null;
    private double theta;  //for linked robots
    private double phi;    //for linked robot
    private double goalTheta;  //for linked robots
    private double goalPhi;    //for linked robots
    private double radius = 1.0;
    private double linkDistance; //for linked robots
    private double seeRad;
       
    
    
    /** Creates a new instance of Robot3D */
    public Robot3D( Point3D pos, Point3D goalPos) {
        this.pos = pos;
        this.goalPos = goalPos;
   
    }
    
    /** Creates a new instance of Robot 3D*/
    public Robot3D(){
//        float a = (float)Math.random();
//        float b = (float)Math.random();
//        float c = (float)Math.random();
//        if(a < 0.1) 
//            a +=0.1;
//        if(b < 0.1)
//            b +=0.1;
//        if(c < 0.1) 
//            c +=0.1;
//        Color3f dummy = new Color3f(a, b, c);
//        this.setColor( dummy );

    }
    /** Gets the position of the Robot*/
    public Point3D getPosition(){
        return this.pos;
    }
    /** Sets the position of the the Robot*/
    public void setPosition(Point3D newPos){
        this.pos = newPos;
    }
    /** Gets the color of the Robot */
//    public Color3f getColor(){
//        return this.color;        
//    }
    /** Sets the color of the Robot */
//    public void setColor( Color3f newColor){
//        this.color = newColor;
//    }
    /** Gets the goal position of the Robot */
    public Point3D getGoalPosition(){
        return this.goalPos;
    }
    /** Sets the goal position of the robot */
    public void setGoalPosition(Point3D newGoalPos){
        this.goalPos = newGoalPos;
    }
    /** Gets the radius of the Robot */
    public double getRadius(){
        return this.radius;
    }  
    /** Sets the radius of the Robot*/
    public void setRadius( double radius ){
        this.radius = radius;
    } 
    
    public void setTheta( double theta ){
//        int dummy;
//        if( theta > Math.PI ){
//            dummy = (int)(theta/Math.PI);
//            theta -= Math.PI*dummy;
//        }
        this.theta = theta;
    } 
    public double getTheta(){
        return this.theta;
    } 
    public void setPhi( double phi ){
//        int dummy;
//        if( phi > Math.PI ){
//            dummy = (int)(phi/Math.PI);
//            phi -= Math.PI*dummy;
//        }
        this.phi = phi;
    } 
    public double getPhi(){
        return this.phi;
    } 
    
    public void setGoalTheta( double goalTheta ){
//        int dummy;
//        if( goalTheta > Math.PI ){
//            dummy = (int)(goalTheta/Math.PI);
//            goalTheta -= Math.PI*dummy;
//        }
        this.goalTheta = goalTheta;
    } 
    public double getGoalTheta(){
        return this.goalTheta;
    } 
    public void setGoalPhi( double goalPhi ){
//        int dummy;
//        if( goalPhi > Math.PI ){
//            dummy = (int)(goalPhi/Math.PI);
//            goalPhi -= Math.PI*dummy;
//        }
        this.goalPhi = goalPhi;
    } 
    public double getGoalPhi(){
        return this.goalPhi;
    }
    public double getLinkDistance(){
        return this.linkDistance;
    }
    public void setLinkDistance(double linkDist){
        this.linkDistance=linkDist;
    }
     public void setSeeRad( double seeRad ){
//        int dummy;
//        if( theta > Math.PI ){
//            dummy = (int)(theta/Math.PI);
//            theta -= Math.PI*dummy;
//        }
        this.seeRad = seeRad;
    } 
    public double getSeeRad(){
        return this.seeRad;
    } 
    
    public Robot3D cloneRobot(){
        Robot3D newR = new Robot3D();
        double x=this.pos.data[0];
        double y=this.pos.data[1];
        double z=this.pos.data[2];
        Point3D point=new Point3D(x,y,z);
        double x2=this.goalPos.data[0];
        double y2=this.goalPos.data[1];
        double z2=this.goalPos.data[2];
        Point3D point2=new Point3D(x2,y2,z2);
//        newR.setColor(this.color);
        newR.setGoalPhi(this.goalPhi);
        newR.setGoalPosition(point2);
        newR.setGoalTheta(this.goalTheta);
        newR.setPhi(this.phi);
        newR.setPosition(point);
        newR.setRadius(this.radius);
        newR.setSeeRad(this.seeRad);
        newR.setTheta(this.theta);
        newR.setLinkDistance(this.linkDistance);
        return newR;
    }
    
}
