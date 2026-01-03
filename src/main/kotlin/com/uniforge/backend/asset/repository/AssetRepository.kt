package com.uniforge.backend.asset.repository

import com.uniforge.backend.asset.entity.Asset
import org.springframework.data.jpa.repository.JpaRepository

interface AssetRepository : JpaRepository<Asset, Long>
