package com.deencompanion.app.domain.usecase.home

import android.content.Context
import com.deencompanion.app.domain.model.DailyDua
import com.deencompanion.app.domain.repository.HomeRepository
import javax.inject.Inject

class GetDailyDuaUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(context: Context): DailyDua {
        return repository.getDailyDua(context)
    }
}
