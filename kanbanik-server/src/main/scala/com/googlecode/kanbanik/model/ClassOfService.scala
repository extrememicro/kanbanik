package com.googlecode.kanbanik.model

import org.bson.types.ObjectId
import com.googlecode.kanbanik.db.HasEntityLoader
import com.googlecode.kanbanik.db.HasMidAirCollisionDetection
import com.googlecode.kanbanik.db.HasMongoConnection
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject

case class ClassOfService(
  id: Option[ObjectId],
  name: String,
  description: String,
  colour: String,
  version: Int) extends HasMongoConnection with HasMidAirCollisionDetection {

  def store: ClassOfService = {
    val idToUpdate = id.getOrElse({
      val obj = ClassOfService.asDBObject(this)
      using(createConnection) { conn =>
        coll(conn, Coll.ClassesOfService) += obj
      }

      return ClassOfService.asEntity(obj)
    })

    val update = $set(
      ClassOfService.Fields.name.toString -> name,
      ClassOfService.Fields.description.toString -> description,
      ClassOfService.Fields.colour.toString -> colour,
      ClassOfService.Fields.version.toString -> { version + 1 }
    )

    ClassOfService.asEntity(versionedUpdate(Coll.ClassesOfService, versionedQuery(idToUpdate, version), update))
  }

  def delete() {
    versionedDelete(Coll.ClassesOfService, versionedQuery(id.get, version))
  }

}

object ClassOfService extends HasMongoConnection with HasEntityLoader {
  object Fields extends DocumentField {
    val description = Value("description")
    val colour = Value("colour")
  }

  def byId(id: ObjectId) = {
    using(createConnection) { conn =>
      val dbObject = coll(conn, Coll.ClassesOfService).findOne(MongoDBObject(Fields.id.toString -> id)).getOrElse(throw new IllegalArgumentException("No such classOfService with id: " + id))
      asEntity(dbObject)
    }
  }

  def all() = {
    using(createConnection) { conn =>
      coll(conn, Coll.ClassesOfService).find().sort(MongoDBObject(ClassOfService.Fields.name.toString -> 1)).map(asEntity(_)).toList
    }
  }
  
  def asEntity(dbObject: DBObject): ClassOfService = {
	new ClassOfService(
      Some(dbObject.get(Fields.id.toString).asInstanceOf[ObjectId]),
      dbObject.get(Fields.name.toString).asInstanceOf[String],
      dbObject.get(Fields.description.toString).asInstanceOf[String],
      dbObject.get(Fields.colour.toString).asInstanceOf[String],
      dbObject.get(Fields.version.toString).asInstanceOf[Int]
    )
  }

  def asDBObject(entity: ClassOfService): DBObject = {
    MongoDBObject(
      Fields.id.toString -> entity.id.getOrElse(new ObjectId),
      Fields.name.toString -> entity.name,
      Fields.version.toString -> entity.version,
      Fields.description.toString -> entity.description,
      Fields.colour.toString -> entity.colour
    )
  }
}