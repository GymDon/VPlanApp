#API#
######Version 0.2######

##Accessability##
The API can be accessed under following URLs:
* <https://pvpctutorials.de/VPlanApp/api.php>
* <https://pvpctutorials.de/VPlanApp/api/>
* <https://skirising.no-ip.org/VPlanApp/api.php>
* <https://skirising.no-ip.org/VPlanApp/api/>

##Request##
Every API-request needs to specify at least the `action` parameter.
Parameters should be send over HTTP-`POST`

###Action###
Possible actions are:
* `user` for accessing information about the specified user
* `ticker` for getting the tickers
* `replacements` for getting all replacements for the user's courses
* `pages` for getting special pages (i.e. before holidays)
* `others` for getting extra information for teachers
* `auth` for validating login information
* `all` for getting a combination of `user`,`replacements`,`ticker`,`pages` and `others`

###Parameters###
* `api` sends the API-version of the client (current version is `0.2`)
  Not needed but strongly recommended for compatibility
* `u` specifies the username
  Needed for all actions.
* `pass` specifies the password
  `pass` or `sync` and  `token` are needed for all actions.
* `app` should be sent by apps to tell the server their app name and version
* `os` should be sent by apps to tell the server their operating system
* `stats` should be sent by apps for statistics
  `stats` needs to be a JSON-Object with possible values `data`,`wifi`,`android_id` and `adb`
* `sync` is a boolean indicating if this is a syncronization request
  if `sync` is `true` a valid `token` can be sent instead of `pass`
* `token` can be sent with `sync=true`
* `hash` can be sent to indicate the (recieved) hash of the cached data

##Response##
Comming soon.
