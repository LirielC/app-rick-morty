package backend

class UrlMappings {
    static mappings = {
        "/api/auth/login"(controller: "auth", action: "login")
        "/api/funcionarios"(controller: "funcionario") {
            action = [GET: "index", POST: "save"]
        }
        "/api/funcionarios/$id"(controller: "funcionario") {
            action = [GET: "show", PUT: "update", DELETE: "delete"]
        }
        "500"(view: '/error')
        "404"(view: '/notFound')
    }
}
