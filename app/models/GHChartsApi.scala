package models

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import scala.concurrent.Future
import scala.util.matching.Regex

object GHChartsApi {
	// TODO split into several parts ?
	def getChartsData(owner : String, repo : String, token : Option[String]) = {
                       GHApi.statsRepository(owner, repo, token).map { stats =>
                                val commitsPerUser = (stats \ "commits" \\ "email")
                                        .groupBy(identity)
                                        .map { x => Json.obj("name" -> x._1, "commits" -> x._2.length)  }
					.toSeq.sortWith { (x,y) =>
						(x \ "name").as[String] <= (y \ "name").as[String]
					}

                                // Takes one per two elements of the list and extract the date (10 first chars)
                                val dates = (stats \ "commits" \\ "date")
                                        .zipWithIndex.filter{_._2 %2 == 0}
                                        .unzip._1
                                        .map { _.as[String] take 10 }

				// Extract the commits timeline from raw data
                                val commitsTimeline = dates.groupBy(identity)
                                        .map { x => (x._1 -> x._2.length)  }
                                        .toSeq.sorted
                                        .map { x => Json.obj("date" -> x._1, "commits" -> x._2) }
				
				// Returns JSON formatted data	
                                Json.obj(
                                	"contributors" -> commitsPerUser,
                                	"timeline" -> commitsTimeline
                                )
                        }							
	}
}
