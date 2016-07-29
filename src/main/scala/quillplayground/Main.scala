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

}
