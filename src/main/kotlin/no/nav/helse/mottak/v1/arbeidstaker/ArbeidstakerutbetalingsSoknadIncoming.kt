package no.nav.helse.mottak.v1.arbeidstaker

import no.nav.helse.SoknadId
import no.nav.helse.AktoerId
import no.nav.helse.mottak.v1.JsonKeys
import org.apache.commons.codec.binary.Base64
import org.json.JSONObject
import java.net.URI

internal class ArbeidstakerutbetalingsSoknadIncoming(json: String) {
    private val jsonObject = JSONObject(json)
    internal val vedlegg: List<Vedlegg>

    private fun hentVedlegg() : List<Vedlegg> {
        val vedlegg = mutableListOf<Vedlegg>()
        jsonObject.getJSONArray(JsonKeys.vedlegg).forEach {
            val vedleggJson = it as JSONObject
            vedlegg.add(
                Vedlegg(
                    content = Base64.decodeBase64(vedleggJson.getString(JsonKeys.content)),
                    contentType = vedleggJson.getString(JsonKeys.contentType),
                    title = vedleggJson.getString(JsonKeys.title)
                )
            )
        }
        return vedlegg.toList()
    }

    init {
        vedlegg = hentVedlegg()
        jsonObject.remove(JsonKeys.vedlegg)
    }

    internal val søkerAktørId = AktoerId(jsonObject.getJSONObject(JsonKeys.søker).getString(
        JsonKeys.aktørId
    ))

    internal fun medSoknadId(soknadId: SoknadId): ArbeidstakerutbetalingsSoknadIncoming {
        jsonObject.put(JsonKeys.søknadId, soknadId.id)
        return this
    }

    internal fun medVedleggTitler() : ArbeidstakerutbetalingsSoknadIncoming{
        val listeOverTitler = mutableListOf<String>()
        for(vedlegg in vedlegg){
            listeOverTitler.add(vedlegg.title)
        }
        jsonObject.put(JsonKeys.titler, listeOverTitler)
        return this
    }

    internal fun medVedleggUrls(vedleggUrls: List<URI>) : ArbeidstakerutbetalingsSoknadIncoming {
        jsonObject.put(JsonKeys.vedleggUrls, vedleggUrls)
        return this
    }

    internal fun somOutgoing() =
        ArbeidstakerutbetalingsSoknadOutgoing(
            jsonObject
        )

}

internal class ArbeidstakerutbetalingsSoknadOutgoing(internal val jsonObject: JSONObject) {
    internal val soknadId = SoknadId(jsonObject.getString(JsonKeys.søknadId))
    internal val vedleggUrls = hentVedleggUrls()

    private fun hentVedleggUrls() : List<URI> {
        val vedleggUrls = mutableListOf<URI>()
        jsonObject.getJSONArray(JsonKeys.vedleggUrls).forEach {
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
