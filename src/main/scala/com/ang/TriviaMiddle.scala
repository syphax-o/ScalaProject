package com.ang

import com.ang.JSONFormats.quizResponseFormat
import com.ang.TriviaClient.Params
import com.ang.models.{Questions, QuizQuestion, QuizResponse}
import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{HttpMethods, HttpRequest}
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.util.Success

object TriviaMiddle {

  sealed trait Command//définition d'un trait une interface et elle et seald veut dire qu'elle sera etendu juste ici dans ce fichier

  final case class GetQuestionsAPI(params: Params, replyTo: ActorRef[Questions] ) extends Command
  /*final pour dire qu'elle ne peut pas être hérité
    case class pour scala et elles ont les méthode déja implementé come apply to string etc
    replyTo contient la ref pour l'actor qu'il faut rep et indique que cette actor s'attend à recevoir des messages
    de type Questions

  */

  def apply(): Behavior[Command] = triviaMiddle()//elle retourne le Bhavior qu'on a fait juste en bas


  private def getQuestionsFromAPI(params: Params, replyTo: ActorRef[Questions] )= {
    implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext: ExecutionContextExecutor = system.executionContext//pour exécuter des opérations asynchrones en scala pour chaque actor

    val config = ConfigFactory.load("application.conf").getConfig("Trivia")
    val apiURL = config.getConfig("external").getString("api-url")
    val queryParams = "?amount="+params.amount.toString + "&category="+params.category + "&difficulty=" + params.difficulty + "&type=" + params.`type`
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = apiURL + queryParams
    )

    val http = Http(system)//initialise le client HTTP
    val responseFuture = http.singleRequest(request)/*
    singleRequest est une méthode du client HTTP qui envoie une requête HTTP unique.
    elle retourne Future[HttpResponse]
    */
    var questions: List[QuizQuestion] = List()

    responseFuture.onComplete {
      case Success(value) => {//value contiendra la valeur réussie
        Unmarshal(value.entity).to[String].map(body =>{
          //Unmarshal est une méthode d'Akka HTTP utilisée pour désérialiser (ou « démarshaliser ») le contenu de la réponse.
          val quizResponse = body.parseJson.convertTo[QuizResponse]/*
          ette ligne prend une chaîne JSON, la parse et la convertit en une instance d'un type de données Scala défini par l'utilisateur, QuizResponse, ce qui facilite la manipulation de ces données dans le code Scala.
          */
          questions = quizResponse.results
          replyTo ! Questions(questions)//cette ligne envoie un message contenant les questions
        })
      }
    }

  }

  def triviaMiddle(): Behavior[Command] =
    Behaviors.receiveMessage {
      case GetQuestionsAPI(params: Params, replyTo: ActorRef[Questions]) =>
        getQuestionsFromAPI(params, replyTo)
        Behaviors.same
    }

}
