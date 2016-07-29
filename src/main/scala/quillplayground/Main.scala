package quillplayground

import io.getquill.{MysqlAsyncContext, SnakeCase}


object Main extends App {

  val mysqlDatabase = new MysqlAsyncContext[SnakeCase]("myslql")

  import mysqlDatabase._
  import scala.concurrent.ExecutionContext.Implicits.global
  // ===========================================================================================================================================================
  // SCHEMA
  // ===========================================================================================================================================================
  case class Category(id: Long, name: String)

  case class Product(id: Long, name: String, rating: Double, categoryId: Long)

  case class Comment(id: Long, content: String, productId: Long)

  val Categories = quote {
    query[Category]
  }

  val Products = quote {
    query[Product]
  }

  val Comments = quote {
    query[Comment]
  }

  // ===========================================================================================================================================================
  // QUERY
  // ===========================================================================================================================================================
  val topProductNames = quote {
    (for {
      (category, product) <- Categories.join(Products).on(_.id == _.categoryId)
      comment <- Comments.join(_.productId == product.id)
    } yield (product.rating, product.name)).sortBy(_._1)(Ord.desc).take(100)
  }

  mysqlDatabase run topProductNames

}
