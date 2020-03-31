package no.nav.helse.kafka

import no.nav.helse.Metadata
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.serialization.StringSerializer

internal data class TopicEntry<V>(
    val metadata: Metadata,
    val data: V
)
internal data class TopicUse<V>(
    val name: String,
    val valueSerializer : Serializer<TopicEntry<V>>
) {
    internal fun keySerializer() = StringSerializer()
}

internal object Topics {
    internal const val SELVSTENDIG_NÆRINGSDRIVENDE_SØKNAD_MOTTATT = "privat-omsorgspengerutbetalingsoknad-mottatt"
    internal const val ARBEIDSTAKER_UTBETALING_SØKNAD_MOTTATT = "privat-oms-utbetalingsoknad-arbeidstaker-mottatt"
    internal const val DITT_NAV_BESKJED = "aapen-brukernotifikasjon-nyBeskjed-v1"
}
