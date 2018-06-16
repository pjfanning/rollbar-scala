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

import org.apache.log4j.helpers.LogLog
import org.apache.log4j.spi.{LoggingEvent, ThrowableInformation}
import org.apache.log4j.{AppenderSkeleton, Level}

/**
 * Created by acidghost on 08/06/15.
 */
class Log4jAppender extends AppenderSkeleton with AbstractAppender {

    override def append(event: LoggingEvent): Unit = {
        if (enabled) {
            try {
                if (event.getLevel.isGreaterOrEqual(notifyLevel)) {
                    val hasThrowable = event.getThrowableInformation != null || event.getMessage.isInstanceOf[Throwable]
                    if (!onlyThrowable || hasThrowable) {
                        rollbarNotifier.notify(event.getLevel.toString, event.getMessage.toString, getThrowable(event), getMDCContext)
                    }
                }
            } catch {
                case e: Exception =>
                    val stackTrace = e.getStackTrace.map(trace => trace.toString).mkString("\n")
                    LogLog.error("error=" + e.getClass.getName + " with message=" + e.getMessage + "\n" + stackTrace)
            }
        }
    }

    override def requiresLayout(): Boolean = true

    override def close(): Unit = {}


    override def activateOptions(): Unit = {
        if (this.apiKey == null || this.apiKey.isEmpty) {
            println("No apiKey set for the appender named [" + getName + "].")
        } else if (this.environment == null || this.environment.isEmpty) {
            println("No environment set for the appender named [" + getName + "].")
        } else {
            println(s"PARAMETERS SET\n\n$apiKey / $environment\n")
            super.activateOptions()
        }
    }

    protected def getThrowable(event: LoggingEvent): Option[Throwable] = {
        event.getThrowableInformation match {
            case throwableInfo: ThrowableInformation => Some(throwableInfo.getThrowable)
            case _ => event.getMessage match {
                case throwable: Throwable => Some(throwable)
                case _ => None
            }
        }
    }

    override protected def notifyLevel: Level = Level.toLevel(notifyLevelString)

    def setNotifyLevel(notifyLevel: String): Unit = notifyLevelString = notifyLevel
}
