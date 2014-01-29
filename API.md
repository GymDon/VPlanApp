#API#
######Version 0.2######

##Accessability##
The API can be accessed under following URLs:
* <https://pvpctutorials.de/VPlanApp/api.php>
* <https://pvpctutorials.de/VPlanApp/api/>
* <https://skirising.no-ip.org/VPlanApp/api.php>
* <https://skirising.no-ip.org/VPlanApp/api/>

---

##Request##
Every API-request needs to specify at least the `action` parameter.
Parameters should be send over HTTP-**POST**

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
* `api` sends the API-version of the client (current version is `0.2`). Not needed but strongly recommended for compatibility
* `u` specifies the username. Needed for all actions.
* `pass` specifies the password. `pass` or `sync` and  `token` are needed for all actions.
* `app` should be sent by apps to tell the server their app name and version
* `os` should be sent by apps to tell the server their operating system
* `stats` should be sent by apps for statistics. `stats` needs to be a JSON-Object with possible values `data`,`wifi`,`android_id` and `adb`
* `sync` is a boolean indicating if this is a syncronization request.  
  If `sync` is `true` a valid `token` can be sent instead of `pass`
* `token` can be sent with `sync=true`
* `hash` can be sent to indicate the (recieved) hash of the cached data
* `lang` two character language code (strongly recommended for `api >= 0.2`)

---

##Response##
Responses are JSON objects.  
The root contains following elements:
* `result` *always*: contains the [result](#result)
* `success` *always*: boolean indicating success of the requested action
* `hash` *always*: the *SHA-256* hash of `result`
* `changed` *always*: boolean indicating difference between given hash and new hash
* `authorized` *always*: boolean indicating valid login
* `token` *if `authorized`*: a single use token valid for max. 24 hours
* `language` *always*: the language used
* `serve_time` *always*: the time needed for processing the request
* `deprecated` *if the action is deprecated*: true
* `warnings` *if warnings occured*: an array of [warnings](#warning)
* `action` *always*: the action performed
* `params` *always*: the parameters from the request

---

###Result###
The result depends on the action

In case of action `all` `result` is an object containing
* `user`: the [user info](#user-info)
* `replacements`: the [replacements](#replacements)
* `ticker`: the [tickers](#tickers)
* `pages`: the [pages](#pages)
* `others`: [extra information](#others) for teachers

For other actions `result` immediately contains the requested informatin corresponding to the `action`:
* `user`: [User Info](#user-info)
* `replacements`: [Replacements](#replacements)
* `ticker`: [Tickers](#tickers)
* `pages`: [Pages](#pages)
* `others`: [Others](#others)
* `auth`: [Auth](#auth)

####User Info####
The user info is a JSON-Object consisting of:
* `user`: the username
* `uid`: the user id
* `gid`: the group id of the main group
* `group`: the main group
* `groups`: array containing all the groups a user is in
* `fullname`: the full name (normally first and last name) of the user
* `student`: boolean indicating if the user is not in the *teachers* group
* `type`: either 'student' or 'teacher'

####Replacements####
The array of replacements contains objects for every replacement.  
These look like this for students:
* `id`
* `grade_pre`: the non-numeric first part of the class
* `grade`: the numeric part of the class
* `grade_last`: the non-numeric last part of the class
* `lesson`
* `teacher`: the short-code of the original teacher
* `replacement`: the short-code of the replaceing teacher or empty
* `room`
* `comment`
* `timestamp`: the timestamp of the day the replacement occurs
* `timestamp_update`: the timestamp of the last update
* `addition`: boolean indicating if the replacement was just added
* `teacher_long`: the full name of the original teacher
* `is_today`: boolean indicating if the replacement is for today
* `teacher_change`: boolean indicating if the teacher is changed as a result of this replacement

And like this for teachers:
* `id`
* `short`: the short-code of the selected teacher
* `teacher`: full name of the specified teacher
* `lesson`
* `grade`: *grade([short-code of original teacher])*
* `room`
* `comment`
* `timestamp`: the timestamp of the day the replacement occurs
* `timestamp_update`: the timestamp of the last update
* `addition`: boolean indicating if the replacement was just added
* `is_today`: boolean indicating if the replacement is for today

####Tickers####
* `id`
* `automatic`: boolean indicationg if this ticker was generated from a note (see [Others](#others))
* `value`: the content of the ticker
* `from_stamp`: the earliest timestamp this ticker should occur
* `to_stamp`: the last timestamp this ticker should occur
* `order`: if multiple tickers are active at the same time this specifies the order

####Pages####
* `id`
* `ordern_num`: ask [@flx5](https://github.com/flx5)
* `content`: the content of the page (HTML)
* `timestamp_from`: the earliest timestamp this page should occur
* `timestamp_end`: the last timestamp this page should occur
* `pupils`: if this page should be displayed for pupils
* `teachers`: if this page should be displayed for teachers

####Others####
* `id`
* `type`: one of t=*teacher*, g=*grade*, r=*room*, s=*school*, a=*supervisor*, n=*note* (are converted to tickers automatically)
* `name`: the name of the subject
* `comment`
* `timestamp`: the timestamp of the day this occurs
* `timestamp_update`: the timestamp of the last update
* `addition`: boolean indicating if this was just added
* `is_today`: boolean indicating if this is for today

####Auth####
The result is a boolean: *true* if logging in was successful, *false* otherwise  
In case of *false* more information is found in the [warnings](#warning)

---

###Warning###
A JSON object containing  
`warning`: the warning code  
a `description` in the used language if available 
other warning-specific fields
