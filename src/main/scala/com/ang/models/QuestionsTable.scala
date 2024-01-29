package com.ang.models
import com.ang.profile.PostgresProfile.api._


class QuestionsTable(tag: Tag) extends Table[QuizQuestion](tag, Some("public"), "questions") {
  def `type` = column[String]("type")
  def difficulty = column[String]("difficulty")
  def category = column[String]("category")
  def question = column[String]("question")
  def correct_answer = column[String]("correct_answer")
  def incorrect_answers = column[List[String]]("incorrect_answers")

  def * = (`type`, difficulty, category, question, correct_answer, incorrect_answers) <> ((QuizQuestion.apply _).tupled, QuizQuestion.unapply)
}