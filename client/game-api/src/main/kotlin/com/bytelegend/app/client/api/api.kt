/*
 * Copyright 2021 ByteLegend Technologies and the original author or authors.
 *
 * Licensed under the GNU Affero General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://github.com/ByteLegend/ByteLegend/blob/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bytelegend.app.client.api

import com.bytelegend.app.client.api.dsl.ObjectsBuilder
import com.bytelegend.app.shared.GameMap
import com.bytelegend.app.shared.GridCoordinate
import com.bytelegend.app.shared.GridSize
import com.bytelegend.app.shared.PixelCoordinate
import com.bytelegend.app.shared.PixelSize
import com.bytelegend.app.shared.entities.Player
import com.bytelegend.app.shared.entities.PlayerChallenge
import com.bytelegend.app.shared.entities.PullRequestAnswer
import com.bytelegend.app.shared.entities.PullRequestCheckRun
import com.bytelegend.app.shared.i18n.Locale
import com.bytelegend.app.shared.objects.GameObject
import com.bytelegend.app.shared.objects.GameObjectRole
import kotlinx.coroutines.Deferred
import org.w3c.dom.HTMLImageElement

val HERO_ID = "hero"

interface GameObjectContainer {
    fun <T : GameObject> getByIdOrNull(id: String): T?
    fun <T : GameObject> getById(id: String): T
    fun getPointById(id: String): GridCoordinate
    fun add(gameObject: GameObject)
    fun <T : GameObject> remove(id: String): T?
    fun getByCoordinate(coordinate: GridCoordinate): List<GameObject>
    fun putIntoCoordinate(gameObject: GameObject, newCoordinate: GridCoordinate)
    fun removeFromCoordinate(gameObject: GameObject, oldCoordinate: GridCoordinate)
    fun <T : GameObject> getByRole(role: GameObjectRole): List<T>
}

enum class LogLevel {
    Debug,
    Info,
    Warn,
    Error
}

interface Logger {
    val logLevel: LogLevel
    val debugEnabled: Boolean
    val infoEnabled: Boolean
    val warnEnabled: Boolean
    val errorEnabled: Boolean
    fun debug(message: String)
    fun info(message: String)
    fun warn(message: String)
    fun error(message: String)
}

interface GameRuntime {
    val hero: Character?
    val heroPlayer: Player
    val RRBD: String
    val locale: Locale
    val eventBus: EventBus
    val sceneContainer: GameSceneContainer
    val elapsedTimeSinceStart: Long
    val activeScene: GameScene
    val modalController: ModalController
    val bannerController: BannerController
    val toastController: ToastController

    fun i(textId: String, vararg args: String): String
}

interface GameContainerSizeAware {

    /**
     * Represent the whole "game container" size. The "game container" is usually
     * the whole browser window, but in certain cases it might be a div or so.
     *
     * Note that it's resizeable. The implementation class should properly respond to
     * the resizing operation, like adjust the inside canvas.
     */
    var gameContainerSize: PixelSize
}

/*
When the game map is far bigger than canvas (model 1):
-----------------------------------------------------------------------------------------------
|    The whole game map                                                                       |
|                                                                                             |
|      ----------------------------------------------------------------------------------     |
|      |   The game container (browser window at most of the time)                       |    |
|      |                                                                                 |    |
|      |                                                                                 |    |
|      |      -----------------------------------------------------------------------    |    |
|      |      |  The map canvas/UI container                                         |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      -----------------------------------------------------------------------    |    |
|      |                                                                                 |    |
|      -----------------------------------------------------------------------------------    |
|                                                                                             |
|---------------------------------------------------------------------------------------------|

When the game map is not far bigger than canvas (model 2):
-----------------------------------------------------------------------------------------------
|      The game container (browser window at most of the time)                                |
|                                                                                             |
|      ----------------------------------------------------------------------------------     |
|      |   The UI container (for displaying UI elements, i.e. dropdowns, widgets)        |    |
|      |                                                                                 |    |
|      |                                                                                 |    |
|      |      -----------------------------------------------------------------------    |    |
|      |      |  The map canvas/game map                                             |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      |                                                                      |   |    |
|      |      -----------------------------------------------------------------------    |    |
|      |                                                                                 |    |
|      -----------------------------------------------------------------------------------    |
|                                                                                             |
|---------------------------------------------------------------------------------------------|

How we calculate the container size:

1. Get the game container size and map size.
2. Shrink the game container by ~100 px in all directions to get the "max game canvas size".
3. If game map is bigger than "max game canvas size", use model 1.
4. Otherwise, use model 2.
*/
interface GameCanvasState : GameContainerSizeAware, GameMap {
    /**
     * The canvas' top-left corner pixel coordinate in the game container, and the pixel size
     */
    fun getCanvasCoordinateInGameContainer(): PixelCoordinate

