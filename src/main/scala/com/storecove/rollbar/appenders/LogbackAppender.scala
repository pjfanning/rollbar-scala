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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.{ILoggingEvent, ThrowableProxy}
import ch.qos.logback.core.UnsynchronizedAppenderBase

/**
 * Created by acidghost on 08/06/15.
 */
class LogbackAppender extends UnsynchronizedAppenderBase[ILoggingEvent] with AbstractAppender {

    override def append(event: ILoggingEvent): Unit = {
        if (enabled) {
            try {
                if (event.getLevel.isGreaterOrEqual(notifyLevel)) {
                    val hasThrowable = event.getThrowableProxy != null
                    if (!onlyThrowable || hasThrowable) {
                        rollbarNotifier.notify(event.getLevel.toString, event.getMessage, getThrowable(event), getMDCContext)
                    }
                }
            } catch {
                case t: Throwable => {
                    println("Error sending error notification! error=" +
                        t.getClass.getName + " with message=" + t.getMessage)
                }
            }
        }
    }

    override def start(): Unit = {
        if (this.apiKey == null || this.apiKey.isEmpty) {
            this.addError("No apiKey set for the appender named [" + getName + "].")
        } else if (this.environment == null || this.environment.isEmpty) {
            this.addError("No environment set for the appender named [" + getName + "].")
        } else {
            super.start()
        }
    }

    protected def getThrowable(event: ILoggingEvent): Option[Throwable] = {
        event.getThrowableProxy match {
            case throwableProxy: ThrowableProxy => Some(throwableProxy.getThrowable)
            case _ => None
        }
    }

    override def notifyLevel: Level = Level.toLevel(notifyLevelString)

    def setNotifyLevel(notifyLevel: String): Unit = notifyLevelString = notifyLevel

}
