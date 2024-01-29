package com.ang.dao

import com.ang.models.{QuizQuestion}
import com.ang.profile.PostgresProfile.api._

import scala.concurrent.Future
object QuestionsDAO extends BaseDao {
  def createQuestion(question: QuizQuestion): Future[QuizQuestion] =
    questionsTable.returning(questionsTable.map(question => question)) += question
  def getQuestions : Future[Seq[QuizQuestion]] = questionsTable.result

}