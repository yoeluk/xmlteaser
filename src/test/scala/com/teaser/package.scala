package com

import play.api.libs.json._

package object teaser {
  implicit val thumbnailImageWrites = Json.format[ThumbnailImage]
  implicit val largeImageWrites = Json.format[LargeImage]
  implicit val enlargeImageWrites = Json.format[EnlargedImage]
  implicit val implicitNutImgWrites = Json.format[NutritionalImage]
  implicit val implicitNutLblWrites = Json.format[NutritionalLabel]
}
