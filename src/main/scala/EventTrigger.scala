import org.joda.time.{DateTime, Duration}

import scala.collection.mutable.ListBuffer

/**
  * Created by tug on 10/08/16.
  */
abstract class EventTrigger {
  var startTrigMeasure: Option[Measure] = None

  val ruleDuration: Duration

  def rule(eventHistory: ListBuffer[Event], measure: Measure): Boolean

  def createEvent(dt: DateTime): Event

  def test(eventHistory: ListBuffer[Event], measure: Measure): Option[Event] = {

    (rule(eventHistory, measure), startTrigMeasure) match {
      // rule = true and start trigger is empty then save this measure
      case (true, None) =>
        startTrigMeasure = Some(measure)
        None

      // rule = true and start trigger exists then check event duration and return an event
      case (true, Some(startMeasure)) =>

        // Check rule duration to return an event
        if (startMeasure.dt.plus(ruleDuration).isBefore(measure.dt)) {
          // Return event
          Some(createEvent(measure.dt))
        } else {
          None
        }

      // rule = false then Delete old start trigger
      case (_, _) =>
        startTrigMeasure = None
        None
    }
  }
}

class TriggerSporulationOidium extends EventTrigger {

  val ruleDuration: Duration = Duration.standardMinutes(60)

  def rule(eventHistory: ListBuffer[Event], measure: Measure): Boolean = {
    measure.humidity > 90
  }

  def createEvent(dt: DateTime): Event = SporulationOidium(dt)
}

class TriggerBotrytis extends EventTrigger {

  val ruleDuration: Duration = Duration.standardMinutes(360)

  def rule(eventHistory: ListBuffer[Event], measure: Measure): Boolean = {
    measure.humidity > 90 && measure.temperature > 15 && measure.temperature < 20
  }

  def createEvent(dt: DateTime): Event = Botrytis(dt)
}

class TriggerDevOidium extends EventTrigger {

  val ruleDuration: Duration = Duration.standardMinutes(360)

  def rule(eventHistory: ListBuffer[Event], measure: Measure): Boolean = {
    eventHistory.exists {
      case _: SporulationOidium => true
      case _ => false
    } &&
      measure.humidity < 70 &&
      measure.temperature > 20
  }

  def createEvent(dt: DateTime): Event = DevOidium(dt)
}