    /**
     * The UI's top-left corner pixel coordinate in the game container
     */
    fun getUICoordinateInGameContainer(): PixelCoordinate

    /**
     * The canvas' top-left corner pixel coordinate in the whole game map, and the pixel size
     */
    fun getCanvasCoordinateInMap(): PixelCoordinate

    fun getCanvasGridCoordinateInMap(): GridCoordinate

    /**
     * The canvas size in pixel, must be integer multiple of tile size
     */
    fun getCanvasPixelSize(): PixelSize

    fun getUIContainerSize(): PixelSize

    fun getCanvasGridSize(): GridSize

    /**
     * Move canvasCoordinateInMap to map's coordinate, controlled by mini map and hero indicator
     */
    fun moveTo(coordinate: PixelCoordinate)

    /**
     * Does canvas cover the whole map?
     * If true, minimap and canvas border is not required.
     */
    val mapCoveredByCanvas: Boolean
}

interface GameRuntimeAware {
    val gameRuntime: GameRuntime
}

interface GameSceneAware {
    val gameScene: GameScene
    val tileSize: PixelSize
        get() = gameScene.map.tileSize
    val gameRuntime: GameRuntime
        get() = gameScene.gameRuntime
}

data class ImageResourceData(
    val imageId: String,
    val size: PixelSize,
    val htmlElement: HTMLImageElement
)

interface GameScene : GameContainerSizeAware, GameRuntimeAware {
    val isActive: Boolean

    /**
     * The map of current scene
     */
    val map: GameMap

    /**
     * The tileset of current scene
     */
    val tileset: ImageResourceData

    /**
     * 2d matrix of blockers.
     * 0 means "nothing".
     * >0 means "it's a blocker for everyone"
     * <0 means "it's a blocker for everyone other than hero, like NPC,other players"
     *
     * So it's a bit tricky: when an NPC steps into a tile, this number decrements by 1,
     * not increment.
     */
    val blockers: Array<Array<Int>>
    val objects: GameObjectContainer
    val canvasState: GameCanvasState
    val playerChallenges: PlayerChallengeContainer
    val logs: PullRequestLogContainer

    fun objects(block: ObjectsBuilder.() -> Unit)
    fun scripts(block: ScriptsBuilder.() -> Unit)

    /**
     * Search a path in the scene, using the predicate to determine wall point.
     */
    fun searchPath(start: GridCoordinate, end: GridCoordinate, wallPredicate: (Int) -> Boolean): List<GridCoordinate>
}

interface PlayerChallengeContainer {
    /**
     * Is the challenge accomplished?
     */
    fun challengeAccomplished(challengeId: String): Boolean

    /**
     * How many star the player gets from the challenge?
     */
    fun challengeStar(challengeId: String): Int

    /**
     * How many star the player gets from the mission?
     */
    fun missionStar(missionId: String): Int

    fun getPlayerChallengesByMissionId(missionId: String): List<PlayerChallenge>

    fun getPullRequestChallengeAnswersByMissionId(missionId: String): List<PullRequestAnswer>
}

interface PullRequestLogContainer {
    fun getLiveLogsByAnswer(answer: PullRequestAnswer, checkRunId: String): List<String>
    fun downloadLogByAnswerAsync(answer: PullRequestAnswer, checkRun: PullRequestCheckRun): Deferred<String>
}

interface GameSceneContainer : GameContainerSizeAware {
    fun getSceneByIdOrNull(mapId: String): GameScene?

    fun getSceneById(mapId: String): GameScene

    /**
     * The current active scene. When the scene is loading (e.g. the game is initializing or
     * switching scenes), return null.
     */
    val activeScene: GameScene?

    /**
     * Load a scene, the following resources will be loaded before proceeding:
     *
     * 1. RRBD/map/{mapId}/map.json
     * 2. RRBD/map/{mapId}/tileset.png
     * 3. RRBD/js/game-{mapId}.js
     * 4. RRBD/i18n/{mapId}/{locale}.json
     *
     * If {switchAfterLoad} is true, the UI will switch after loading.
     */
    fun loadScene(mapId: String, switchAfterLoad: Boolean = true, onFinish: suspend (GameScene?, GameScene) -> Unit = { _, _ -> })
}

interface ModalController {
    val visible: Boolean
    fun showModal(content: String, title: String? = null)
}

interface ToastController {
    fun addToast(headerHtml: String, bodyHtml: String, autoHideMs: Int = 0)
}

class Banner(
    val contentHtml: String,
    /**
     * success/warning, etc.
     */
    val variant: String,
    /**
     * After how many seconds will this banner be closed automatically.
     * There will be an indicator counting down to zero.
     * 0 means no auto closing.
     */
    val autoCloseSeconds: Int = 0,
)

interface BannerController {
    fun showBanner(banner: Banner)
}
