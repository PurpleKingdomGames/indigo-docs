package snake.init

import indigo.*

object StartupData:

  def initialise(
      viewConfig: ViewConfig
  ): Outcome[Startup[StartupData]] =
    Outcome(
      Startup.Success(createStartupData(viewConfig))
    )

  def createStartupData(viewConfig: ViewConfig): StartupData =
    val blockSize = viewConfig.gridSquareSize

    StartupData(
      viewConfig = viewConfig,
      staticAssets = StaticAssets(
        apple = GameAssets.apple(blockSize),
        snake = GameAssets.snake(blockSize),
        wall = GameAssets.wall(blockSize)
      )
    )

final case class StartupData(viewConfig: ViewConfig, staticAssets: StaticAssets)

final case class StaticAssets(
    apple: Graphic[Material.Bitmap],
    snake: Graphic[Material.Bitmap],
    wall: Graphic[Material.Bitmap]
)

final case class ViewConfig(
    gridSize: Size,
    gridSquareSize: Int,
    footerHeight: Int,
    viewport: Size
):
  val center: Point = viewport.toPoint / 2

object ViewConfig:

  val default: ViewConfig =
    val gridSquareSize     = 12
    val gridSize           = Size(30, 20)
    val footerHeight       = 36

    ViewConfig(
      gridSize,
      gridSquareSize,
      footerHeight,
      (gridSize * gridSquareSize) + footerHeight
    )
