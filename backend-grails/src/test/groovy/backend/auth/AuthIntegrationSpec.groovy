package backend.auth

import backend.domain.Usuario
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Integration
@Rollback
class AuthIntegrationSpec extends Specification {

    @Autowired
    WebApplicationContext webApplicationContext

    MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

        if (!Usuario.findByEmail("admin@empresa.com")) {
            new Usuario(
                    nome: "Administrador",
                    email: "admin@empresa.com",
                    senha: "123456",
                    ativo: true
            ).save(failOnError: true, flush: true)
        }
    }

    void "POST /api/auth/login com credenciais validas retorna 200 e payload esperado"() {
        given:
        String payload = JsonOutput.toJson([
                email: "admin@empresa.com",
                senha: "123456"
        ])

        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == true
        json.token
        json.user
        json.user.email == "admin@empresa.com"
    }

    void "POST /api/auth/login com senha invalida retorna 401 e success false"() {
        given:
        String payload = JsonOutput.toJson([
                email: "admin@empresa.com",
                senha: "senha-invalida"
        ])

        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == false
        json.message
    }

    void "POST /api/auth/login com body vazio retorna erro de autenticacao"() {
        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == false
        json.message
    }

    void "POST /api/auth/login com email vazio retorna 401"() {
        given:
        String payload = JsonOutput.toJson([
                email: "",
                senha: "123456"
        ])

        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == false
        json.message
    }

    void "POST /api/auth/login com senha vazia retorna 401"() {
        given:
        String payload = JsonOutput.toJson([
                email: "admin@empresa.com",
                senha: ""
        ])

        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == false
        json.message
    }

    void "POST /api/auth/login com usuario inexistente retorna 401"() {
        given:
        String payload = JsonOutput.toJson([
                email: "naoexiste@empresa.com",
                senha: "123456"
        ])

        when:
        def response = mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isUnauthorized())
                .andReturn()
                .response

        def json = new JsonSlurper().parseText(response.contentAsString)

        then:
        json.success == false
        json.message
    }
}
