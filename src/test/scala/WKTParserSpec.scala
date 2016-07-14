import com.github.raduba.gis._
import org.scalatest._

// TODO: add more tests
class WKTParserSpec extends FunSpec with Matchers {

  def gparse[A](in: String, parser: WKTParser.Parser[A]): Option[A] = {
    WKTParser.parse(parser, in) match {
      case WKTParser.Success(g, _) => Some(g)
      case _ => None
    }
  }

  describe("WKTParser should parse point definition") {
    it("should parse valid point definitions") {
      val toParse = List("1 2", "-1 -2", "   -1  2", "1  -2", "1.123 2.123", "-1.123 2.123", "-1.123 -2.123", "1.123 -2.123", "0 1", "0 0", "1 0", "0 -1", "-1 0")
      val expected = List(Point2D(1, 2), Point2D(-1, -2), Point2D(-1, 2), Point2D(1, -2), Point2D(1.123, 2.123), Point2D(-1.123, 2.123), Point2D(-1.123, -2.123), Point2D(1.123, -2.123), Point2D(0, 1), Point2D(0, 0), Point2D(1, 0), Point2D(0, -1), Point2D(-1, 0))
      val result = toParse.flatMap(s => gparse(s"POINT ($s)", WKTParser.point))

      result should contain theSameElementsAs expected
    }

    it("should parse empty point definition") {
      val result = gparse(s"POINT EMPTY", WKTParser.point)
      result.isEmpty should equal(false)
      result.get should equal(Point2D(0, 0))
    }

    it ("should fail on invalid point definition") {
      val toParse = List("1 2a", " -2", "a", "a b", "", " ", "a1 -2.123", "-1 0")
      val expected = List(Point2D(-1, 0))
      val result = toParse.flatMap(s => gparse(s"POINT ($s)", WKTParser.point))

      result should contain theSameElementsAs expected
    }
  }

  describe("WKTParser should parse linestring definition") {
    it("should parse valid linestring definitions") {
      val validLineString = gparse("LINESTRING (30 10, 10 30, 40 40)", WKTParser.lineString)
      val minValidLineString = gparse("LINESTRING(30 10, 10 30)", WKTParser.lineString)
      val invalidLineString = gparse("LINESTRING (30 10)", WKTParser.lineString)

      validLineString should equal(Some(Line(List(Point2D(30.0,10.0), Point2D(10.0,30.0), Point2D(40.0,40.0)))))
      minValidLineString should equal(Some(Line(List(Point2D(30.0,10.0), Point2D(10.0,30.0)))))
      invalidLineString should equal(None)
    }
  }

  describe("WKTParser should parse polygon definition") {
    it("should parse valid polygon definitions") {
      val poly1 = gparse("POLYGON ((30 10, 40 40, 20 40, 10 20, 30 10))", WKTParser.polygon)
      val poly2 = gparse("POLYGON((35 10, 45 45, 15 40, 10 20, 35 10), (20 30, 35 35, 30 20, 20 30))", WKTParser.polygon)

      poly1 should equal(Some(Polygon(List(Line(List(Point2D(30.0,10.0), Point2D(40.0,40.0), Point2D(20.0,40.0), Point2D(10.0,20.0), Point2D(30.0,10.0)))))))
      poly2 should equal(Some(Polygon(List(Line(List(Point2D(35.0,10.0), Point2D(45.0,45.0), Point2D(15.0,40.0), Point2D(10.0,20.0), Point2D(35.0,10.0))), Line(List(Point2D(20.0,30.0), Point2D(35.0,35.0), Point2D(30.0,20.0), Point2D(20.0,30.0)))))))
    }
  }

  describe("WKTParser should parse multipoint definition") {
    it("should parse valid multipoint definitions") {
      val mp1 = gparse("MULTIPOINT ((10 40), (40 30), (20 20), (30 10))", WKTParser.multiPoint)
      val mp2 = gparse("MULTIPOINT(10 40, 40 30, 20 20, 30 10)", WKTParser.multiPoint)

      mp1 should equal(Some(MultiPoint(List(Point2D(10.0,40.0), Point2D(40.0,30.0), Point2D(20.0,20.0), Point2D(30.0,10.0)))))
      mp2 should equal(Some(MultiPoint(List(Point2D(10.0,40.0), Point2D(40.0,30.0), Point2D(20.0,20.0), Point2D(30.0,10.0)))))
    }
  }

  describe("WKTParser should parse multilinestring definition") {
    it("should parse valid multilinestring definitions") {
      val mls = gparse("MULTILINESTRING ((10 10, 20 20, 10 40), (40 40, 30 30, 40 20, 30 10))", WKTParser.multiLineString)
      mls should equal(Some(MultiLineString(List(Line(List(Point2D(10.0,10.0), Point2D(20.0,20.0), Point2D(10.0,40.0))), Line(List(Point2D(40.0,40.0), Point2D(30.0,30.0), Point2D(40.0,20.0), Point2D(30.0,10.0)))))))
    }
  }

  describe("WKTParser should parse multipolygon definition") {
    it("should parse valid multipolygon definitions") {
      val mpoly1 = gparse("MULTIPOLYGON (((30 20, 45 40, 10 40, 30 20)), ((15 5, 40 10, 10 20, 5 10, 15 5)))", WKTParser.multiPolygon)
      val mpoly2 = gparse("MULTIPOLYGON(((40 40, 20 45, 45 30, 40 40)), ((20 35, 10 30, 10 10, 30 5, 45 20, 20 35), (30 20, 20 15, 20 25, 30 20)))", WKTParser.multiPolygon)

      mpoly1 should equal(Some(MultiPolygon(List(Polygon(List(Line(List(Point2D(30.0,20.0), Point2D(45.0,40.0), Point2D(10.0,40.0), Point2D(30.0,20.0))))), Polygon(List(Line(List(Point2D(15.0,5.0), Point2D(40.0,10.0), Point2D(10.0,20.0), Point2D(5.0,10.0), Point2D(15.0,5.0)))))))))
      mpoly2 should equal(Some(MultiPolygon(List(Polygon(List(Line(List(Point2D(40.0,40.0), Point2D(20.0,45.0), Point2D(45.0,30.0), Point2D(40.0,40.0))))), Polygon(List(Line(List(Point2D(20.0,35.0), Point2D(10.0,30.0), Point2D(10.0,10.0), Point2D(30.0,5.0), Point2D(45.0,20.0), Point2D(20.0,35.0))), Line(List(Point2D(30.0,20.0), Point2D(20.0,15.0), Point2D(20.0,25.0), Point2D(30.0,20.0)))))))))
    }
  }

}
