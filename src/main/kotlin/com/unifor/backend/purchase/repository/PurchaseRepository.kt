package com.uniforge.backend.purchase.repository

import com.uniforge.backend.purchase.entity.Purchase
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseRepository : JpaRepository<Purchase, Long>
