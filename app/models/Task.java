package models;

import play.data.format.Formats;
import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by HP on 21.10.2014.
 */

/**
 * Database entity for the simulation job
 */
@Entity
public class Task extends Model {

    @Id
    public String id; // Unique id created for particular job

    public String email; // E-mail address of the user

    public String initialPDB; // Name of initial conformation file

    public String initialChain; // Chain of initial conformation

    public String finalPDB; // Name of final conformation file

    public String finalChain; // Chain of final conformation

    public String RMSD;

    public boolean isDone; // Is simulation complete?

    public void setIsDone(boolean _isdone){
        isDone = _isdone;
    }

    public boolean isRunning; // Is simulation running?

    public void setIsRunning(boolean _isrunning){
        isRunning = _isrunning;
    }

    public boolean isCancelled; // Is simulation cancelled?

    public void setIsCancelled(boolean _iscancelled){
        isCancelled = _iscancelled;
    }

    @Formats.DateTime(pattern="hh:mm:ss, dd/MM/yyyy")
    public Date uploadDate = new Date(); // Upload date of the file

    @Formats.DateTime(pattern="hh:mm:ss, dd/MM/yyyy")
    public Date startTime = new Date();

    public void setStartTime(Date start){
        startTime = start;
    }

    @Formats.DateTime(pattern="hh:mm:ss, dd/MM/yyyy")
    public Date endTime = new Date();

    public void setEndTime(Date stop){
        endTime = stop;
    }

}
