package controllers


import play.api._
import play.api.mvc._
import play2.tools.xml._
import play2.tools.xml.ESOAP._
import play2.tools.xml.DefaultImplicits._
// import scala.xml.{Attribute, NamespaceBinding}


object Application extends Controller {
  case class Foo(id: Long, name: String, age: Option[Int])
  case class Body(foo: Foo)


  implicit object FooXMLF extends XMLFormatter[Foo] {
  	def read(x: xml.NodeSeq): Option[Foo] = {

	Logger.info("FooXMLF.read: " + x ) 

	val out=for( 
        id <- EXML.fromXML[Long](x \ "id");
      	name <- EXML.fromXML[String](x \ "name");
        age <- EXML.fromXML[Option[Int]](x \ "age")
      ) yield(Foo(id, name, age))

	  Logger.info("FooXMLF.read: Yielded: " + out )
	  out

    }

    def write(f: Foo, base: xml.NodeSeq): xml.NodeSeq = {
      <foo>
        <id>{ f.id }</id>
        <name>{ f.name }</name>
        { EXML.toXML(f.age, <age/>) }
      </foo>
    }
  }  



  implicit object Body extends XMLFormatter[Body] {
  	def read(x: xml.NodeSeq): Option[Body] = {
      Logger.info("BodyXMLF.read: " + x ) 
      val out=for( 
        foo <- EXML.fromXML[Foo](x \ "foo")
      ) yield(Body(foo))

	  Logger.info("BodyXMLF.read: Yielded: " + out )
	  out
    }

    def write(b: Body, base: xml.NodeSeq): xml.NodeSeq = {
	  <Body>
        { EXML.toXML(b.foo, <foo/>) }
      </Body>
    }
  }




  def index = Action {

	// Fails
    //     Ok(ESOAP.toSOAP(Foo(1234, "brutus", Some(23))))

	// Succeeds
	Ok(EXML.toXML(SoapEnvelope(Foo(1234L, "brutus", Some(23)))))

  }

  def foo = Action(parse.xml) { request =>

    Logger.info("request: " + request.body)

    //  val foo: Option[Foo] = ESOAP.fromSOAP[Foo](soapXML)
    // EXML.fromXML[Foo](request.body).map { foo =>
  
    ESOAP.fromSOAP[Body](request.body).map { body =>

  	  Logger.info("Handled Soap, body is: " + body )
  	  Logger.info("Handled Soap, body.foo is: " + body.foo )

//      Ok(EXML.toXML(body.foo))
	  // Write the Full XML Enveolope back
      Ok(EXML.toXML(SoapEnvelope(body.foo)))

    }.getOrElse{
      Logger.error("Unable to parse incoming object!")
      BadRequest("Expecting Foo XML data")
    }
  }
  
}