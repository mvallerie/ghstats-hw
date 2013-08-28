package util

// Global settings values
object Settings {
	val API_ROOT = "https://api.github.com"
	val CLIENT_ID = "113d8485f8f0baae9b8d"
	val CLIENT_SECRET = "a7a8a6c932046a87e38c5f3da0addb5e7b2c5b69"
	val GH_OAUTH_URL = "https://github.com/login/oauth/authorize?client_id=%s&state=%s"
	val GH_OAUTH_CONFIRM_URL = "https://github.com/login/oauth/access_token"
	val NB_COMMITS_USED = "100"
}
