package com.deencompanion.app.domain.repository


import com.deencompanion.app.domain.model.Dua

interface DuaRepository {
    suspend fun getAllDuas(): List<Dua>
    suspend fun getDuaById(id: Int): Dua?
}