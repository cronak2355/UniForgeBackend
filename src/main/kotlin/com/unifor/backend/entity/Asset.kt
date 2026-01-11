package com.unifor.backend.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "assets")
data class Asset(
    @Id
    val id: String = UUID.randomUUID().toString(),
    
    @Column(nullable = false)
    val name: String,
    
    @Column(nullable = false)
    val price: BigDecimal = BigDecimal.ZERO,
    
    @Column(length = 2000)
    val description: String? = null,
    
    @Column(nullable = false)
    val authorId: String,
    
    @Column(nullable = true)
    val imageUrl: String? = null,
    
    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val isPublic: Boolean = true,

    @Column(nullable = true)
    val genre: String? = "Other",

    @Column(nullable = true, length = 500)
    val tags: String? = null,  // 쉼표로 구분된 태그 (예: "공포,RPG,액션")

    @Column(nullable = true, length = 50)
    val assetType: String? = "오브젝트"  // 타일, 캐릭터, 무기, 오브젝트, VFX, UI
)



