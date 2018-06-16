// This code is licensed under the MIT License.
//
// Copyright (c) 2015 Andrea Jemmett
// Copyright (c) 2018 Kontainers
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.storecove.rollbar.appenders

import com.storecove.rollbar.{RollbarNotifier, RollbarNotifierDefaults, RollbarNotifierFactory}
import org.slf4j.MDC

import scala.collection.JavaConverters.mapAsScalaMap
import scala.collection.mutable

/**
 * Created by andrea on 08/06/15.
 */
trait AbstractAppender {

    protected var enabled: Boolean = true
    protected var onlyThrowable: Boolean = true

    protected var url: String = RollbarNotifierDefaults.defaultUrl
    protected var apiKey: String = _
    protected var environment: String = _
    protected var notifyLevelString: String = "ERROR"

    protected val rollbarNotifier: RollbarNotifier = RollbarNotifierFactory.getNotifier(apiKey, environment)

    def setNotifyLevel(level: String): Unit

    protected def notifyLevel: Any = "ERROR"

    def setEnabled(enabled: Boolean): Unit = this.enabled = enabled

    def setOnlyThrowable(onlyThrowable: Boolean): Unit = this.onlyThrowable = onlyThrowable

    def setApiKey(apiKey: String): Unit = {
        this.apiKey = apiKey
        rollbarNotifier.setApiKey(apiKey)
    }

    def setEnvironment(environment: String): Unit = {
        this.environment = environment
        rollbarNotifier.setEnvironment(environment)
    }

    def setUrl(url: String): Unit = {
        this.url = url
        rollbarNotifier.setUrl(url)
    }

    def getEnabled: Boolean = enabled
    def getOnlyThrowable: Boolean = onlyThrowable
    def getApiKey: String = apiKey
    def getEnvironment: String = environment
    def getUrl: String = url
    def getNotifyLevel: String = notifyLevelString

    protected def getMDCContext: mutable.Map[String, String] = {
        val mdc = MDC.getCopyOfContextMap
        if (mdc == null) {
            mutable.Map.empty[String, String]
        } else {
            mapAsScalaMap(mdc)
        }
    }

}
