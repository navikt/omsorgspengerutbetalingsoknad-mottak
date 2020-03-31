package no.nav.helse.mottak.v1.arbeidstaker

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveStream
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import no.nav.helse.getSoknadId
import no.nav.helse.mottak.v1.selvstendignaringsrivende.metadata

internal fun Route.ArbeidstakerutbetalingsøknadApi(
    soknadV1MottakService: ArbeidstakerutbetalingSoknadMottakService
) {
    post("v1/arbeidstaker/soknad") {
        val soknadId = call.getSoknadId()
        val metadata = call.metadata()
        val soknad = call.arbeidstakerutbetalingsøknad()
        soknadV1MottakService.leggTilProsessering(
            soknadId = soknadId,
            metadata = metadata,
            soknad = soknad
        )
        call.respond(HttpStatusCode.Accepted, mapOf("id" to soknadId.id))
    }
}


private suspend fun ApplicationCall.arbeidstakerutbetalingsøknad() : ArbeidstakerutbetalingsSoknadIncoming {
    val json = receiveStream().use { String(it.readAllBytes(), Charsets.UTF_8) }
    val incoming =
        ArbeidstakerutbetalingsSoknadIncoming(json)
    incoming.validate()
    return incoming
}
