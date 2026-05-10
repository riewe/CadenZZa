package by.riewe.cadence.domain.usecase

import by.riewe.cadence.domain.repository.RefuelRepository
import by.riewe.cadence.domain.repository.TrailerChangeRepository
import java.util.Date
import javax.inject.Inject

/**
 * UseCase для добавления новой заправки.
 */
class AddRefuelUseCase @Inject constructor(
    private val refuelRepository: RefuelRepository,
    private val trailerChangeRepository: TrailerChangeRepository
) {
    suspend operator fun invoke(params: RefuelParams): Result<Long> {
        // Если заправляем прицеп, проверяем наличие активной сцепки
        if (params.trailerFuel > 0) {
            val canRefuel = refuelRepository.canRefuelTrailer(params.cadenceId)
            if (!canRefuel) {
                return Result.failure(IllegalStateException("Нет активного прицепа"))
            }
        }

        return try {
            val id = refuelRepository.addRefuel(
                cadenceId = params.cadenceId,
                date = Date(params.date), // Конвертируем Long Timestamp в Date
                location = params.location,
                truckFuel = params.truckFuel,
                adBlue = params.adBlue,
                trailerFuel = params.trailerFuel,
                cardName = params.cardName
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class RefuelParams(
    val cadenceId: Long,
    val date: Long,
    val location: String,
    val truckFuel: Int,
    val adBlue: Int,
    val trailerFuel: Int,
    val cardName: String
)
