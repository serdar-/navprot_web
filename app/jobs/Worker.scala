package jobs

import akka.actor.{Props, Actor}
import models.Task
import prc.navprot.ProcessSimulation
import java.io.File
import prc.navprot.utils.Analysis.createChartData
import com.avaje.ebean.Ebean

case class StartWorking(task: Task)
/**
 * Created by Serdar on 11/3/14.
 */
class Worker extends Actor{

  def start(task: Task){

    val simulation: ProcessSimulation = new ProcessSimulation
    val jobDir = new File("/home/serdar/navprot/navprot/userdata",task.id)
//    val jobDir = new File("userdata",task.id)
    simulation.setJobDirectory(jobDir.getCanonicalPath)
    simulation.setResultFileName("results.txt")
    simulation.setCAs(task.initialPDB, task.initialChain)
    simulation.setTask(task)
    task.setIsRunning(true)
    task.save()
    simulation.start("torsion.txt")
    val postman = context.actorOf(Props[PostMan], name = task.id+"endmail")
    val thisTask = Ebean.find(classOf[Task],task.id)
    if(!thisTask.isCancelled){
      createChartData(task)
      task.setIsDone(true)
      task.setIsRunning(false)
      task.save()
      postman ! SendFinishedMail(task)
    } else {
      postman ! SendCancelMail(task)
    }
  }

  def receive = {

    case StartWorking(task) => start(task)

  }

}
