package backend.funcionario

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Integration
@Rollback
class FuncionarioIntegrationSpec extends Specification {

    @Autowired
    WebApplicationContext webApplicationContext

    MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    void "GET /api/funcionarios retorna 200"() {
        expect:
        mockMvc.perform(get("/api/funcionarios"))
                .andExpect(status().isOk())
    }

    void "CRUD completo de funcionario retorna status esperados"() {
        given:
        String email = "summer.${System.currentTimeMillis()}@empresa.com"
        String createPayload = JsonOutput.toJson([
                nome   : "Summer Smith",
                email  : email,
                cargo  : "Analista",
                salario: 4200.50,
                ativo  : true
        ])

        when: "cria funcionario"
        def createResponse = mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response
        def createdJson = new JsonSlurper().parseText(createResponse.contentAsString)
        Long funcionarioId = createdJson.id as Long

        then:
        funcionarioId != null
        createdJson.email == email

        when: "busca por id"
        def showResponse = mockMvc.perform(get("/api/funcionarios/${funcionarioId}"))
                .andExpect(status().isOk())
                .andReturn()
                .response
        def showJson = new JsonSlurper().parseText(showResponse.contentAsString)

        then:
        showJson.id == funcionarioId
        showJson.nome == "Summer Smith"

        when: "atualiza funcionario"
        String updatePayload = JsonOutput.toJson([
                nome   : "Summer Atualizada",
                email  : email,
                cargo  : "Coordenadora",
                salario: 5100.00,
                ativo  : false
        ])
        def updateResponse = mockMvc.perform(
                put("/api/funcionarios/${funcionarioId}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload)
        )
                .andExpect(status().isOk())
                .andReturn()
                .response
        def updateJson = new JsonSlurper().parseText(updateResponse.contentAsString)

        then:
        updateJson.nome == "Summer Atualizada"
        updateJson.cargo == "Coordenadora"
        updateJson.ativo == false

        when: "exclui funcionario"
        mockMvc.perform(delete("/api/funcionarios/${funcionarioId}"))
                .andExpect(status().isNoContent())

        and: "busca funcionario excluido"
        mockMvc.perform(get("/api/funcionarios/${funcionarioId}"))
                .andExpect(status().isNotFound())

        then:
        noExceptionThrown()
    }

    void "POST /api/funcionarios com dados invalidos retorna 400"() {
        given:
        String invalidPayload = JsonOutput.toJson([
                nome   : "",
                email  : "email-invalido",
                cargo  : "",
                salario: -10,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload)
        )
                .andExpect(status().isBadRequest())
    }

    void "POST /api/funcionarios com e-mail duplicado retorna 400"() {
        given:
        String email = "beth.${System.currentTimeMillis()}@empresa.com"
        String payload = JsonOutput.toJson([
                nome   : "Beth Smith",
                email  : email,
                cargo  : "Gestora",
                salario: 6100.00,
                ativo  : true
        ])

        when:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isCreated())

        def duplicateResponse = mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isBadRequest())
                .andReturn()
                .response

        def duplicateJson = new JsonSlurper().parseText(duplicateResponse.contentAsString)

        then:
        duplicateJson.message
    }

    void "GET /api/funcionarios id inexistente retorna 404"() {
        expect:
        mockMvc.perform(get("/api/funcionarios/999999999"))
                .andExpect(status().isNotFound())
    }

    void "PUT /api/funcionarios id inexistente retorna 404"() {
        given:
        String payload = JsonOutput.toJson([
                nome   : "Nao Existe",
                email  : "naoexiste@empresa.com",
                cargo  : "Analista",
                salario: 1000,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                put("/api/funcionarios/999999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isNotFound())
    }

    void "DELETE /api/funcionarios id inexistente retorna 404"() {
        expect:
        mockMvc.perform(delete("/api/funcionarios/999999999"))
                .andExpect(status().isNotFound())
    }

    void "POST /api/funcionarios sem nome retorna 400"() {
        given:
        String payload = JsonOutput.toJson([
                nome   : "",
                email  : "semnome.${System.currentTimeMillis()}@empresa.com",
                cargo  : "Analista",
                salario: 1000,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isBadRequest())
    }

    void "POST /api/funcionarios sem email retorna 400"() {
        given:
        String payload = JsonOutput.toJson([
                nome   : "Funcionario Sem Email",
                email  : "",
                cargo  : "Analista",
                salario: 1000,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isBadRequest())
    }

    void "POST /api/funcionarios com salario negativo retorna 400"() {
        given:
        String payload = JsonOutput.toJson([
                nome   : "Funcionario Salario Negativo",
                email  : "negativo.${System.currentTimeMillis()}@empresa.com",
                cargo  : "Analista",
                salario: -1,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isBadRequest())
    }

    void "POST /api/funcionarios com salario invalido retorna 400"() {
        given:
        String payload = JsonOutput.toJson([
                nome   : "Funcionario Salario Invalido",
                email  : "invalido.${System.currentTimeMillis()}@empresa.com",
                cargo  : "Analista",
                salario: "abc",
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload)
        )
                .andExpect(status().isBadRequest())
    }

    void "PUT /api/funcionarios com e-mail duplicado retorna 400"() {
        given:
        String firstEmail = "jerry.${System.currentTimeMillis()}@empresa.com"
        String secondEmail = "morty.${System.currentTimeMillis()}@empresa.com"
        String firstPayload = JsonOutput.toJson([
                nome   : "Jerry Smith",
                email  : firstEmail,
                cargo  : "Gerente",
                salario: 5000,
                ativo  : true
        ])
        String secondPayload = JsonOutput.toJson([
                nome   : "Morty Smith",
                email  : secondEmail,
                cargo  : "Assistente",
                salario: 2500,
                ativo  : true
        ])

        def firstResponse = mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstPayload)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response

        def secondResponse = mockMvc.perform(
                post("/api/funcionarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondPayload)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .response

        Long secondId = (new JsonSlurper().parseText(secondResponse.contentAsString).id as Long)
        String updatePayload = JsonOutput.toJson([
                nome   : "Morty Smith",
                email  : firstEmail,
                cargo  : "Assistente",
                salario: 2500,
                ativo  : true
        ])

        expect:
        mockMvc.perform(
                put("/api/funcionarios/${secondId}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload)
        )
                .andExpect(status().isBadRequest())
    }
}
