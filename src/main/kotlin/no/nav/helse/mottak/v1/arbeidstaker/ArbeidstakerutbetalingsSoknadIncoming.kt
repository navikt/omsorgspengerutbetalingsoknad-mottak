package no.nav.helse.mottak.v1.arbeidstaker

import no.nav.helse.SoknadId
import no.nav.helse.AktoerId
import no.nav.helse.mottak.v1.JsonKeys
import org.json.JSONObject

internal class ArbeidstakerutbetalingsSoknadIncoming(json: String) {
    private val jsonObject = JSONObject(json)


    internal val søkerAktørId = AktoerId(jsonObject.getJSONObject(JsonKeys.søker).getString(
        JsonKeys.aktørId
    ))

    internal fun medSoknadId(soknadId: SoknadId): ArbeidstakerutbetalingsSoknadIncoming {
        jsonObject.put(JsonKeys.søknadId, soknadId.id)
        return this
    }

    internal fun somOutgoing() =
        ArbeidstakerutbetalingsSoknadOutgoing(
            jsonObject
        )

}

internal class ArbeidstakerutbetalingsSoknadOutgoing(internal val jsonObject: JSONObject) {
    internal val soknadId = SoknadId(jsonObject.getString(JsonKeys.søknadId))

}
