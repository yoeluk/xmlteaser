package com.teaser

import org.junit.Test
import org.junit.Assert._
import play.api.libs.json.Json
import scalaz.Apply, scalaz.Scalaz.optionInstance
import shapeless._, record._, syntax.singleton._, ops.record.Selector
import shapeless.test.illTyped

class TeaserSpec {

  @Test
  def testSingleFieldSelection(): Unit = {
    import KitchenSink.rabbitWitness

    def selectRabbit[L <: HList](xs: L)(implicit sel: Selector[L, rabbitWitness.T]) =
      xs("rabbit")

    val animals =
      ("rabbit" ->> "Longears") ::
        ("mouse" ->> "Mickey") ::
        ("pig" ->> "Oink") ::
        HNil

    val withoutRabbit =
      ("mouse" ->> "Mickey") ::
        ("pig" ->> "Oink") ::
        HNil

    val theRabbit = selectRabbit(animals)

    assertEquals(theRabbit, "Longears")

    illTyped(
    """
      selectRabbit(withoutRabbit)
    """)
  }

  @Test
  def testMultipleFieldSelection(): Unit = {

    def personMin[L <: HList](xs: L)(implicit sels: PersonSelectors[L]) = { import sels._
      (xs("name"), xs("surname"), xs("age"))
    }

    val personRecord1 =
      ("age" ->> 26) ::
        ("title" ->> "Ms") ::
        ("name" ->> "Tom") ::
        ("surname" ->> "Harris") ::
        HNil

    val personRecord2 =
      ("age" ->> 14) ::
        ("title" ->> "Mr") ::
        ("name" ->> "Fred") ::
        ("surname" ->> "Forde") ::
        ("haircolour" ->> "blond") ::
        HNil

    val incompletePersonMin =
      ("title" ->> "Mr") ::
        ("name" ->> "Fred") ::
        ("surname" ->> "Forde") ::
        ("haircolour" ->> "blond") ::
        HNil

    val personMin1 = personMin(personRecord1)

    val personMin2 = personMin(personRecord2)

    assertEquals(personMin1, ("Tom", "Harris", 26))

    assertEquals(personMin2, ("Fred", "Forde", 14))

    illTyped(
    """
      personMin(incompletePersonMin)
    """)
  }

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

  @Test
  def testParsingSimpleXml(): Unit = {
    import KitchenSink.rabbitWitness
    import com.xmlteaser.xmls._ // xml syntax

    def selectRabbit[L <: HList](xs: L)(implicit sel: Selector[L, rabbitWitness.T]) = xs("rabbit")

    val maybeNoRabbitRec = xml.toRecord[Record.`"duckcount" -> Int, "allsome" -> String, "root" -> String, "emptyfield" -> String`.T]

    illTyped(
    """
      maybeNoRabbitRec.map(selectRabbit(_))
    """)

    val maybeRabbitRec = xml.toRecord[Record.`"rabbit" -> String`.T]

    val maybeCombinedRec = Apply[Option].apply2(maybeNoRabbitRec, maybeRabbitRec){_ ++ _}

    val maybeRabbit = maybeCombinedRec.map(selectRabbit(_))

    assertEquals(maybeRabbit, Some("Longears"))

    /**
      * won't parse xml nodes to record with invalid field type
      */

    val maybeIntRabbitRec = xml.toRecord[Record.`"rabbit" -> Int, "duckcount" -> Int`.T]

    assertTrue(maybeIntRabbitRec.isEmpty)
  }

  val imgXml =
    <images>
      <image isMain="true" lang="en_CA">
        <thumbnail_image>
          <url Height="75" Width="75">images/597/154597.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </thumbnail_image>
        <large_image>
          <url Height="300" Width="300">images/597/151297.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </large_image>
        <enlarged_image>
          <url Height="400" Width="400">images/597/156797.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </enlarged_image>
      </image>
      <image isMain="false" lang="fr_CA">
        <thumbnail_image>
          <url Height="75" Width="75">images/923/1382323.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </thumbnail_image>
        <large_image>
          <url Height="300" Width="300">images/923/1387823.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </large_image>
        <enlarged_image>
          <url Height="400" Width="400">images/923/1312823.jpg</url>
          <alt_text>
            <value>Crystal Light Lemonade</value>
          </alt_text>
        </enlarged_image>
      </image>
    </images>

  val jsonImgs =
    """
      |{
      |  "labelImages": [
      |    {
      |      "lang": "en_CA",
      |      "isMain": true,
      |      "thumbnailImage": {
      |        "url": "images/597/154597.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 75,
      |        "height": 75
      |      },
      |      "largeImage": {
      |        "url": "images/597/151297.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 300,
      |        "height": 300
      |      },
      |      "enlargedImage": {
      |        "url": "images/597/156797.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 400,
      |        "height": 400
      |      }
      |    },
      |    {
      |      "lang": "fr_CA",
      |      "isMain": false,
      |      "thumbnailImage": {
      |        "url": "images/923/1382323.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 75,
      |        "height": 75
      |      },
      |      "largeImage": {
      |        "url": "images/923/1387823.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 300,
      |        "height": 300
      |      },
      |      "enlargedImage": {
      |        "url": "images/923/1312823.jpg",
      |        "altText": "Crystal Light Lemonade",
      |        "width": 400,
      |        "height": 400
      |      }
      |    }
      |  ]
      |}
    """.stripMargin

  @Test
  def testParsingNutritionalLabelFromXml(): Unit = {
    import com.xmlteaser.xmls._ // xml syntax
    import syntax.typeable._

    def labelSelector[L <: HList](xs: L, labelWitness: Witness.Lt[String])(implicit sel: Selector[L, labelWitness.T]) =
      xs(labelWitness)

    val maybeImagesRec = imgXml.toRecord[Record.`"images" -> NutritionalLabel`.T]

    val nutritionalLabel = labelSelector(maybeImagesRec.get, Witness("images")).cast[NutritionalLabel]

    assertTrue(maybeImagesRec.nonEmpty)

    illTyped(
    """
      labelSelector(maybeImagesRec.get, Witness("img"))
    """)

    assertEquals(Json.parse(jsonImgs), Json.toJson(nutritionalLabel.get))
  }
}
