package com.sokima.solidgate.controller

import com.sokima.solidgate.dto.BatchBalanceUpdateRequest
import com.sokima.solidgate.persistence.BalanceEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.testcontainers.utility.MountableFile

@Testcontainers
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BalanceControllerTest {

    companion object {
        @Container
        val postgreSqlContainer: PostgreSQLContainer<*> = createPostgresContainer()

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("POSTGRES_HOST", postgreSqlContainer::getHost)
            registry.add("POSTGRES_PORT", postgreSqlContainer::getFirstMappedPort)
            registry.add("POSTGRES_USERNAME", postgreSqlContainer::getUsername)
            registry.add("POSTGRES_PASSWORD", postgreSqlContainer::getPassword)
        }

        private fun createPostgresContainer(): PostgreSQLContainer<*> {
            return PostgreSQLContainer(DockerImageName.parse("postgres:14.0"))
                .withDatabaseName("balances")
                .withUsername("postgres")
                .withPassword("postgres")
                .withCopyFileToContainer(
                    MountableFile.forHostPath("init.sql"),
                    "/docker-entrypoint-initdb.d/init.sql"
                )
        }
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Test
    fun `should set account balances`() {
        val balances = (1..1500).associateWith { it * 10 }
        val batchRequest = BatchBalanceUpdateRequest(balances)

        val result = webTestClient.post()
            .uri("/balances")
            .bodyValue(batchRequest)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(BalanceEntity::class.java)
            .hasSize(1500)
    }
}