package controllers;


import com.sun.mail.smtp.SMTPTransport;
import models.Task;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by HP on 6.11.2014.
 */
public class EmailSender {

    private Email email;
    private Task task;
    final private String user = "navprot@prc.boun.edu.tr";
    final private String pass = "";
    final private int port = 25;

    
    public EmailSender(Task task){

        Email email = new SimpleEmail();
        try {
            email.setSmtpPort(port);
            email.setHostName("mail.prc.boun.edu.tr");
            email.setAuthenticator(new DefaultAuthenticator(user,pass));
            email.setFrom(user,"NavProt Server");
            email.addTo(task.email);
            email.addBcc("serdar.ozsezen@gmail.com");
            this.email = email;
            this.task = task;
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendQueueMail(){

        try {
            email.setSubject("Your job is in the queue");
            email.setMsg("Dear User,\n\nJob ID: " + task.id + "\n" +
            "Status link: " + "http://safir.prc.boun.edu.tr/navprot/results/" + task.id + "\n" +
            "Your job request is in the queue at the moment.\n" +
            "Once we start processing your job, " +
            "we are going to send you another e-mail "+
            "to let you know about that.\n\n" +
            "Thank you for your patience,\n\nNavProt");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendCancelMail(){

        try {
            email.setSubject("Your job is cancelled");
            email.setMsg("Dear User,\n\nJob ID: " + task.id + "\n" +
            "Your job is cancelled by your request." + "\n\nNavProt");
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

    public void sendProcessStartedMail(){

        try{
            email.setSubject("Your job is being processed");
            email.setMsg("Dear User,\n\nJob ID: " + task.id + "\n" +
            "Status link: " + "http://safir.prc.boun.edu.tr/navprot/results/" + task.id + "\n" +
            "We started processing your job, " +
            "we will let you know when it is finished.\n"+
            "Thank you for your patience,\n\nNavProt");
            email.send();
        } catch (EmailException e){
            e.printStackTrace();
        }
    }

    public void sendFinishedMail(){

        try{
            email.setSubject("Your simulation is complete");
            email.setMsg("Dear User,\n\nJob ID: " + task.id + "\n" +
                    "Status link: " + "http://safir.prc.boun.edu.tr/navprot/results/" + task.id + "\n" +
                    "Your simulation has finished. You can check your status link to see your results.\n\n" +
                    "Navprot");
            email.send();
        } catch (EmailException e){
            e.printStackTrace();
        }
    }


}
