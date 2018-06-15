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

package com.storecove.rollbar

import org.json4s.JObject

import scala.collection.mutable
import scala.concurrent.Future

/**
 * Created by acidghost on 06/06/15.
 */
trait RollbarNotifier {

    protected val notifierName = "rollbar-scala"
    protected val notifierVersion = "0.0.1"

    protected var url: String = _
    protected var apiKey: String = _
    protected var environment: String = _
    protected var language: String = _
    protected var platform: String = _

    protected def log(x: Any) = println(s"[${classOf[RollbarNotifier]}] - $x")

    def getUrl: String = url
    def getApiKey: String = apiKey
    def getEnvironment: String = environment
    def getLanguage: String = language
    def getPlatform: String = platform

    def setUrl(url: String): Unit = this.url = url
    def setApiKey(apiKey: String): Unit = this.apiKey = apiKey
    def setEnvironment(environment: String): Unit = this.environment = environment
    def setLanguage(language: String): Unit = this.language = language
    def setPlatform(platform: String): Unit = this.platform = platform

    def notify(level: String, message: String, throwable: Option[Throwable], mdc: mutable.Map[String, String]): Future[String]

    protected[rollbar] def buildPayload(level: String, message: String, throwable: Option[Throwable], mdc: mutable.Map[String, String]): JObject

}
