package com.ang

import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem}
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.util.Timeout
import com.ang.TriviaClient.{CreateQuestion, GetQuestions, GetQuestionsDB, Params}
import org.apache.pekko.http.scaladsl.server.Directives._
import com.ang.errors.BadRequestException
import com.ang.models.QuizQuestion

import scala.concurrent.Future

class TriviaRoutes(triviaClient: ActorRef[TriviaClient.Command])(implicit val system: ActorSystem[_]) {

  import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JSONFormats._

  private implicit val timeout: Timeout = Timeout.create(system.settings.config.getDuration("trivia.routes.ask-timeout"))

  private def getQuestions(params: Params) = triviaClient.ask(GetQuestions(params, _))
  private def getQuestionsDB = triviaClient.ask(GetQuestionsDB)
  private def createQuestion(question: QuizQuestion): Future[QuizQuestion] = triviaClient.ask(CreateQuestion(question, _))

  val triviaRoutes: Route =
  pathPrefix("api") {
    concat(
      pathPrefix("questions") {
        concat(
          pathPrefix("db") {
            pathEnd {
              concat(
                get {
                  complete(getQuestionsDB)
                },
                post {
                  entity(as[QuizQuestion]) { quizQuestion =>
                    onSuccess(createQuestion(quizQuestion)) { performed =>
                      complete((StatusCodes.Created, performed))
                    }
                  }
                }

              )
            }
          },
          pathEnd {
            concat(
              get {
                extract(_.request.uri.query()) { params => {
                  try {
                    val numberOfQuestions = params.get("amount").get.toInt
                    val category = params.get("category").get.toInt
                    val difficulty = params.get("difficulty").get
                    val `type` = params.get("type").get
                    val paramsToSend = Params(numberOfQuestions, category, difficulty, `type`)

                    if (numberOfQuestions < 0 || numberOfQuestions > 50 || category < 9 || category > 32) {
                      throw BadRequestException()
                    }
                    if (!(difficulty.equals("easy") || difficulty.equals("medium") || difficulty.equals("hard"))) {
                      throw BadRequestException()
                    }
                    if (!(`type`.equals("multiple") || `type`.equals("boolean"))) {
                      throw BadRequestException()
                    }
                    complete(getQuestions(paramsToSend))
                  } catch {
                    case _: NoSuchElementException => {
                      complete(StatusCodes.BadRequest)
                    }
                    case err: BadRequestException => {
                      complete(StatusCodes.BadRequest, err.getMessage)
                    }
                  }

                }
                }
              }
            )
          }
        )
      } ,
    )
  }
}
