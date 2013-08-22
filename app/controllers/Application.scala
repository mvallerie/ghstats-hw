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
  
	def search(keyword : String) = Action {
		Async {
			GHApi.searchRepository(keyword).map { repos =>
				println("Repos : "+repos)
    				Ok(repos)
			}
		}
	}
	
	def stats(owner : String, repo : String) = Action {
		Async {
			GHApi.statsRepository(owner, repo).map { stats =>
				println("Stats : "+stats)
				Ok(stats)
			}
		}
	}

}
