package jobs

import models.Task
import akka.actor.Actor
import controllers.EmailSender

case class SendCancelMail(task: Task)
case class SendQueueMail(task: Task)
case class SendStartMail(task: Task)
case class SendFinishedMail(task: Task)

/**
 * Created by Serdar on 11/8/14.
 */
class PostMan extends Actor{

  def receive = {

    case SendCancelMail(task: Task) => {if(emailDefined(task)){val sender = new EmailSender(task); sender.sendCancelMail()}}
    case SendQueueMail(task: Task) => {if(emailDefined(task)){val sender = new EmailSender(task); sender.sendQueueMail()}}
    case SendStartMail(task: Task) => {if(emailDefined(task)){val sender = new EmailSender(task); sender.sendProcessStartedMail()}}
    case SendFinishedMail(task: Task) => {if(emailDefined(task)){val sender = new EmailSender(task); sender.sendFinishedMail()}}
    case _ => println("Unknown message sent to PostMan, no e-mail was sent...")

  }

  private def emailDefined(task: Task): Boolean = {

    if(task.email.length > 0){
      return true;
    } else {
      return false;
    }

  }

}
