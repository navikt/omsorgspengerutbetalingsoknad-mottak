import no.nav.helse.dusseldorf.ktor.core.*
import no.nav.helse.mottak.v1.SoknadV1Incoming

internal fun SoknadV1Incoming.validate() {
    val violations = mutableSetOf<Violation>()

    if (!søkerAktørId.id.erKunSiffer()) {
        violations.add(
            Violation(
                parameterName = "søker.aktørId",
                parameterType = ParameterType.ENTITY,
                reason = "Ikke gyldig Aktør ID.",
                invalidValue = søkerAktørId.id
            )
        )
    }

    if (violations.isNotEmpty()) {
        throw Throwblem(ValidationProblemDetails(violations))
    }
}
