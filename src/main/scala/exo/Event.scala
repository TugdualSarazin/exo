package exo

import org.joda.time.DateTime

/**
  * Created by tug on 10/08/16.
  */
abstract class Event {
  def dt : DateTime
}

case class SporulationOidium(dt: DateTime) extends Event
case class Botrytis(dt: DateTime) extends Event
case class DevOidium(dt: DateTime) extends Event
