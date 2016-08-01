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
  // QUERY-0
  // ===========================================================================================================================================================
  val topProductNames0 = quote {
    (for {
      category <- Categories
      product <- Products if category.id == product.categoryId
    } yield {
      (product.name, product.rating)
    })
      .sortBy(_._2)(Ord.desc)
  }

  /* compile error message:
  Error:(_, 17) exception during macro expansion:
  java.lang.IllegalStateException: The monad composition can't be expressed using applicative joins. Faulty expression: 'category.id == product.categoryId'. Free variables: 'List(category)'.
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
    mysqlDatabase run topProductNames0
  */

  // ===========================================================================================================================================================
  // QUERY-1
  // ===========================================================================================================================================================
  val topProductNames4 = quote {
    Categories
      .flatMap(category =>
        Products
          .withFilter(product => category.id == product.categoryId)
          .map(product => (product.name, product.rating))
      )
      .sortBy(_._2)
  }

  /* compile error message:
  Error:(_, 17) exception during macro expansion:
  java.lang.IllegalStateException: The monad composition can't be expressed using applicative joins. Faulty expression: 'category.id == product.categoryId'. Free variables: 'List(category)'.
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
    mysqlDatabase run topProductNames4

   */

  // ===========================================================================================================================================================
  // QUERY-2
  // ===========================================================================================================================================================
  val topProductNames2 = quote {
    Categories
      .join(Products).on(_.id == _.categoryId)
      .join(Comments).on(_._2.id == _.productId)
      .map { case (((category, product), comment)) =>
        (product.name, product.rating)
      }
      .sortBy(_._2)
  }

  /* compile macro output:
  Information:(_, 17) SELECT x4.name, x4.rating FROM category x3 INNER JOIN product x4 ON x3.id = x4.category_id INNER JOIN comment x6 ON x4.id = x6.product_id ORDER BY x4.rating ASC
  mysqlDatabase run topProductNames2
   */

  // ===========================================================================================================================================================
  // RUN
  // ===========================================================================================================================================================
  mysqlDatabase run topProductNames2


}
