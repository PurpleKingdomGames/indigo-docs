import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $ivy.`io.indigoengine::mill-indigo:0.21.1`, indigoplugin._
import $ivy.`org.typelevel::scalac-options:0.1.7`, org.typelevel.scalacoptions._

trait GameModule extends MillIndigo {
  def scalaVersion   = "3.6.4"
  def scalaJSVersion = "1.19.0"

  override def scalacOptions = T {
    val flags = super.scalacOptions() ++
      ScalacOptions.defaultTokensForVersion(ScalaVersion.unsafeFromString(scalaVersion())) ++
      Seq("-Xfatal-warnings")

    /*
    By default, we get the following flags:

    -encoding, utf8, -deprecation, -feature, -unchecked,
    -language:experimental.macros, -language:higherKinds,
    -language:implicitConversions, -Xkind-projector,
    -Wvalue-discard, -Wnonunit-statement, -Wunused:implicits,
    -Wunused:explicits, -Wunused:imports, -Wunused:locals,
    -Wunused:params, -Wunused:privates, -Xfatal-warnings
     */

    // Alledgedly unused local definitions are unavoidable in Ultraviolet,
    // so we remove the flag to make things tolerable.
    flags.filterNot(_ == "-Wunused:locals")
  }

  def indigoOptions: IndigoOptions

  def makeIndigoOptions(title: String): IndigoOptions =
    IndigoOptions.defaults
      .withTitle(title)
      .withWindowSize(550, 400)
      .withAssetDirectory(os.RelPath.rel / "assets")
      .withBackgroundColor("black")
      .excludeAssets {
        case p if p.endsWith(os.RelPath.rel / ".gitkeep")  => true
        case p if p.endsWith(os.RelPath.rel / ".DS_Store") => true
        case _                                             => false
      }

  // This is the root directory of the Mill workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  def indigoGenerators: IndigoGenerators =
    IndigoGenerators("generated")
      .generateConfig("Config", indigoOptions)
      .embedFont(
        moduleName = "DefaultFont",
        font = workspaceDir / "generator-data" / "Minecraft.ttf",
        fontOptions = FontOptions(
          fontKey = "DefaultFont",
          fontSize = 16,
          charSet = CharSet.ASCII,
          color = RGB.White,
          antiAlias = false,
          layout = FontLayout.default
        ),
        imageOut = workspaceDir / "assets" / "generated"
      )
      .listAssets("Assets", indigoOptions.assets)

  val indigoVersion = "0.21.1"

  def ivyDeps =
    Agg(
      ivy"io.indigoengine::indigo-json-circe::$indigoVersion",
      ivy"io.indigoengine::indigo::$indigoVersion",
      ivy"io.indigoengine::indigo-extras::$indigoVersion"
    )

  object test extends ScalaJSTests {
    def ivyDeps = Agg(
      ivy"org.scalameta::munit::1.1.1"
    )

    override def moduleKind = T(mill.scalajslib.api.ModuleKind.CommonJSModule)

    def testFramework: mill.T[String] = T("munit.Framework")
  }

}
