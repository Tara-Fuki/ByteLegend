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
package com.bytelegend.client.app.obj

import com.bytelegend.app.client.api.GameScene
import com.bytelegend.app.client.api.HasBouncingTitle
import com.bytelegend.app.client.api.dsl.UnitFunction
import com.bytelegend.app.shared.PixelCoordinate
import com.bytelegend.app.shared.objects.GameObject
import com.bytelegend.app.shared.objects.GameObjectRole
import com.bytelegend.client.app.ui.mission.BouncingTitleWidget
import com.bytelegend.client.utils.jsObjectBackedSetOf
import react.RBuilder

class BouncingTitleObject(
    override val id: String,
    private val titleText: String,
    private val color: String,
    private val backgroundColor: String,
    private val pixelCoordinate: PixelCoordinate,
    private val onClickFunction: UnitFunction?,
    private val gameScene: GameScene
) : GameObject, HasBouncingTitle {
    override val layer: Int = 0
    override val roles: Set<String> = jsObjectBackedSetOf(GameObjectRole.HasBouncingTitle.toString())
    override var bouncingTitleEnabled: Boolean = true
    override fun renderBouncingTitle(builder: RBuilder) {
        builder.child(BouncingTitleWidget::class) {
            attrs.pixelCoordinate = pixelCoordinate
            attrs.onClickFunction = onClickFunction
            attrs.title = titleText
            attrs.gameScene = gameScene
            attrs.backgroundColor = backgroundColor
            attrs.color = color
        }
    }
}
