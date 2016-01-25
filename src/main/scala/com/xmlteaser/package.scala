package com

import shapeless._, labelled._
import scala.util.Try
import scala.xml._, xml.Text

package object xmlteaser {

  trait ~:>[A, B] extends Serializable {
    def parse(a: A): Option[B]
  }

  object ~:> {
    implicit val stringParser = parseString(identity)
    implicit val charParser = parseString(_.head)
    implicit val byteParser = parseString(_.toByte)
    implicit val shortParser = parseString(_.toShort)
    implicit val intParser = parseString(_.toInt)
    implicit val longParser = parseString(_.toLong)
    implicit val floatParser = parseString(_.toFloat)
    implicit val doubleParser = parseString(_.toDouble)
    implicit val booleanParser = parseString(_.toBoolean)
    implicit def nodeAtomParser[B](implicit sp: String ~:> B): Node ~:> B =
      parseNode { node =>
        val trimmed = Utility.trim(node)
        val atom = trimmed.child.find(_.isAtom).getOrElse(Text(""))
        sp.parse(atom.text)
      }
  }

  def parseNode[B](fp: Node => Option[B]): Node ~:> B =
    new (Node ~:> B) {
      def parse(n: Node): Option[B] = fp(n)
    }

  def parseString[B](fp: String => B): String ~:> B =
    new (String ~:> B) {
      def parse(s: String): Option[B] = Try(fp(s)).toOption
    }

  trait RecFromXml[R <: HList] extends Serializable {
    def apply(n: Node): Option[R]
  }

  object RecFromXml {
    def apply[R <: HList](implicit fxml: RecFromXml[R]) = fxml

    implicit def hnilFromXml[T]: RecFromXml[HNil] =
      new RecFromXml[HNil] {
        def apply(n: Node): Option[HNil] = Some(HNil)
      }

    implicit def hlistFromXml[S <: String, V, T <: HList]
    (implicit wk: Witness.Aux[S], parser: Node ~:> V, fmt: RecFromXml[T])
    : RecFromXml[FieldType[S, V] :: T] =
      new RecFromXml[FieldType[S, V] :: T] {
        def apply(n: Node): Option[FieldType[S, V] :: T] =
          for {
            node <- n.descendant_or_self.find(_.label == wk.value)
            typed <- parser.parse(node)
            rest <- fmt(n)
          } yield field[S](typed) :: rest
      }
  }

  object xmls {
    implicit def xmlOps(n: Node): XmlOps = new XmlOps(n)
  }

  final class XmlOps(val node: Node) extends AnyVal {
    def toRecord[R <: HList](implicit fm: RecFromXml[R]): Option[R] = fm(node)
  }
}
