/*
 * Copyright 2023 Mikhail Titov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.d1s.beam.bundle.html

import io.ktor.server.config.*
import kotlinx.html.HEAD
import kotlinx.html.unsafe
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CounterScriptIndexModule : IndexModule, KoinComponent {

    private val config by inject<ApplicationConfig>()

    private val vkAdsId
        get() = config.propertyOrNull("bundle.counter.vk-ads")?.getString()

    override fun HEAD.render(renderParameters: RenderParameters) {
        vkAdsId?.let {
            renderVkAdsScript(it)
        }
    }

    private fun HEAD.renderVkAdsScript(counterId: String) {
        unsafe {
            +"""
                <!-- Top.Mail.Ru counter -->
                <script type="text/javascript">
                var _tmr = window._tmr || (window._tmr = []);
                _tmr.push({id: "$counterId", type: "pageView", start: (new Date()).getTime()});
                (function (d, w, id) {
                  if (d.getElementById(id)) return;
                  var ts = d.createElement("script"); ts.type = "text/javascript"; ts.async = true; ts.id = id;
                  ts.src = "https://top-fwz1.mail.ru/js/code.js";
                  var f = function () {var s = d.getElementsByTagName("script")[0]; s.parentNode.insertBefore(ts, s);};
                  if (w.opera == "[object Opera]") { d.addEventListener("DOMContentLoaded", f, false); } else { f(); }
                })(document, window, "tmr-code");
                </script>
                <noscript><div><img src="https://top-fwz1.mail.ru/counter?id=$counterId;js=na" style="position:absolute;left:-9999px;" alt="Top.Mail.Ru" /></div></noscript>
                <!-- /Top.Mail.Ru counter -->
            """.trimIndent()
        }
    }
}