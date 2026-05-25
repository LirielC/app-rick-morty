package backend.auth

import backend.exceptions.UnauthorizedException
import backend.domain.Usuario
import grails.gorm.transactions.Transactional

@Transactional
class AuthService {
    Map autenticar(String email, String senha) {
        validarCredenciais(email, senha)

        Usuario usuario = Usuario.findByEmailAndSenhaAndAtivo(email, senha, true)
        if (!usuario) {
            throw new UnauthorizedException("Credenciais invalidas")
        }

        return [
                success: true,
                token  : "token-simples",
                user   : [
                        id   : usuario.id,
                        nome : usuario.nome,
                        email: usuario.email
                ]
        ]
    }

    private static void validarCredenciais(String email, String senha) {
        if (!email?.trim() || !senha?.trim()) {
            throw new UnauthorizedException("Credenciais invalidas")
        }
    }
}
