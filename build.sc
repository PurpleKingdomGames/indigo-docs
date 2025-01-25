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

}
