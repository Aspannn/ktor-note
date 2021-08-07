package kz.aspan.routes

import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kz.aspan.data.collections.Note
import kz.aspan.data.deleteNoteForUser
import kz.aspan.data.getNotesForUser
import kz.aspan.data.requests.DeleteNoteRequest
import kz.aspan.data.saveNotes

fun Route.noteRoutes() {
    route("/getNotes") {
        authenticate {
            get {
                val email = call.principal<UserIdPrincipal>()!!.name
                val notes = getNotesForUser(email)
                call.respond(OK, notes)
            }
        }
    }

    route("/deleteNote") {
        authenticate {
            post {
                val email = call.principal<UserIdPrincipal>()!!.name
                val request = try {
                    call.receive<DeleteNoteRequest>()
                } catch (e: ConcurrentModificationException) {
                    call.respond(BadRequest)
                    return@post
                }

                if (deleteNoteForUser(email, request.id)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }

    route("/addNote") {
        authenticate {
            post {
                val note = try {
                    call.receive<Note>()
                } catch (e: ConcurrentModificationException) {
                    call.respond(BadRequest)
                    return@post
                }

                if (saveNotes(note)) {
                    call.respond(OK)
                } else {
                    call.respond(Conflict)
                }
            }
        }
    }
}