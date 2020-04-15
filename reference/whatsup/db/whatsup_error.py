class WhatsupError(RuntimeError):
    error_list = {
        0: "Username has already been taken.",
        1: "Cannot find the target user.",
        2: "Cannot find the target event.",
    }

    def __init__(self, error_type):
        self.error_message = self.error_list[error_type]

    def __str__(self):
        return self.error_message

