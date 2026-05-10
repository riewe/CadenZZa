package by.riewe.cadence.domain.model

data class DetailedFuelBalance(
    val truck: VehicleBalance,
    val currentTrailer: TrailerBalance,
    val allTrailersInCadence: List<SimpleTrailerInfo>
)

data class VehicleBalance(
    val initial: Int,
    val refueled: Int,
    val remaining: Int
)

data class TrailerBalance(
    val number: String,
    val initial: Int,
    val refueled: Int,
    val remaining: Int
)

data class SimpleTrailerInfo(
    val number: String,
    val totalRefueled: Int,
    val refuelCount: Int
)