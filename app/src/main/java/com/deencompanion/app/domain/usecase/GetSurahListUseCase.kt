

package com.deencompanion.app.domain.usecase

import com.deencompanion.app.domain.model.Surah
import javax.inject.Inject

class GetSurahListUseCase @Inject constructor() {
    operator fun invoke(): List<Surah> {
        return Surah.ALL_SURAHS
    }
}