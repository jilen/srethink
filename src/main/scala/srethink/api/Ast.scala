package srethink.ast

import srethink.protocol._
import srethink.protocol.Datum.DatumType
import srethink.protocol.Term.TermType
import scala.collection.immutable.Seq
import scala.collection.{Seq => _}

trait RDatum {
  def toDatum: Datum
}

case class RBool(value: Boolean) extends RDatum {
  def toDatum = Datum(
    `type` = Some(DatumType.R_BOOL),
    rBool = Some(value)
  )
}
case class RNum(value: Double) extends RDatum {
  def toDatum = Datum(
    `type` = Some(DatumType.R_NUM),
    rNum = Some(value)
  )
}

case class RStr(value: String) extends RDatum {
  def toDatum = Datum(
    `type` = Some(DatumType.R_STR),
    rStr = Some(value)
  )
}

case class RArray(value: Seq[RDatum]) extends RDatum {
  def toDatum = Datum(
    `type` = Some(DatumType.R_ARRAY),
    rArray = value.map(_.toDatum))
}

case class RObject(value: Seq[(String, RDatum)]) extends RDatum {
  val pairs = value.map {
    case (name, data) => Datum.AssocPair(Some(name), Some(data.toDatum))
  }
  def toDatum = Datum(
    `type` = Some(DatumType.R_OBJECT),
    rObject = pairs
  )
}

case class RJson(value: String) extends RDatum {
  def toDatum = Datum(
    `type` = Some(DatumType.R_JSON),
    rStr = Some(value)
  )
}

trait RTerm {
  def toTerm: Term
}

case class DatumTerm[T <: RDatum](rDatum: T) extends RTerm {
  def toTerm = Term(Some(TermType.DATUM),  Some(rDatum.toDatum))
}


case class RTable(name: DatumTerm[RStr]) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.TABLE),
    `args` = Seq(name.toTerm)
  )
}

case class RDb(name: DatumTerm[RStr]) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.DB),
    `args` = Seq(name.toTerm)
  )
}

case class RMakeArray(terms: Seq[RTerm]) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.MAKE_ARRAY),
    args = terms.map(_.toTerm)
  )
}

case class Get(table: RTable, primaryKey: DatumTerm[RStr]) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.GET),
    args = Seq(table.toTerm, primaryKey.toTerm)
  )
}

case class DBCreate(db: DatumTerm[RStr]) extends RTerm {
  def toTerm = Term(`type` = Some(TermType.DB_CREATE), args = Seq(db.toTerm))
}

case class DBDrop(db: DatumTerm[RStr]) extends RTerm {
  def toTerm = Term(`type` = Some(TermType.DB_DROP), args = Seq(db.toTerm))
}

case class TableCreate(table: DatumTerm[RStr], db: Option[RDb] = None) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.TABLE_CREATE),
    args = Seq(table.toTerm)
  )
}

case class TableDrop(table: DatumTerm[RStr], db: Option[RDb] = None) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.TABLE_DROP),
    args = Seq(table.toTerm)
  )
}

case class Insert[T <: RDatum]( table: RTable, data: DatumTerm[T], db: Option[RDb] = None) extends RTerm {
  def toTerm = Term(
    `type` = Some(TermType.INSERT),
    `args` = Seq(table.toTerm, data.toTerm)
  )
}
