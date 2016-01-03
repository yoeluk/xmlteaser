package com.teaser

import com.teaser.KitchenSink._
import shapeless._
import shapeless.ops.record._

object KitchenSink {
  val titleWitness = Witness("title")
  val nameWitness = Witness("name")
  val surnameWitness = Witness("surname")
  val ageWitness = Witness("age")
  val attributeWitness = Witness("attribute")
  val rabbitWitness = Witness("rabbit")
}

case class PersonSelectors[L <: HList](implicit
  s1: Selector[L, nameWitness.T],
  s2: Selector[L, surnameWitness.T],
  s3: Selector[L, titleWitness.T],
  s4: Selector[L, ageWitness.T]
)

object PersonSelectors {
  implicit def selectors[L <: HList](implicit
    s1: Selector[L, nameWitness.T],
    s2: Selector[L, surnameWitness.T],
    s3: Selector[L, titleWitness.T],
    s4: Selector[L, ageWitness.T]
  ) = PersonSelectors[L]
}
