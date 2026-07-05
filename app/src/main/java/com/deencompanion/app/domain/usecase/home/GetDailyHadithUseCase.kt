package com.deencompanion.app.domain.usecase.home

import android.content.Context
import com.deencompanion.app.domain.model.DailyHadith
import com.deencompanion.app.domain.repository.HomeRepository
import javax.inject.Inject

class GetDailyHadithUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(context: Context): DailyHadith {
        return repository.getDailyHadith(context)
    }
}
