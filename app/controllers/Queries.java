package controllers;

import com.avaje.ebean.Ebean;
import models.Task;
import org.apache.commons.io.FileUtils;
import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.io.PDBFileParser;
import play.db.ebean.Model;
import play.mvc.Result;
import play.twirl.api.Html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static play.mvc.Results.ok;

/**
 * Created by Serdar on 11/3/14.
 */
public class Queries {

    /**
     * Returns true if there are not any jobs running
     * @return
     */
    public boolean notAnyJobsRunning(){

        List<Task> taskList = new Model.Finder(String.class, Task.class)
                .where()
                .eq("isRunning",true)
                .findList();
        if(taskList.size() > 0){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns true if all array is false
     * @param array
     * @return
     */
    private boolean isAllFalse(boolean[] array){

        for(boolean b: array) if(!b) return true;
        return false;

    }

    /**
     * Returns the earliest uploaded task which is not complete
     * @return
     */
    public Task nextOntheLine(){

        Task task = null;
        List<Task> taskList = new Model.Finder(String.class, Task.class)
                                                .where()
                                                .eq("isDone", false)
                                                .eq("isCancelled", false)
                                                .eq("isRunning", false)
                                                .orderBy("uploadDate asc")
                                                .findList();
        if(taskList.size() > 0){
            task = taskList.get(0);
        }
        return task;
    }

    /**
     * Gets the chart data
     * @param task
     * @return
     */
    public static Html getChartData(Task task){
        String data = "";
        try{
            data = FileUtils.readFileToString(new File("userdata",task.id + "/chartdata"));
        } catch (IOException e){
            e.printStackTrace();
        }
        return Html.apply(data);
    }

    /**
     * Creates PDB file for Jmol and for user to download
     * @param uid
     * @return
     */
    public static Result resultPDB(String uid){

        PDBFileParser parser = new PDBFileParser();
        try{
            Structure pdb = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",uid +"/result.pdb"))));
            return ok(pdb.toPDB());
        } catch(IOException e) {
            return null;
        }
    }

    /**
     * Creates the initial PDB file for Jmol
     * @param uid
     * @return
     */
    public static Result initialPDB(String uid){

        Task task = Ebean.find(Task.class, uid);
        PDBFileParser parser = new PDBFileParser();
        try{
            Structure pdb = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",task.id +"/" + task.initialPDB + "_aligned"))));
            return ok(pdb.toPDB());
        } catch(IOException e){
            return null;
        }
    }

    /**
     * Creates the final PDB file for Jmol
     * @param uid
     * @return
     */
    public static Result finalPDB(String uid){

        Task task = Ebean.find(Task.class, uid);
        PDBFileParser parser = new PDBFileParser();
        try{
            Structure pdb = parser.parsePDBFile(new BufferedReader(new FileReader(new File("userdata",task.id +"/" + task.finalPDB + "_aligned"))));
            return ok(pdb.toPDB());
        } catch(IOException e){
            return null;
        }
    }

}
