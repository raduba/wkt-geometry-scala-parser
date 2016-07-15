package com.github.raduba.gis

import language.implicitConversions
import play.api.libs.json.{Json, Writes}

trait GeoJson
case class Feature(geometry: Geometry, properties: Map[String, String] = Map.empty) extends GeoJson
case class FeatureCollection(features: List[Feature]) extends GeoJson

object GeoJson {

  private def writeJson[A](name: String, f: A => Json.JsValueWrapper, attribute: String = "coordinates"): Writes[A] = new Writes[A] {
    def writes(geometry: A) = Json.obj("type" -> name, attribute -> f(geometry))
  }

  implicit def pointToList(point: Point2D): List[Double] = List(point.x, point.y)

  implicit def lineToList(line: Line): List[List[Double]] = line.points.map(pointToList(_))

  implicit def polygonToList(polygon: Polygon): List[List[List[Double]]] = polygon.lines.map(lineToList(_))

  implicit def multiPointToList(multiPoint: MultiPoint): List[List[Double]] = multiPoint.points.map(pointToList(_))

  implicit def multiLineToList(multiLine: MultiLine): List[List[List[Double]]] = multiLine.lines.map(lineToList(_))

  implicit def multiPolygonToList(multiPolygon: MultiPolygon): List[List[List[List[Double]]]] = multiPolygon.polygons.map(polygonToList(_))

  implicit def featureCollectionToList(featureCollection: FeatureCollection): List[Feature] = featureCollection.features

  implicit class GeometryToFeature(geometry: Geometry) {
    def toFeature: Feature = Feature(geometry)
  }

  implicit val pointWrites: Writes[Point2D] = writeJson("Point", pointToList)

  implicit val lineWrites: Writes[Line] = writeJson("LineString", lineToList)

  implicit val polygonWrites: Writes[Polygon] = writeJson("Polygon", polygonToList)

  implicit val multiPointWrites: Writes[MultiPoint] = writeJson("MultiPoint", multiPointToList)

  implicit val multiLineWrites: Writes[MultiLine] = writeJson("MultiLineString", multiLineToList)

  implicit val multiPolygonWrites: Writes[MultiPolygon] = writeJson("MultiPolygon", multiPolygonToList)

  implicit val geometryWrites = new Writes[Geometry] {
    def writes(geometry: Geometry) = geometry match {
      case p: Point2D => Json.toJson(p)
      case l: Line => Json.toJson(l)
      case py: Polygon => Json.toJson(py)
      case mp: MultiPoint => Json.toJson(mp)
      case ml: MultiLine => Json.toJson(ml)
      case mpy: MultiPolygon => Json.toJson(mpy)
    }
  }

  implicit def featureWrites: Writes[Feature] = new Writes[Feature] {
    def writes(feature: Feature) = Json.obj(
      "type" -> "Feature",
      "geometry" -> Json.toJson(feature.geometry),
      "properties" -> feature.properties
    )
  }

  implicit val featureCollectionWrites: Writes[FeatureCollection] = writeJson("FeatureCollection", featureCollectionToList, "features")

  implicit class FeatureToJson(feature: Feature) {
    def toJson: String = Json.toJson(feature).toString()
  }

  implicit class FeatureCollectionToJson(featureCollection: FeatureCollection) {
    def toJson: String = Json.toJson(featureCollection).toString()
  }
}
