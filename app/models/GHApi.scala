package models

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import scala.concurrent.Future
import scala.util.matching.Regex

object GHApi {
	lazy val root = WS.url("https://api.github.com")
	val defaultPattern = genPattern()
	

	def searchRepository(keyword : String) = {
		val searchPattern = genPattern("keyword")
		root.get().flatMap { response =>
			val searchCall = cleanCall(
						searchPattern replaceFirstIn(
							(response.json \ "repository_search_url").as[String],
							keyword)
					)
			WS.url(searchCall).get().map(_.json)
		}
	}

	def statsRepository(owner : String, repo : String) = {
		val ownerPattern = genPattern("owner")
		val repoPattern = genPattern("repo")
		root.get().flatMap { response =>
			val repoCall = ownerPattern replaceFirstIn(
						repoPattern replaceFirstIn(
							(response.json \ "repository_url").as[String],
							repo),
						owner)
			WS.url(repoCall).get().flatMap { repo =>
				val contribCall = cleanCall((repo.json \ "contributors_url").as[String])
				val commitCall = cleanCall((repo.json \ "commits_url").as[String])
				println(commitCall)
				for {
					con <- WS.url(contribCall).get().map{_.json.as[JsArray]}
					com <- WS.url(commitCall).get().map{_.json.as[JsArray]}
				}
					yield JsObject("contributors" -> con :: "commits" -> com :: Nil)
			}
		}
							
	}

	private[this] def cleanCall(call : String) = defaultPattern replaceAllIn(call, "")

	private [this] implicit def strToSome(str : String) : Option[String] = Some(str)

	private[this] def genPattern(what : Option[String] = None) : Regex = what match {
		case None => "\\{\\??/?([a-z_],?)+\\}".r
		case Some(x) => ("\\{"+x+"\\}").r
	}
}
