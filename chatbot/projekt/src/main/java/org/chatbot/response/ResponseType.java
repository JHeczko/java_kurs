package org.chatbot.response;

public enum ResponseType {
    WELCOME("Witaj w Chatbot Rezerwacji! Aby zrobić rezerwację, wpisz 'Rezerwuj'. Aby usunąć rezerwację, wpisz 'Usuń rezerwację [ID]'."),
    RESERVATION_SUCCESS("Rezerwacja została pomyślnie dodana."),
    RESERVATION_DELETED("Rezerwacja o ID %d została usunięta."),
    RESERVATION_LIST("Lista rezerwacji:"),
    CONFIRMATION_REQUEST("Czy na pewno chcesz usunąć rezerwację o ID %d? Odpowiedz 'tak' lub 'nie'."),
    ERROR("Wystąpił błąd: %s"),
    INVALID_COMMAND("Nie rozpoznano polecenia. Spróbuj ponownie."),
    INVALID_RESERVATION_ID_FORMAT("Nieprawidłowy format ID rezerwacji. Proszę spróbować ponownie."),
    CANCELLATION_CONFIRMED("Usuwanie rezerwacji anulowane.");

    private final String message;

    ResponseType(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
