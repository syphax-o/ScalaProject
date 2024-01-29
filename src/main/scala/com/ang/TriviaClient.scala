package com.ang

import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import com.ang.dao.QuestionsDAO
import com.ang.models.{Questions, QuizQuestion}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
object TriviaClient {
  sealed trait Command
  final case class GetQuestions(params: Params, replyTo : ActorRef[Questions]) extends Command
  final case class GetQuestionsDB(replyTo : ActorRef[Questions]) extends Command
  final case class CreateQuestion(question: QuizQuestion, replyTo : ActorRef[QuizQuestion]) extends Command

  final case class Params (amount: Int,
                          category: Int,
                          difficulty: String,
                          `type`: String)
  def apply(triviaMiddle: ActorRef[TriviaMiddle.GetQuestionsAPI]): Behavior[Command] = triviaClient(triviaMiddle)

  private def triviaClient(triviaMiddle: ActorRef[TriviaMiddle.GetQuestionsAPI]): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetQuestions(params: Params, replyTo: ActorRef[Questions] ) =>
        triviaMiddle ! TriviaMiddle.GetQuestionsAPI(params, replyTo)
        Behaviors.same

      case GetQuestionsDB(replyTo : ActorRef[Questions]) => {
        QuestionsDAO.getQuestions.onComplete{
          case Success(value) =>{
            replyTo ! Questions(value)
          }
        }
        Behaviors.same
      }

      case CreateQuestion(question : QuizQuestion, replyTo: ActorRef[QuizQuestion]) =>
        QuestionsDAO.createQuestion(question)
        replyTo ! question
        Behaviors.same
    }

}
