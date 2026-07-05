package com.deencompanion.app.domain.usecase



import android.content.Context
import com.deencompanion.app.domain.model.Azkar
import com.deencompanion.app.domain.repository.AzkarRepository
import javax.inject.Inject

class GetAzkarByTypeUseCase @Inject constructor(
    private val repository: AzkarRepository
) {
    operator fun invoke(context: Context, type: String): List<Azkar> {
        return repository.getAzkarByType(context, type)
    }
}