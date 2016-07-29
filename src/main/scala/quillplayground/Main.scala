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
      // comment the third join -> the query compiles
      comment <- Comments.join(_.productId == product.id)
    } yield (product.rating, product.name)).sortBy(_._1)(Ord.desc).take(100)
  }

  mysqlDatabase run topProductNames

  /* compile error message:
  Error:(44, 17) exception during macro expansion:
  java.lang.IllegalStateException: The monad composition can't be expressed using applicative joins. Faulty expression: 'x2.rating'. Free variables: 'List(x2)'., Faulty expression: 'x3x3'. Free variables: 'List(x3x3)'.
    at io.getquill.util.Messages$.fail(Messages.scala:8)
    at io.getquill.context.sql.Prepare$$anonfun$1.apply(Prepare.scala:29)
    at io.getquill.context.sql.Prepare$$anonfun$1.apply(Prepare.scala:29)
    at scala.Option.map(Option.scala:146)
    at io.getquill.context.sql.Prepare$.apply(Prepare.scala:29)
    at io.getquill.context.sql.SqlContextMacro.prepare(SqlContextMacro.scala:19)
    at io.getquill.context.QueryMacro$class.runQuery(QueryMacro.scala:48)
    at io.getquill.context.sql.SqlContextMacro.runQuery(SqlContextMacro.scala:13)
    at io.getquill.context.ContextMacro$class.run(ContextMacro.scala:69)
    at io.getquill.context.ContextMacro$class.runExpr(ContextMacro.scala:51)
    at io.getquill.context.ContextMacro$class.run(ContextMacro.scala:21)
    at io.getquill.context.sql.SqlContextMacro.run(SqlContextMacro.scala:13)
    mysqlDatabase run topProductNames
  */

}
