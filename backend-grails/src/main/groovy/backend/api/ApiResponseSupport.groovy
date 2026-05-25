package backend.api

import grails.converters.JSON

trait ApiResponseSupport {
    void renderJson(def body, int status = 200) {
        render(status: status, text: (body as JSON).toString(), contentType: "application/json")
    }

    void renderError(int status, String message, Map extra = [:]) {
        renderJson(extra + [message: message], status)
    }

    void renderNoContent() {
        render(status: 204, text: "")
    }
}
