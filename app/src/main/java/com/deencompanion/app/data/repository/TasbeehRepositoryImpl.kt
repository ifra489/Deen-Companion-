package com.deencompanion.app.data.repository

import com.deencompanion.app.data.local.dao.TasbeehDao
import com.deencompanion.app.data.local.entity.TasbeehCountEntity
import com.deencompanion.app.domain.model.TasbeehItem
import com.deencompanion.app.domain.model.TasbeehType
import com.deencompanion.app.domain.repository.TasbeehRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasbeehRepositoryImpl @Inject constructor(
    private val tasbeehDao: TasbeehDao
) : TasbeehRepository {

    override fun getAllTasbeehItems(): Flow<List<TasbeehItem>> {
        return tasbeehDao.getAllCounts().map { entities ->
            val entityMap = entities.associateBy { it.type }

            // Map built-in dhikr types
            val builtInItems = TasbeehType.values().map { type ->
                val entity = entityMap[type.name]
                TasbeehItem(
                    id = type.name,
                    displayName = type.getDisplayName(),
                    count = entity?.count ?: 0,
                    lastUpdated = entity?.lastUpdated ?: 0L,
                    isCustom = false,
                    targetCount = entity?.targetCount ?: 33
                )
            }

            // Map custom dhikr entries
            val customItems = entities.filter { it.isCustom }.map { entity ->
                TasbeehItem(
                    id = entity.type,
                    displayName = entity.displayName,
                    count = entity.count,
                    lastUpdated = entity.lastUpdated,
                    isCustom = true,
                    targetCount = entity.targetCount
                )
            }

            builtInItems + customItems
        }
    }

    override suspend fun incrementCount(id: String) {
        val timestamp = System.currentTimeMillis()
        val builtInType = try { TasbeehType.valueOf(id) } catch (e: Exception) { null }
        if (builtInType != null) {
            tasbeehDao.incrementCount(
                type = id,
                defaultDisplayName = builtInType.getDisplayName(),
                isCustom = false,
                timestamp = timestamp
            )
        } else {
            tasbeehDao.incrementCountInternal(id, timestamp)
        }
    }

    override suspend fun resetCount(id: String) {
        val timestamp = System.currentTimeMillis()
        val builtInType = try { TasbeehType.valueOf(id) } catch (e: Exception) { null }
        if (builtInType != null) {
            tasbeehDao.resetCount(
                type = id,
                defaultDisplayName = builtInType.getDisplayName(),
                isCustom = false,
                timestamp = timestamp
            )
        } else {
            tasbeehDao.resetCountInternal(id, timestamp)
        }
    }

    override suspend fun addCustomDhikr(displayName: String, targetCount: Int): String {
        val id = "custom_${UUID.randomUUID()}"
        val entity = TasbeehCountEntity(
            type = id,
            count = 0,
            lastUpdated = System.currentTimeMillis(),
            isCustom = true,
            displayName = displayName,
            targetCount = targetCount
        )
        tasbeehDao.insertOrUpdate(entity)
        return id
    }

    override suspend fun deleteCustomDhikr(id: String) {
        tasbeehDao.deleteByType(id)
    }

    override suspend fun updateTargetCount(id: String, target: Int) {
        val timestamp = System.currentTimeMillis()
        val builtInType = try { TasbeehType.valueOf(id) } catch (e: Exception) { null }
        if (builtInType != null) {
            tasbeehDao.updateTargetCount(
                type = id,
                defaultDisplayName = builtInType.getDisplayName(),
                isCustom = false,
                target = target,
                timestamp = timestamp
            )
        } else {
            tasbeehDao.updateTargetCountInternal(id, target)
        }
    }
}