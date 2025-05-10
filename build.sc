import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $file.scripts.gamemodule

import indigoplugin._

object examples extends mill.Module {

  object `actors-and-performers` extends mill.Module {

    object actors extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Actors Example")
          .withWindowSize(800, 600)
    }

    object `actors-with-physics` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Actors with Physics Example")
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

  // This is the root directory of the workspace / project.
  private val workspaceDir: os.Path =
    sys.env
      .get("MILL_WORKSPACE_ROOT")
      .map(os.Path(_))
      .getOrElse(os.pwd)

  object importers extends mill.Module {

    object `tiled-loaded` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Tiled (Loaded)")
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

    object `scene-management` extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("Scene Management")
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

    object radiobuttons extends gamemodule.GameModule {
      val indigoOptions: IndigoOptions =
        makeIndigoOptions("UI Components - Radio buttons")
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
