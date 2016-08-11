
import java.io.{BufferedReader, File, FileReader}
import java.nio.file.{Files, Paths}
import java.util

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.collection.mutable.ListBuffer


case class Measure(dt: DateTime, humidity: Double, temperature: Double)

object Exo1 extends App {
  //val measureFilePath = "data/measure-mini.csv"
  val measureFilePath = "data/measure.csv"
  val dtFormatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")

  // Init akka and create actor
  val system = ActorSystem("system")
  val monitoringActor = system.actorOf(Props[Monitoring])

  val measureFile = scala.io.Source.fromFile(measureFilePath)
  try {
    measureFile.getLines().foreach { l =>
      val cols = l.split(",").map(_.trim)
      try {
        val measure = Measure(
          dt = DateTime.parse(cols(0), dtFormatter),
          humidity = cols(1).toDouble,
          temperature = cols(2  ).toDouble
        )
        monitoringActor ! measure
      } catch {
        case e: IllegalArgumentException => println(s"cannot parse line: $l")
        case e: Exception => throw e
      }
    }
  }
  finally measureFile.close()

  // Close akka
  Thread.sleep(1000)
  system.terminate()
}

class Monitoring extends Actor {

  val eventHistory = new ListBuffer[Event]()

  val triggers = Seq(
    new TriggerSporulationOidium,
    new TriggerBotrytis,
    new TriggerDevOidium
  )

  def receive = {
    case measure: Measure =>
      //println(measure)

      // Test all triggers and get events
      val newEvents = triggers.flatMap(_.test(eventHistory, measure))

      // Add them to the event history
      eventHistory ++= newEvents

      //println(s"$measureInput")
      newEvents.foreach(e => println(s"New event: $e"))
      if (newEvents.nonEmpty) { println() }
  }
}