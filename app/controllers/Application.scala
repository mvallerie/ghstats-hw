package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results.Async
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import models._

object Application extends Controller {

	def index = Action {
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

}
