package controllers;

import com.avaje.ebean.Ebean;
import com.google.common.collect.Lists;
import jobs.PostMan;
import models.Task;
import org.apache.commons.io.FileUtils;
import org.biojava.bio.structure.StructureException;
import play.db.ebean.Model;
import play.mvc.*;
import java.io.*;
import java.util.*;
import prc.navprot.utils.StructuralAlignment;
import views.html.*;

import static java.util.UUID.randomUUID;
import static play.libs.Json.toJson;

public class Application extends Controller {

    /**
     * Presents the index page
     *
     * @return
     */
    public static Result index() {
        return ok(index.render("NavProt"));
    }

    /**
     * Presents the people page
     *
     * @return
     */
    public static Result people() {
        return ok(people.render("People"));
    }

    public static Result downloads(){
        return ok(downloads.render("Downloads"));
    }

    public static Result about(){
      return ok(about.render("About"));
    }

    /**
     * Handles the file upload
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Result upload() {

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart pdb1 = body.getFile("pdb1"); // Get first PDB file
        Http.MultipartFormData.FilePart pdb2 = body.getFile("pdb2"); // Get second PDB file
        String email = body.asFormUrlEncoded().get("email")[0]; // Get email address of the user
        String chain1 = body.asFormUrlEncoded().get("chain1")[0]; // Chain name for first PDB
        String chain2 = body.asFormUrlEncoded().get("chain2")[0]; // Chain name for second PDB
        try {
            if (pdb1 != null && pdb2 != null && email != null) {
                String pdb1FileName = pdb1.getFilename(); // Get names of the files
                String pdb2FileName = pdb2.getFilename();
                File pdb1File = pdb1.getFile();
                File pdb2File = pdb2.getFile();
                UUID uid = randomUUID(); // Create a unique ID for the job
                File jobDir = new File("/home/serdar/navprot/navprot/userdata", uid.toString());
                //File jobDir = new File("userdata", uid.toString()); // Set job directory for copying files
                jobDir.mkdir(); // Create job directory
                File newPDB1 = new File(jobDir, pdb1FileName);
                File newPDB2 = new File(jobDir, pdb2FileName);
                FileUtils.copyFile(pdb1File, newPDB1); // Copy files to job directory
                FileUtils.copyFile(pdb2File, newPDB2);
                if (CheckFiles.residueNumbersEqual(newPDB1, chain1, newPDB2, chain2)) { // Check if residue numbers are correct
                    Calendar cal = Calendar.getInstance(); // For getting the time     // and file is readable.
                    CheckFiles.convertPDB2Torsion(newPDB1, chain1, newPDB2, chain2, jobDir);

                    double rmsd = StructuralAlignment.align(newPDB1, chain1, newPDB2, chain2, uid.toString());

                    return createTask(uid.toString(), // Create database entity for the job with unique ID
                            email, // E-mail of user
                            false, // Job is not complete
                            false, // Job is not running yet
                            false, // Job is not cancelled, it has just been submitted
                            cal.getTime(), // Upload time
                            rmsd, // initial RMSD between structures
                            pdb1FileName, // Information of uploaded PDB files
                            pdb2FileName, // and chains
                            chain1,
                            chain2);

                } else {
                    return ok(uploadfailed.render("Error", "A problem occurred with your request. It seems like number of residues " +
                            "are not equal or there was a problem with reading your PDB files. Please go back and " +
                            "upload your files again."));
                }
            } else {
                return ok(uploadfailed.render("Error","It seems like you did not choose a file to upload or there was a problem " +
                        "with reading your files. Please go back and upload your files again."));
            }
        } catch (IOException | StructureException e) {
            return ok(uploadfailed.render("Error","An error occurred while handling your request. Please try to upload your " +
                    "files again. If you keep seeing this page, please contact the administrator."));
        }
    }

    /**
     * Creates a database entity for the job and returns the upload successful page.
     * @param jobId
     * @param email
     * @param isDone
     * @param isRunning
     * @param isCancelled
     * @param date
     * @param initialRmsd
     * @param initName
     * @param finalName
     * @param initialChain
     * @param finalChain
     * @return
     */
    public static Result createTask(String jobId, String email, boolean isDone,
                                    boolean isRunning, boolean isCancelled, Date date,
                                    double initialRmsd, String initName,
                                    String finalName, String initialChain, String finalChain) {

        try {
            Task job = new Task();
            job.id = jobId;
            job.email = email;
            job.RMSD = String.format("%.2f", initialRmsd);
            job.isDone = isDone;
            job.isRunning = isRunning;
            job.uploadDate = date;
            job.isCancelled = isCancelled;
            job.initialPDB = initName;
            job.finalPDB = finalName;
            job.initialChain = initialChain;
            job.finalChain = finalChain;
            job.save();
            if(job.email.length() > 0){
                EmailSender sender = new EmailSender(job);
                sender.sendQueueMail();
            }
            return ok(uploadresult.render(job, initName, finalName, String.format("%.2f", initialRmsd)));

        } catch (Exception e) {

            e.printStackTrace();
            return ok(uploadfailed.render("Error","An error occurred while handling your request. Please try to upload your " +
                    "files again. If you keep seeing this page, please contact the administrator."));

        }

    }

