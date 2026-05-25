package backend

import backend.domain.Funcionario
import backend.domain.Usuario

class BootStrap {
    private static final String ADMIN_EMAIL = "admin@empresa.com"

    def init = { servletContext ->
        criarUsuarioAdminSeNecessario()
        criarFuncionariosExemploSeNecessario()
    }

    def destroy = {
    }

    private static void criarUsuarioAdminSeNecessario() {
        if (Usuario.findByEmail(ADMIN_EMAIL)) return

        new Usuario(
                nome: "Administrador",
                email: ADMIN_EMAIL,
                senha: "123456"
        ).save(failOnError: true)
    }

    private static void criarFuncionariosExemploSeNecessario() {
        if (Funcionario.count() > 0) return

        [
                [nome: "Rick Sanchez", email: "rick@empresa.com", cargo: "Cientista", salario: 9999.99G, ativo: true],
                [nome: "Morty Smith", email: "morty@empresa.com", cargo: "Assistente", salario: 3500.00G, ativo: true]
        ].each { dados ->
            new Funcionario(dados).save(failOnError: true)
        }
    }
}
