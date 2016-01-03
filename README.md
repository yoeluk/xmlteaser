## XML Teaser: Ad hoc xml parser with shapeless

```scala
import scalaz.Apply, scalaz.Scalaz.optionInstance
import shapeless._, record._, syntax.singleton._, ops.record.Selector

val xml =
    <root>
      <emptyfield/>
      <allsome>
        <some>value</some>
        <some>value</some>
        <some>value</some>lala
      </allsome>
      <rabbit>Longears</rabbit>
      <duckcount>56</duckcount>
    </root>

def selectRabbit[L <: HList](xs: L)(implicit sel: Selector[L, rabbitWitness.T]) = xs("rabbit")

val maybeNoRabbitRec = xml.toRecord[Record.`"duckcount" -> Int, "allsome" -> String, "root" -> String, "emptyfield" -> String`.T]

maybeNoRabbitRec.map(selectRabbit(_)) // won't compile

val maybeRabbitRec = xml.toRecord[Record.`"rabbit" -> String`.T]

val maybeCombinedRec = Apply[Option].apply2(maybeNoRabbitRec, maybeRabbitRec){_ ++ _}

maybeCombinedRec.map(selectRabbit(_)) // Some("Longears")

xml.toRecord[Record.`"rabbit" -> Int, "duckcount" -> Int`.T] // None

```
