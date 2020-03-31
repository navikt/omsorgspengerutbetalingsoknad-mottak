package no.nav.helse.mottak.v1.selvstendignaringsrivende

import no.nav.helse.SoknadId
import no.nav.helse.AktoerId
import no.nav.helse.mottak.v1.JsonKeys
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import java.net.URI



internal class SoknadV1Incoming(json: String) {
    private val jsonObject = JSONObject(json)
    internal val vedlegg: List<Vedlegg>

    internal val sokerFodselsNummer = jsonObject.getJSONObject(JsonKeys.søker).getString(
        JsonKeys.fødselsnummer
    )

    private fun hentVedlegg(): List<Vedlegg> = vedleggsFilerTilJson(
        JsonKeys.vedlegg
    ).toList()

    private fun vedleggsFilerTilJson(jsonKey: String): MutableList<Vedlegg> {
        val vedleggsFiler: MutableList<Vedlegg> = mutableListOf()
        jsonObject.getJSONArray(jsonKey).forEach {
            val vedleggJson = it as JSONObject
            vedleggsFiler.add(
                Vedlegg(
                    content = Base64.decodeBase64(vedleggJson.getString(JsonKeys.content)),
                    contentType = vedleggJson.getString(JsonKeys.contentType),
                    title = vedleggJson.getString(JsonKeys.title)
                )
            )
        }
        return vedleggsFiler
    }

    init {
        vedlegg = hentVedlegg()
        jsonObject.remove(JsonKeys.vedlegg)
    }

    internal val søkerAktørId = AktoerId(jsonObject.getJSONObject(JsonKeys.søker).getString(
        JsonKeys.aktørId
    ))

    internal fun medLegeerklæringUrls(vedleggUrls: List<URI>): SoknadV1Incoming {
        jsonObject.put(JsonKeys.vedlegg, vedleggUrls)
        return this
    }

    internal fun medSoknadId(soknadId: SoknadId): SoknadV1Incoming {
        jsonObject.put(JsonKeys.søknadId, soknadId.id)
        return this
    }

    internal fun somOutgoing() =
        SoknadV1Outgoing(jsonObject)

}

internal class SoknadV1Outgoing(internal val jsonObject: JSONObject) {
    internal val soknadId = SoknadId(jsonObject.getString(JsonKeys.søknadId))
    internal val vedlegg = hentVedleggUrls(JsonKeys.vedlegg)

    private fun hentVedleggUrls(jsonkey: String): List<URI> {
        val vedleggUrls = mutableListOf<URI>()
        jsonObject.getJSONArray(jsonkey).forEach {
            vedleggUrls.add(URI(it as String))
        }
        return vedleggUrls.toList()
    }
}

data class Vedlegg(
    val content: ByteArray,
    val contentType: String,
    val title: String
)