    /**
     * Lists all requests, for debug purposes
     *
     * @return as json
     */
    public static Result getAllRequests() {

        List<Task> tasks = new Model.Finder(String.class, Task.class).all();
        return ok(toJson(tasks));

    }

    /**
     * Gets all requests by date, newest first, for debug purposes
     *
     * @return by reverse date
     */
    public static List<Task> getRequestsbyDate() {

        List<Task> tasks = new Model.Finder(String.class, Task.class).where().orderBy("uploadDate desc").findList();
        List<Task> reverseView = Lists.reverse(tasks);
        return reverseView;

    }

    /**
     * Cancels the run and returns "Job is cancelled" page
     * @param uid
     * @return
     */
    public static Result cancelJob(String uid){

        Task task = Ebean.find(Task.class, uid);
        Result res = ok(uploadfailed.render("Job status: cancelled","Please go back to home page if you would like to " +
                "submit a new job request."));
        if(task != null){
          task.setIsCancelled(true);
          task.setIsRunning(false);
          task.setIsDone(false);
          task.save();
        }
        return res;

    }
    /**
     * Sets the job to done, for debugging purposes.
     *
     */
    public static Result setJobDone(String uid){
	
	Task task = Ebean.find(Task.class, uid);
	Result res = ok(uploadfailed.render("Job status is set to done!","Check results page."));
	if(task != null){
	  task.setIsCancelled(false);
	  task.setIsRunning(false);
	  task.setIsDone(true);
	  task.save();
	}
	return res;
    }


    /**
     * Returns the results the simulation status page, if simulation is complete returns the results page
     * @param uuid
     * @return
     */
    public static Result returnSimulationResults(String uuid) {

        Task thisTask = Ebean.find(Task.class, uuid);
        Result res = ok(uploadfailed.render("Job status: unavailable", "There does not seem to be a registered job with this ID."));
        try {
            if (thisTask.isDone) {
                // render simulation results
                res = ok(results.render(thisTask));
            } else if (thisTask.isRunning) {
                // render simulation is running
                res = ok(jobstatus.render("running", "Your simulation is running at the moment. It may take some time until it" +
                        " is complete. Please revisit this page after an hour. If you provided an e-mail address with your " +
                        "request, we will send you an e-mail when your simulation is done.",uuid));
            } else if (thisTask.isCancelled) {
                // render simulation is cancelled
                res = ok(uploadfailed.render("Job status: cancelled", "Your job seems to be cancelled. Please go back to home page to upload " +
                        "your PDB files and start a new simulation."));
            } else if (!thisTask.isDone && !thisTask.isRunning && !thisTask.isCancelled) {
                // render simulation is at queue
                res = ok(jobstatus.render("pending", "Your job is in the queue. Please revisit this page later to see your " +
                        "job status or your results.",uuid));
            }
        } catch ( NullPointerException e ) {
            e.printStackTrace();
        }
        return res;
    }
}
