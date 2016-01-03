package com.teaser

import com.xmlteaser.~:>
import scala.xml.Node

sealed trait ImageParser[A] {
  def toImage(url: String, altText: String, width: Int, height: Int): A
}

object ImageParser {
  def apply[A](xml: Node)(implicit
    ip: ImageParser[A],
    strP: String ~:> String,
    intP: String ~:> Int): Option[A] =
    for {
      urlNode     <- xml.descendant.find(_.label == "url")
      altTextNode <- xml.descendant.find(_.label == "alt_text")
      url         <- strP.parse(urlNode.text.trim)
      width       <- intP.parse((urlNode \@ "Width").trim)
      height      <- intP.parse((urlNode \@ "Height").trim)
      altText     <- strP.parse((altTextNode \ "value").text.trim)
    } yield ip.toImage(url, altText, width, height)

  implicit val thumbImage = new ImageParser[ThumbnailImage] {
    def toImage(url: String, altText: String, width: Int, height: Int) =
      ThumbnailImage(url, altText, width, height)
  }

  implicit val largeImage = new ImageParser[LargeImage] {
    def toImage(url: String, altText: String, width: Int, height: Int) =
      LargeImage(url, altText, width, height)
  }

  implicit val enlargeImage = new ImageParser[EnlargedImage] {
    def toImage(url: String, altText: String, width: Int, height: Int) =
      EnlargedImage(url, altText, width, height)
  }
}