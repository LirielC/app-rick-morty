package backend.funcionario

import backend.api.ApiResponseSupport
import backend.exceptions.ApiValidationException
import backend.exceptions.ResourceNotFoundException
import backend.domain.Funcionario

class FuncionarioController implements ApiResponseSupport {
    FuncionarioService funcionarioService
    static responseFormats = ['json']
    static allowedMethods = [index: "GET", show: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def index() {
        renderJson(funcionarioService.listar())
    }

    def show(Long id) {
        Funcionario funcionario = funcionarioService.buscar(id)
        if (!funcionario) {
            renderError(404, "Funcionario nao encontrado.")
            return
        }

        renderJson(funcionario)
    }

    def save() {
        try {
            Funcionario funcionario = funcionarioService.criar(request.JSON as Map)
            renderJson(funcionario, 201)
        } catch (ApiValidationException exception) {
            renderError(400, exception.message)
        } catch (Exception exception) {
            renderError(500, "Erro interno no servidor.", [success: false])
        }
    }

    def update(Long id) {
        try {
            Funcionario funcionario = funcionarioService.atualizar(id, request.JSON as Map)
            renderJson(funcionario)
        } catch (ResourceNotFoundException exception) {
            renderError(404, exception.message)
        } catch (ApiValidationException exception) {
            renderError(400, exception.message)
        } catch (Exception exception) {
            renderError(500, "Erro interno no servidor.", [success: false])
        }
    }

    def delete(Long id) {
        try {
            boolean deleted = funcionarioService.excluir(id)
            if (deleted) {
                renderNoContent()
            } else {
                renderError(404, "Funcionario nao encontrado.")
            }
        } catch (Exception exception) {
            renderError(500, "Erro interno no servidor.", [success: false])
        }
    }
}
