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
@file:JsModule("react-markdown")
@file:JsNonModule

package com.bytelegend.client.app.external

import react.ElementType
import react.PropsWithChildren

@JsName("default")
external val ReactMarkdown: ElementType<ReactMarkdownProps>

// https://www.npmjs.com/package/react-markdown
external interface ReactMarkdownProps : PropsWithChildren {
    var className: String
    var skipHtml: Boolean
    var sourcePos: Boolean
    var rawSourcePos: Boolean
    var transformImageUri: Any
}
