package no.nav.helse.mottak.v1.arbeidstaker

import no.nav.helse.AktoerId
import no.nav.helse.CorrelationId
import no.nav.helse.Metadata
import no.nav.helse.SoknadId
import no.nav.helse.dokument.Dokument
import no.nav.helse.dokument.DokumentGateway
import no.nav.helse.mottak.v1.selvstendignaringsrivende.SoknadV1MottakService
import org.slf4j.LoggerFactory
import java.net.URI

internal class ArbeidstakerutbetalingSoknadMottakService(
    private val dokumentGateway: DokumentGateway,
    private val soknadV1KafkaProducer: ArbeidstakerutbetalingSoknadKafkaProducer
) {

    private companion object {
        private val logger = LoggerFactory.getLogger(SoknadV1MottakService::class.java)
    }

    internal suspend fun leggTilProsessering(
        soknadId: SoknadId,
        metadata: Metadata,
        soknad: ArbeidstakerutbetalingsSoknadIncoming
    ): SoknadId {

        val correlationId = CorrelationId(metadata.correlationId)

        logger.trace("Lagrer vedlegg")
        val vedleggUrls = lagreVedlegg(
            aktoerId = soknad.søkerAktørId,
            vedlegg = soknad.vedlegg,
            correlationId = correlationId
        )

        val outgoing: ArbeidstakerutbetalingsSoknadOutgoing = soknad
            .medVedleggTitler()
            .medVedleggUrls(vedleggUrls)
            .medSoknadId(soknadId)
            .somOutgoing()

        logger.info("Legger på kø")
        soknadV1KafkaProducer.produce(
            metadata = metadata,
            soknad = outgoing
        )

        return soknadId
    }

    private suspend fun lagreVedlegg(
        aktoerId: AktoerId,
        correlationId: CorrelationId,
        vedlegg: List<Vedlegg>
    ) : List<URI> {
        logger.info("Lagrer ${vedlegg.size} vedlegg.")
        return dokumentGateway.lagreDokmenter(
            dokumenter = vedlegg.somDokumenter(),
            correlationId = correlationId,
            aktoerId = aktoerId
        )
    }
}

private fun List<Vedlegg>.somDokumenter() = map {
    Dokument(
        content = it.content,
        contentType = it.contentType,
        title = it.title
    )
}.toSet()
