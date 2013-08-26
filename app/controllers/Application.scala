package controllers

import util.Settings._

import play.api._
import play.api.mvc._
import play.api.mvc.Results.Async
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.libs.Codecs._

import models._


object Application extends Controller {
	def index = Action { implicit request =>
		Ok(views.html.index("Let's search a Github repository :)."))
	}
  
	def apiSearch(keyword : String) = Action {
		Async {
			GHApi.searchRepository(keyword).map { repos =>
    				Ok(repos)
			}
		}
	}

	def apiStats(owner : String, repo : String) = Action {
		Async {
			GHApi.statsRepository(owner, repo).map { stats =>
				Ok(stats)
			}
		}
	}
	
	def apiCharts(owner : String, repo : String) = Action {
		Async {
			GHChartsApi.getChartsData(owner, repo).map { chartsData =>
				Ok(chartsData)
			}
		}
	}

	def auth(code : String = "", state : String = "") = Action { request =>
		// TODO I hate ifs ... !
		if(code.isEmpty) {
			import scala.util.Random
			val state = sha1((for(i <- 1 to 20) yield Random.nextPrintableChar).mkString)
			Redirect(GH_OAUTH_URL.format(CLIENT_ID, state)).withSession(
				"state" -> state
			)
		} else {
			request.session.get("state").filter(_ == state).map { s =>
				Async {
					val content = Map("client_id" -> Seq(CLIENT_ID), "client_secret" -> Seq(CLIENT_SECRET), "code" -> Seq(code))
					WS.url(GH_OAUTH_CONFIRM_URL).withHeaders("Accept" -> "application/json").post(content).map { response =>
						Redirect(routes.Application.index).withSession(
							"token" -> (response.json \ "access_token").as[String]
						)
					}
				}
			}.getOrElse {
				Redirect(routes.Application.index).withNewSession
			}
		}
	}

}
