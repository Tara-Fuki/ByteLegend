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
package com.bytelegend.client.app.ui

import com.bytelegend.client.app.engine.MOUSE_CLICK_EVENT
import com.bytelegend.client.app.engine.MOUSE_MOVE_EVENT
import com.bytelegend.client.app.engine.MOUSE_OUT_OF_MAP_EVENT
import com.bytelegend.client.utils.jsObjectBackedSetOf
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseOutFunction
import react.RBuilder
import react.State
import react.dom.attrs

/*
 This layer is a transparent layer on top of map canvas layer, it responds to user mouse interaction
 and converts game events.


  -------------------------------------------------------------------------------------------------------------
  |<---------------------------------------------  window.innerWidth ----------------------------------------->|
  |  --------------------------------------------------------------------------------------------------------  |
  |  |                                               gap                                                    |  |
  |  --------------------------------------------------------------------------------------------------------  |
  |                                                                                                            |
  |            ----------------------------------------------------------------------------------              |
  |            |<-------------- map canvas, must be integer multiple of tile size -------------->|             |
  |  -------   |                                                                                 |  ---------  |
  |  | gap |   |                                                                                 |  |  gap  |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                 map canvas                                      |  |       |  |
  |  |     |   |          only mouse events in this area are responded by this class             |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  |     |   |                                                                                 |  |       |  |
  |  -------   |                                                                                 |  ---------  |
  |            |                                                                                 |             |
  |            -----------------------------------------------------------------------------------             |
  |                                                                                                            |
  |  --------------------------------------------------------------------------------------------------------  |
  |  |                                               gap                                                    |  |
  |  --------------------------------------------------------------------------------------------------------  |
  |------------------------------------------------------------------------------------------------------------|

  Map canvas is in center of browser window, mouse events inside this area will be converted to GameMouseEvent (a special
  kind of event which carries game coordinate information):
  1. mouse.move -> GameMouseEvent
  2. mouse.click -> GameMouseEvent

  Note that you don't need to respond to window.onresize event, Game.viewport responds to that event.
 */

const val USER_MOUSE_INTERACTION_LAYER_ID = "user-mouse-interaction-layer"
class UserMouseInteractionLayer : GameUIComponent<GameProps, State>() {
    override fun RBuilder.render() {
        absoluteDiv(
            left = canvasCoordinateInGameContainer.x,
            top = canvasCoordinateInGameContainer.y,
            width = canvasPixelSize.width,
            height = canvasPixelSize.height,
            zIndex = Layer.UserMouseInteraction.zIndex(),
            classes = jsObjectBackedSetOf("user-mouse-interaction-layer")
        ) {
            attrs {
                id = USER_MOUSE_INTERACTION_LAYER_ID
                onClickFunction = {
                    game.eventBus.emit(MOUSE_CLICK_EVENT, toGameMouseEvent(it))
                }
                onMouseMoveFunction = {
                    game.eventBus.emit(MOUSE_MOVE_EVENT, toGameMouseEvent(it))
                }
                onMouseOutFunction = {
                    game.eventBus.emit(MOUSE_OUT_OF_MAP_EVENT, null)
                }
            }
        }
    }
}
