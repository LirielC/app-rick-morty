package backend.domain

class Funcionario {
    String nome
    String email
    String cargo
    BigDecimal salario
    Boolean ativo = true
    Date dataCriacao = new Date()

    static mapping = {
        table "funcionarios"
    }

    static constraints = {
        nome blank: false
        email blank: false, unique: true, email: true
        cargo blank: false
        salario nullable: false, min: 0.0G
        ativo nullable: false
        dataCriacao nullable: false
    }
}
