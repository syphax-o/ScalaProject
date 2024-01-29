package com.ang

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route

import scala.util.{Failure, Success}

object TriviaServer {
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 3001).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val triviaMiddleActor = context.spawn(TriviaMiddle(), "TriviaMiddleActor")//création d'un nouvel actor
      val triviaClientActor = context.spawn(TriviaClient(triviaMiddleActor), "TriviaClientActor")//création d'un nouvel actor
      context.watch(triviaClientActor)//surveiller pour savoir s'il a un problème fatal ou s'arrête

      val routes = new TriviaRoutes(triviaClientActor)(context.system)//création d'une route et la relier pour l'actor triviaClientActor et context.system c'est le system d'actor
      startHttpServer(routes.triviaRoutes)(context.system)//lance le serveur avec le system d'actor

      Behaviors.empty//Behaviors est un objet de akka ou la nouvel version pekko il cree des comportements d'actors et empty cela veut dire que l'actor ne fait rien et ne répond à aucun messages
    }
    ActorSystem[Nothing](rootBehavior, "TriviaAkkaHttpServer")//pour lancer le system d'actor nothing veut dire que ce type d'actor n'est pas destiné à recevoir un message utilisateur
  }
}
