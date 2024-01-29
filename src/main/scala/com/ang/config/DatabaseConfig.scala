package com.ang.config

import com.ang.profile.PostgresProfile

trait DatabaseConfig extends Config {

  val driver: PostgresProfile.type = PostgresProfile

  import driver.api._

  def db: driver.backend.JdbcDatabaseDef = Database.forConfig("trivia.postgres")

  implicit val session: Session = db.createSession()
}