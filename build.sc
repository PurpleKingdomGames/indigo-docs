import $ivy.`com.lihaoyi::mill-contrib-bloop:$MILL_VERSION`
import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

import $file.scripts.gamemodule

import indigoplugin._

object examples extends mill.Module {

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

    // TODO: Custom component

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
