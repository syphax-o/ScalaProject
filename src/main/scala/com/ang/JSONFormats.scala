package com.ang

import com.ang.models.{Questions, QuizQuestion, QuizResponse}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JSONFormats {
  import DefaultJsonProtocol._

  implicit val quizQuestionFormat: RootJsonFormat[QuizQuestion] = jsonFormat6(QuizQuestion)
  implicit val quizResponseFormat: RootJsonFormat[QuizResponse] = jsonFormat2(QuizResponse)
  implicit val questionsJsonFormat: RootJsonFormat[Questions] = jsonFormat1(Questions.apply)

}
