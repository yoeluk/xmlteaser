package com.teaser

import com.xmlteaser.~:>
import play.api.libs.json.Json
import scala.xml.Node

sealed trait SkuImage
final case class ThumbnailImage(url: String, altText: String, width: Int, height: Int) extends SkuImage
final case class LargeImage(url: String, altText: String, width: Int, height: Int) extends SkuImage
final case class EnlargedImage(url: String, altText: String, width: Int, height: Int) extends SkuImage

final case class NutritionalImage(lang: String, isMain: Boolean,
                                  thumbnailImage: Option[ThumbnailImage],
                                  largeImage: Option[LargeImage],
                                  enlargedImage: Option[EnlargedImage])

final case class NutritionalLabel(labelImages: Seq[NutritionalImage]) {
  def toJsonString: String = Json.toJson(this).toString()
}

object NutritionalLabel {
  implicit def nodeAtomParser(implicit boolP: String ~:> Boolean,
    strP: String ~:> String): Node ~:> NutritionalLabel =
    new (Node ~:> NutritionalLabel) {
      def parse(images: Node) = {
        val iterImages = for {
          imgNode <- images.descendant.filter(_.label == "image")
          lang <- strP.parse((imgNode \@ "lang").trim)
          isMain <- boolP.parse((imgNode \@ "isMain").trim)
        } yield {
          val maybeThumbImageNode = (imgNode \ "thumbnail_image").headOption
          val maybeLargeImageNode = (imgNode \ "large_image").headOption
          val maybeEnlargeImageNode = (imgNode \ "enlarged_image").headOption
          val maybeThumbnailImage = maybeThumbImageNode.flatMap(ImageParser[ThumbnailImage])
          val maybeLargeImage = maybeLargeImageNode.flatMap(ImageParser[LargeImage])
          val maybeEnlargeImage = maybeEnlargeImageNode.flatMap(ImageParser[EnlargedImage])
          NutritionalImage(lang, isMain, maybeThumbnailImage, maybeLargeImage, maybeEnlargeImage)
        }
        iterImages match {
          case Seq() => None
          case nutritionalImages =>
            Some(NutritionalLabel(nutritionalImages.toSeq))
        }
      }
    }
}
