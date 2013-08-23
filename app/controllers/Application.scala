package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results.Async
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import models.GHApi

object Application extends Controller {

	def index = Action {
		Ok(views.html.index("Your new application is ready."))
	}
  
	def apiSearch(keyword : String) = Action {
		Async {
			GHApi.searchRepository(keyword).map { repos =>
				//println("Repos : "+repos)
    				Ok(repos)
			}
		}
	}
	
	def apiStats(owner : String, repo : String) = Action {
		Async {
			GHApi.statsRepository(owner, repo).map { stats =>

				// TODO : ordering
				val commitsPerUser = (stats \ "commits" \\ "email")
					.groupBy(identity)
					.map { x => Json.toJson(Map("name" -> x._1, "commits" -> Json.toJson(x._2.length)))  }
					.toSeq

				// Takes one per two elements of the list and extract the date (10 first chars)
				val dates = (stats \ "commits" \\ "date")
                                        .zipWithIndex.filter{_._2 %2 == 0}
                                        .unzip._1
                                        .map { _.as[String] take 10 }
				val commitsTimeline = dates.groupBy(identity)
                                        .map { x => (x._1 -> x._2.length)  }
                                        .toSeq.sorted
					.map { x => Json.toJson(Map("date" -> Json.toJson(x._1), "commits" -> Json.toJson(x._2))) }

				val jResult = Json.toJson(
					Map(
						"contributors" -> commitsPerUser,
						"timeline" -> commitsTimeline
					)
				)

				Ok(jResult)
			}
		}
	}

	def search(keyword : String) = Action {
		Ok
	}

	def stats(owner : String, repo : String) = Action {
		Ok(views.html.stats())
	}

}
