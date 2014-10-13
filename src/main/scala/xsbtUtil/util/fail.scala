package xsbtUtil.util

import sbt._

import Keys.TaskStreams

object fail {
	object FailureException extends FeedbackProvidedException
	
	def logging(streams:TaskStreams, messages:String*):Nothing	= {
		messages foreach { streams.log error _ }
		throw FailureException
	}
}
