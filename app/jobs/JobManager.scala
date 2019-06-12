package jobs

import akka.actor.{Props, Actor}
import controllers.Queries

case class CheckDatabase()
/**
 * Created by Serdar on 11/2/14.
 */
class JobManager extends Actor {

  def receive = { case CheckDatabase() => CheckIt }

  def CheckIt = {
    /**
     * Checks the database, if there are no jobs running, starts the next job
     */
    val q = new Queries
      if(q.notAnyJobsRunning){
        val nextTask = q.nextOntheLine
        if(nextTask != null ){
          val worker = context.system.actorOf(Props[Worker], name = nextTask.id)
          val postman = context.system.actorOf(Props[PostMan], name = nextTask.id +"startmail")
          worker ! StartWorking(nextTask)
          postman ! SendStartMail(nextTask)
        } else {
          println("Status: idle...")
      }
    }
  }
}




