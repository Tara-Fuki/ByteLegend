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

import com.bytelegend.app.client.api.EventListener
import com.bytelegend.client.app.engine.DefaultGameScene
import com.bytelegend.client.app.engine.GAME_CLOCK_20MS_EVENT
import com.bytelegend.client.utils.jsObjectBackedSetOf
import com.bytelegend.client.app.obj.character.CharacterSprite
import com.bytelegend.client.app.obj.getSpriteBlockOnCanvas
import react.RBuilder
import react.RComponent
import react.Props
import react.State
import react.setState

interface PlayerNamesProps : GameProps
interface PlayerNamesState : State

class PlayerNames : GameUIComponent<PlayerNamesProps, PlayerNamesState>() {
    private val on50HzClockListener: EventListener<Nothing> = {
        setState { }
    }

    override fun RBuilder.render() {
        if (!game.heroPlayer.isAnonymous && !game._hero!!.outOfCanvas() && game.heroPlayer.map == activeScene.map.id) {
            renderOne(game.heroPlayer.nickname, game._hero!!, true)
        }

        game.activeScene.unsafeCast<DefaultGameScene>()
            .players
            .getDrawableCharacters()
            .forEach {
                renderOne(it.player.nickname, it, false)
            }
    }

    private fun RBuilder.renderOne(playerNickName: String, sprite: CharacterSprite, isHero: Boolean) {
        val imageBlockOnCanvas = sprite.getSpriteBlockOnCanvas(activeScene)
        val x = imageBlockOnCanvas.x + canvasCoordinateInGameContainer.x + activeScene.map.tileSize.width / 2
        val y = imageBlockOnCanvas.y + canvasCoordinateInGameContainer.y - 10
        child(PlayerNameSpan::class) {
            attrs.x = x
            attrs.y = y
            attrs.name = playerNickName
            attrs.isHero = isHero
        }
    }

    override fun componentDidMount() {
        super.componentDidMount()
        props.game.eventBus.on(GAME_CLOCK_20MS_EVENT, on50HzClockListener)
    }

    override fun componentWillUnmount() {
        super.componentWillUnmount()
        props.game.eventBus.remove(GAME_CLOCK_20MS_EVENT, on50HzClockListener)
    }
}

interface PlayerNameSpanProps : Props {
    // coordinate in game container
    var x: Int
    var y: Int
    var name: String
    var isHero: Boolean
}

class PlayerNameSpan : RComponent<PlayerNameSpanProps, State>() {
    override fun RBuilder.render() {
        absoluteDiv(
            left = props.x,
            top = props.y,
            zIndex = Layer.PlayerNames.zIndex(),
            classes = if (props.isHero)
                jsObjectBackedSetOf("yellow-text-black-shadow", "nickname-span")
            else
                jsObjectBackedSetOf("white-text-black-shadow", "nickname-span")
        ) {
            +props.name
        }
    }

    override fun shouldComponentUpdate(nextProps: PlayerNameSpanProps, nextState: State): Boolean {
        return props.x != nextProps.x || props.y != nextProps.y || props.name != nextProps.name
    }
}
