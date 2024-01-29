package com.ang.models

case class QuizQuestion(`type`: String, difficulty: String, category: String, question: String, correct_answer: String, incorrect_answers: List[String])

