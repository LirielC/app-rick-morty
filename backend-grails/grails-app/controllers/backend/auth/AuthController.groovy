package backend.auth

import backend.api.ApiResponseSupport
import backend.exceptions.UnauthorizedException
class AuthController implements ApiResponseSupport {
    AuthService authService
    static responseFormats = ['json']
    static allowedMethods = [login: "POST"]

    def login() {
        try {
            Map payload = request.JSON as Map
            renderJson(authService.autenticar(payload.email as String, payload.senha as String))
        } catch (UnauthorizedException exception) {
            renderError(401, exception.message, [success: false])
        } catch (Exception exception) {
            renderError(500, "Erro interno no servidor.", [success: false])
        }
    }
}
