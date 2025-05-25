import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $file.scripts.gamemodule

import indigoplugin._

object demos extends mill.Module {

  // This is the root directory of the workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  object snake extends gamemodule.GameModule {

    val indigoOptions: IndigoOptions =
      makeIndigoOptions("Snake - Made with Indigo")
        .withWindowWidth(720)
        .withWindowHeight(516)
        .withBackgroundColor("black")
        .withAssetDirectory(os.RelPath("demos/snake/assets"))
        .excludeAssets {
          case p if p.startsWith(os.RelPath("asset_dev")) => true
          case _                                          => false
        }

    override def indigoGenerators: IndigoGenerators =
      IndigoGenerators("snake.generated")
        .listAssets("Assets", indigoOptions.assets)
        .generateConfig("SnakeConfig", indigoOptions)

    override def ivyDeps = T {
      super.ivyDeps() ++ Agg(ivy"org.scalacheck::scalacheck:1.15.3")
    }
  }

  object `snake-in-5-minutes` extends gamemodule.GameModule {
    val indigoOptions: IndigoOptions =
      makeIndigoOptions("Snake in 5 minutes - Made with Indigo")
        .withWindowSize(400, 400)
  }

  object `the-cursed-pirate` extends gamemodule.GameModule {

    val indigoOptions: IndigoOptions =
      makeIndigoOptions("The Cursed Pirate - Made with Indigo")
        .withWindowWidth(1280)
        .withWindowHeight(720)
        .withBackgroundColor("black")
        .withAssetDirectory(os.RelPath("demos/the-cursed-pirate/assets"))
        .excludeAssetPaths {
          case p if p.contains("unused")                       => true
          case p if p.contains("Captain Clown Nose Data.json") => true
        }

    override def indigoGenerators: IndigoGenerators =
      IndigoGenerators("pirate.generated")
        .listAssets("Assets", indigoOptions.assets)
        .generateConfig("Config", indigoOptions)
        .embedAseprite(
          "CaptainAnim",
          workspaceDir / "demos" / "the-cursed-pirate" / "assets" / "captain" / "Captain Clown Nose Data.json"
        )

    override def ivyDeps = T {
      super.ivyDeps() ++ Agg(ivy"org.scalacheck::scalacheck:1.15.3")
    }
  }

}

object features extends mill.Module {

  // This is the root directory of the workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  object `actors-and-performers` extends mill.Module {

    object actors extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Actors Example")
          .withWindowSize(800, 600)
    }

    object performers extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Performers Example")
          .withWindowSize(800, 600)
    }

    object `performers-with-physics` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Performers with Physics Example")
          .withWindowSize(800, 600)
    }

  }

  object materials extends mill.Module {

    object bitmap extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Bitmap Material Example")
    }

    object filltypes extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Fill Types Example")
    }

    object imageeffects extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("ImageEffects Material Example")
    }

  }

  object physics extends mill.Module {

    object basics extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Basic Physics Example")
          .withWindowSize(800, 600)
    }

  }

  object primitives extends mill.Module {

    object graphic extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Graphic Example")
    }

    object shapes extends mill.Module {

      object box extends gamemodule.GameModule {
        val indigoOptions: IndigoOptions =
          makeIndigoOptions("Box Shape Example")
      }

      object circle extends gamemodule.GameModule {
        val indigoOptions: IndigoOptions =
          makeIndigoOptions("Circle Shape Example")
      }

      object line extends gamemodule.GameModule {
        val indigoOptions: IndigoOptions =
          makeIndigoOptions("Line Shape Example")
      }

      object polygon extends gamemodule.GameModule {
        val indigoOptions: IndigoOptions =
          makeIndigoOptions("Polygon Shape Example")
      }

    }

    object text extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Text Example")

      /** The generator includes the usual config and asset listing generation, but also includes a
        * font embedding step. It reads a font file from somewhere in your file system and outputs
        * the rendered font to you asset directory. If you need more variations of size or style
        * then you need to add another embedding step.
        */
      override def indigoGenerators: IndigoGenerators =
        IndigoGenerators("generated")
          .generateConfig("Config", indigoOptions)
          .embedFont(
            moduleName = "KiwiSodaFont",
            font = workspaceDir / "generator-data" / "KiwiSoda.ttf",
            fontOptions = FontOptions(
              fontKey = "KiwiSoda",
              fontSize = 16,
              charSet = CharSet.ASCII,
              color = RGB.White,
              antiAlias = false,
              layout = FontLayout.normal
            ),
            imageOut = workspaceDir / "assets" / "generated"
          )
          .listAssets("Assets", indigoOptions.assets)
    }

  }

  object scenes extends mill.Module {

    object minimal extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Minimal Scene Example")
    }

  }

  object shaders extends mill.Module {

    object basic extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Basic Custom Shader")
    }

  }

  object ui extends mill.Module {

    object button extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Button")
    }

    object `component-group` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - ComponentGroup")
    }

    object `component-list` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - ComponentList")
    }

    object custom extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Custom Components")
    }

    object label extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Label")
    }

    object hitarea extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - HitArea")
    }

    object input extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Input")
    }

    object maskedpane extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - MaskedPane")
    }

    object scrollpane extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - ScrollPane")
    }

    object switch extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Switch")
    }

    object textarea extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - TextArea")
    }

    object window extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Window")
    }

  }

}

object guides extends mill.Module {

  // This is the root directory of the workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  object actors extends mill.Module {

    object `actors-with-physics` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Actors with Physics Example")
          .withWindowSize(800, 600)
    }

  }

  object assets extends mill.Module {

    object preloader extends gamemodule.GameModule {

      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Preloader / Loading Screen Example")
          .withWindowWidth(1280)
          .withWindowHeight(720)
          .withBackgroundColor("black")
          .withAssetDirectory(os.RelPath("guides/assets/preloader/assets"))
          .excludeAssetPaths {
            case p if p.contains("unused")                       => true
            case p if p.contains("Captain Clown Nose Data.json") => true
          }
  
      override def indigoGenerators: IndigoGenerators =
        IndigoGenerators("preloader.generated")
          .listAssets("Assets", indigoOptions.assets)
          .generateConfig("Config", indigoOptions)
          .embedAseprite(
            "CaptainAnim",
            workspaceDir / "guides" / "assets" / "preloader" / "assets" / "captain" / "Captain Clown Nose Data.json"
          )

    }
  }

  object importers extends mill.Module {

    object `tiled-loaded` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Tiled (Loaded)")
    }

  }

  object scenes extends mill.Module {

    object `scene-management` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Scene Management")
    }

  }

  object ui extends mill.Module {

    object radiobuttons extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Radio buttons")
    }

  }

}
