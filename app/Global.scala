import java.util.Locale

import akka.actor.{Props, Actor, ActorSystem}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import jobs.{CheckDatabase, JobManager}
import play.api._
/**
 * Created by Serdar on 11/2/14.
 */
object Global extends GlobalSettings{


  override def onStart(app: Application){

    val loc = new Locale("en","US")
    println("NavProt has started")
    val system = ActorSystem("JobQueue")
    val jobManager = system.actorOf(Props[JobManager], name = "manager")
    system.scheduler.schedule(0 milliseconds, 60000 milliseconds, jobManager, CheckDatabase())

  }

}
