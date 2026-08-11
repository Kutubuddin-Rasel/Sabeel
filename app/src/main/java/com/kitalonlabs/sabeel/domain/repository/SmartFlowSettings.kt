package com.kitalonlabs.sabeel.domain.repository

import com.kitalonlabs.sabeel.domain.model.SmartFlowVariant
import kotlinx.coroutines.flow.Flow

/** ISP: Smart Flow (post-Salah sequence) toggle + variant — a distinct concern from counting itself. */
interface SmartFlowSettings {
    val isSmartFlowEnabled: Flow<Boolean>
    val smartFlowVariant: Flow<SmartFlowVariant>
    suspend fun setSmartFlowEnabled(enabled: Boolean)
    suspend fun setSmartFlowVariant(variant: SmartFlowVariant)
}