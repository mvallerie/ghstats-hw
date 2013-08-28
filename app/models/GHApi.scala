package models

import util.Settings._

import play.api._
import play.api.mvc._
import play.api.libs.ws.WS
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json._

import scala.concurrent.Future
import scala.util.matching.Regex

object GHApi {
	val defaultPattern = genPattern()
	
	// Search a repository from GitHub
	def searchRepository(keyword : String, token : Option[String]) = {
		// Replacing {keyword} with keyword value in URI
		val searchPattern = genPattern("keyword")
		GHWS.root(token).get().flatMap { response =>
			val searchCall = cleanCall(
						searchPattern replaceFirstIn(
							(response.json \ "repository_search_url").as[String],
							keyword)
					)
			
			// Call API and returns resulting JSON
			GHWS.url(searchCall, token).get().map(_.json)
		}
	}

	// Get needed data from a repository (commits and contributors)
	def statsRepository(owner : String, repo : String, token : Option[String]) = {
		// Replacing {owner} and {repo} with corresponding values in URI
		val ownerPattern = genPattern("owner")
		val repoPattern = genPattern("repo")
		GHWS.root(token).get().flatMap { response =>
			val repoCall = ownerPattern replaceFirstIn(
						repoPattern replaceFirstIn(
							(response.json \ "repository_url").as[String],
							repo),
						owner)

			// Calling API to get URIs to call, and calling those URIs. Finally, returning resulting JSON
			GHWS.url(repoCall, token).get().flatMap { repo =>
				val contribCall = cleanCall((repo.json \ "contributors_url").as[String])
				val commitCall = cleanCall((repo.json \ "commits_url").as[String])
				for {
					con <- GHWS.url(contribCall, token).get().map{_.json.as[JsArray]}
					com <- GHWS.url(commitCall, token).withQueryString(("per_page", NB_COMMITS_USED)).get().map{_.json.as[JsArray]}
				}
					yield JsObject("contributors" -> con :: "commits" -> com :: Nil)
			}
		}
							
	}


	// ====================================================
	// Helpers ============================================
	// ====================================================
	
	// To remove unused {param} from Github URIs
	private[this] def cleanCall(call : String) = defaultPattern replaceAllIn(call, "")

	// A bit dangerous ? Needed for below method
	private [this] implicit def strToSome(str : String) : Option[String] = Some(str)

	// This is maybe cleaner than something like : ' what : String = "" ' ?
	private[this] def genPattern(what : Option[String] = None) : Regex = what match {
		case None => "\\{\\??/?([a-z_],?)+\\}".r
		case Some(x) => ("\\{"+x+"\\}").r
	}
}


// WS wrapper, useful for token
object GHWS {
	def url(url : String, token : Option[String] = None) = {
		token.map { t =>
			WS.url(url).withQueryString(("access_token", t))
		}.getOrElse {
			WS.url(url)
		}
	}

	def root(token : Option[String] = None) = url(API_ROOT, token)
}
