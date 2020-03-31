package no.nav.helse.mottak.v1.arbeidstaker

import no.nav.helse.Metadata
import no.nav.helse.SoknadId
import no.nav.helse.mottak.v1.selvstendignaringsrivende.SoknadV1MottakService
import org.slf4j.LoggerFactory

internal class ArbeidstakerutbetalingSoknadMottakService(
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

        val outgoing = soknad
            .medSoknadId(soknadId)
            .somOutgoing()

        logger.info("Legger på kø")
        soknadV1KafkaProducer.produce(
            metadata = metadata,
            soknad = outgoing
        )

        return soknadId
    }
}
