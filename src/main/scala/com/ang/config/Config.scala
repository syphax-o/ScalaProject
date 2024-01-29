package com.ang.config

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val databaseConfig = config.getConfig("trivia.postgres")
}