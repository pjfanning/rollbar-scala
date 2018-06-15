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

package com.storecove.rollbar.test

import java.net.InetAddress

import com.storecove.rollbar.RollbarNotifierFactory
import org.json4s.JsonAST.JField
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.{LoggerFactory, MDC}

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
 * Created by acidghost on 06/06/15.
 */
class RollbarNotifierSpec extends FlatSpec with Matchers {

    val notifierName = "rollbar-scala"
    val notifierVersion = "0.0.1"

    val apiKey = "FAKE_API_KEY"
    val environment = "test"
    val language = "scala"
    val platform = "JVM"
    val url = "http://www.fakeurl.com/api/v2"
    val defaultLanguage = "scala"
    val defaultUrl = "https://api.rollbar.com/api/1/item/"

    val logger = LoggerFactory.getLogger(getClass)

    {
        MDC.put("user", "acidghost")
        MDC.put("platform", platform)
        MDC.put("environment", environment)
    }

    "A RollbarNotifier" should "return the right parameters" in {
        val notifier = RollbarNotifierFactory.getNotifier(apiKey, environment)
        notifier.getUrl should be (defaultUrl)
        notifier.getApiKey should be (apiKey)
        notifier.getEnvironment should be (environment)
        notifier.getLanguage should be (defaultLanguage)
        val notifier2 = RollbarNotifierFactory.getNotifier(apiKey, environment, language = language, url = url)
        notifier2.getLanguage should be (language)
        notifier2.getUrl should be (url)
    }

    //TODO: mock rollbar Web Service to test this
    ignore should "notify to rollbar correctly" in {
        val notifier = RollbarNotifierFactory.getNotifier(apiKey, environment)
        val response = notifier.notify("INFO", "This is a test error notification.", None, getMDC)
        response.onComplete {
            case Success(s) =>
                logger.info(compact(s))
            case Failure(f) => throw f
        }
    }

    it should "print the correct data" in {
        val notifier = RollbarNotifierFactory.getNotifier(apiKey, environment)

        def filter(v: Any) = v match {
            // removing those fileds because may be difficult
            // to test (e.g. the stack trace depends on 3th party code)
            case JField("frames", _) | JField("raw", _) | JField("timestamp", _) => true
            case _ => false
        }

        val p1 = notifier.buildPayload("INFO", "This is a test error notification.", Some(new Exception("this is the exception")), getMDC)
        val p1mock = buildMockPayload("INFO", "This is a test error notification.", Some(new Exception("this is the exception")), getMDC)
        val p1filtered = p1.removeField(filter(_))
        compact(p1filtered) should be (compact(p1mock))

        val p2 = notifier.buildPayload("INFO", "This is a test error notification.", None, getMDC)
        val p2mock = buildMockPayload("INFO", "This is a test error notification.", None, getMDC)
        val p2filtered = p2.removeField(filter(_))
        compact(p2filtered) should be (compact(p2mock))
    }

    private def buildMockPayload(level: String, message: String, throwable: Option[Throwable], mdc: mutable.Map[String, String]) = {
        val root = "access_token" -> apiKey

        val data = ("environment" -> environment) ~
            ("level" -> level) ~
            ("platform" -> mdc.getOrElse("platform", platform)) ~
            ("framework" -> mdc.getOrElse("framework", platform)) ~
            ("language" -> language)

        val rightBody = throwable match {
            case Some(t) =>
                JField("trace_chain",
                    List(List("exception" ->         //use a List to simulate the removal of "frames"
                        ("class" -> t.getClass.getCanonicalName) ~
                        ("message" -> t.getMessage)
                    ))
                )
            case None =>
                JField("message", "body" -> message)
        }
        val body = JField("body", rightBody)

        val person = "person" -> (mdc.get("user") match {
            case Some(id) => Some(("id" -> id) ~ ("username" -> mdc.get("username")) ~ ("email" -> mdc.get("email")))
            case _ => None
        })

        val localhost = InetAddress.getLocalHost
        val server = "server" -> (("host" -> localhost.getHostName) ~ ("ip" -> localhost.getHostAddress))

        val notifier = "notifier" -> ("name" -> notifierName) ~ ("version" -> notifierVersion)

        root ~ ("data" -> (data ~ body ~ person ~ server ~ notifier))
    }

    private def getMDC = {
        val mdc = MDC.getCopyOfContextMap
        if (mdc == null) {
            mutable.Map.empty[String, String]
        } else {
            mapAsScalaMap(mdc)
        }
    }

}
