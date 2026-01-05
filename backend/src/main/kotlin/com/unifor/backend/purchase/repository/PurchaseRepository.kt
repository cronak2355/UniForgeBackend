package com.unifor.backend.purchase.repository

import com.unifor.backend.purchase.entity.Purchase
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseRepository : JpaRepository<Purchase, Long>
