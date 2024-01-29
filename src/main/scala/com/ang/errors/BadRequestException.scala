package com.ang.errors

final case class BadRequestException(private val message: String = "Any of given arguments have not accepted values",
                                 private val cause: Throwable = None.orNull)
  extends Exception(message, cause)