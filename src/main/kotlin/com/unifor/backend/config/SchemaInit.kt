package com.unifor.backend.config

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

@Component
class SchemaInit(private val jdbcTemplate: JdbcTemplate) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(SchemaInit::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info(">>> SchemaInit: Checking DB Schema for 'is_public' column...")
        try {
            // Check if column exists, if not add it (PostgreSQL specific syntax using IF NOT EXISTS logic handled manually or via simple ALTER catching error)
            // Postgres supports ADD COLUMN IF NOT EXISTS since v9.6
            val sql = "ALTER TABLE games ADD COLUMN IF NOT EXISTS is_public BOOLEAN DEFAULT FALSE;"
            jdbcTemplate.execute(sql)
            logger.info(">>> SchemaInit: 'is_public' column check/creation completed.")

            // Ensure no NULL values exist (migration)
            val updateSql = "UPDATE games SET is_public = FALSE WHERE is_public IS NULL;"
            val rows = jdbcTemplate.update(updateSql)
            logger.info(">>> SchemaInit: Updated $rows rows with NULL is_public to FALSE.")

        } catch (e: Exception) {
            logger.error(">>> SchemaInit: Failed to update schema", e)
        }
    }
}
