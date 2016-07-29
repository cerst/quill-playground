import sbt._

object Dependencies {

  val quillVersion = "0.8.0"

  // Apache License 2.0
  val quillAsync = "io.getquill" %% "quill-async" % quillVersion

  val libraries = Seq[ModuleID](
    quillAsync
  )

}
