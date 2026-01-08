<<<<<<< HEAD
package com.unifor.backend.repository
=======
﻿package com.unifor.backend.repository
>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da

import com.unifor.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
<<<<<<< HEAD
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}
=======
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
}



>>>>>>> 338a79d154f1cca38ca079749882aff6399db7da
