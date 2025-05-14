package pirate.scenes.level.subsystems

import indigo.*
import indigoextras.subsystems.*
import pirate.core.Assets
import pirate.core.LayerKeys
import pirate.core.Model

object CloudsAutomata:

  val signal: SignalReader[(AutomatonSeedValues, SceneNode), AutomatonUpdate] =
    SignalReader { case (seed, node) =>
      node match
        case cloud: Graphic[_] =>
          Signal
            .Lerp(seed.spawnedAt, Point(-150, seed.spawnedAt.y), seed.lifeSpan)
            .map { position =>
              AutomatonUpdate(cloud.moveTo(position))
            }

        case _ =>
          Signal.fixed(AutomatonUpdate.empty)

    }

  val automaton: Automaton =
    Automaton(
      AutomatonNode.OneOf(
        Assets.Clouds.cloud1,
        Assets.Clouds.cloud2,
        Assets.Clouds.cloud3
      ),
      Seconds.zero,
      signal,
      _ => Nil
    )

  val poolKey: AutomataPoolKey = AutomataPoolKey("cloud")

  val automata: Automata[Model] =
    Automata(
      poolKey,
      automaton,
      LayerKeys.smallClouds
    ).withMaxPoolSize(15)
