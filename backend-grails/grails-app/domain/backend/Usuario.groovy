package backend.domain

class Usuario {
    String nome
    String email
    String senha
    Boolean ativo = true
    Date dataCriacao = new Date()

    static mapping = {
        table "usuarios"
    }

    static constraints = {
        nome blank: false
        email blank: false, unique: true, email: true
        senha blank: false
        ativo nullable: false
        dataCriacao nullable: false
    }
}
