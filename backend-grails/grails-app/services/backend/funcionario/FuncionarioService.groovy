package backend.funcionario

import backend.exceptions.ApiValidationException
import backend.exceptions.ResourceNotFoundException
import backend.domain.Funcionario
import grails.gorm.transactions.Transactional

@Transactional
class FuncionarioService {
    @Transactional(readOnly = true)
    List<Funcionario> listar() {
        Funcionario.list(sort: "id", order: "asc")
    }

    @Transactional(readOnly = true)
    Funcionario buscar(Long id) {
        Funcionario.get(id)
    }

    Funcionario criar(Map data) {
        Funcionario funcionario = new Funcionario()
        aplicarDados(funcionario, data, true)
        salvar(funcionario)
    }

    Funcionario atualizar(Long id, Map data) {
        Funcionario funcionario = obterFuncionario(id)
        aplicarDados(funcionario, data, false)
        salvar(funcionario)
    }

    boolean excluir(Long id) {
        Funcionario funcionario = buscar(id)
        if (!funcionario) return false

        funcionario.delete(flush: true)
        true
    }

    private static void aplicarDados(Funcionario funcionario, Map data, boolean novoRegistro) {
        funcionario.nome = data.nome
        funcionario.email = data.email
        funcionario.cargo = data.cargo
        funcionario.salario = parseSalario(data.salario)
        funcionario.ativo = data.ativo != null ? data.ativo as Boolean : (novoRegistro ? true : funcionario.ativo)
    }

    private static BigDecimal parseSalario(def salario) {
        if (salario == null || salario.toString().trim().isEmpty()) {
            throw new ApiValidationException("Salario e obrigatorio.")
        }

        try {
            return new BigDecimal(salario.toString())
        } catch (NumberFormatException exception) {
            throw new ApiValidationException("Salario deve ser um numero valido.")
        }
    }

    private static Funcionario salvar(Funcionario funcionario) {
        if (!funcionario.validate()) {
            String message = buildValidationMessage(funcionario)
            throw new ApiValidationException(message)
        }

        funcionario.save(flush: true, failOnError: true)
        funcionario
    }

    private static Funcionario obterFuncionario(Long id) {
        Funcionario funcionario = Funcionario.get(id)
        if (!funcionario) {
            throw new ResourceNotFoundException("Funcionario nao encontrado.")
        }
        funcionario
    }

    private static String buildValidationMessage(Funcionario funcionario) {
        if (funcionario.errors.getFieldError("nome")) {
            return "Nome e obrigatorio."
        }
        if (funcionario.errors.getFieldError("email")) {
            def emailErrorCode = funcionario.errors.getFieldError("email")?.code
            if (emailErrorCode?.contains("unique")) {
                return "Ja existe um funcionario com este e-mail."
            }
            if (!funcionario.email?.trim()) {
                return "E-mail e obrigatorio."
            }
            return "E-mail invalido."
        }
        if (funcionario.errors.getFieldError("cargo")) {
            return "Cargo e obrigatorio."
        }
        if (funcionario.errors.getFieldError("salario")) {
            if (funcionario.salario == null) {
                return "Salario e obrigatorio."
            }
            return "Salario deve ser maior ou igual a zero."
        }
        if (funcionario.errors.getFieldError("ativo")) {
            return "Status do funcionario e obrigatorio."
        }
        return "Dados do funcionario sao invalidos."
    }
}
