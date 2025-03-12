package pl.project.stages;

import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WaitingTest {

    @Test
    void testExecute_StartGame() throws IOException {
        // Mockowanie klienta
        Client mockClient = mock(Client.class);

        // Mockowanie klasy Waiting z nadpisaniem getResponse
        Waiting waitingStage = spy(new Waiting());
        doReturn("startGame").when(waitingStage).getResponse(mockClient);

        // Wywołanie metody execute
        waitingStage.execute(mockClient);

        // Weryfikacja
        verify(mockClient, times(1)).setStage(any(PokerGame.class)); // Sprawdzenie, czy zmieniono etap na PokerGame
    }

    @Test
    void testExecute_PlayerJoin() throws IOException {
        // Mockowanie klienta
        Client mockClient = mock(Client.class);

        // Mockowanie klasy Waiting z nadpisaniem getResponse
        Waiting waitingStage = spy(new Waiting());
        doReturn("playerJoin Player1").when(waitingStage).getResponse(mockClient);

        // Przechwycenie wyjścia na konsolę
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Wywołanie metody execute
        waitingStage.execute(mockClient);

        // Weryfikacja treści konsoli
        String expectedOutput = "Player : [Player1] joined the game!";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    void testExecute_UnrecognizedMessage() throws IOException {
        // Mockowanie klienta
        Client mockClient = mock(Client.class);

        // Mockowanie klasy Waiting z nadpisaniem getResponse
        Waiting waitingStage = spy(new Waiting());
        doReturn("unknownMessage").when(waitingStage).getResponse(mockClient);

        // Przechwycenie wyjścia na konsolę
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Wywołanie metody execute
        waitingStage.execute(mockClient);

        // Weryfikacja treści konsoli
        String expectedOutput = "Unrecognised message from server";
        assertTrue(outContent.toString().contains(expectedOutput));
    }



    @Test
    void testInfoMethod() {
        // Przechwycenie wyjścia na konsolę
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Tworzenie instancji klasy Waiting
        Waiting waitingStage = new Waiting();

        // Wywołanie metody info
        waitingStage.info();

        // Weryfikacja treści konsoli
        String expectedOutput = "You are waiting for other players to join the game...";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    void testHandleResponse_StartGame() {
        // Mockowanie klienta
        Client mockClient = mock(Client.class);

        // Tworzenie instancji klasy Waiting
        Waiting waitingStage = new Waiting();

        // Wywołanie handleResponse z odpowiedzią "startGame"
        waitingStage.handleResponse(mockClient, "startGame");

        // Weryfikacja, czy etap zmieniono na PokerGame
        verify(mockClient, times(1)).setStage(any(PokerGame.class));
    }

    @Test
    void testHandleResponse_PlayerJoin() {
        // Tworzenie instancji klasy Waiting
        Waiting waitingStage = new Waiting();

        // Przechwycenie wyjścia na konsolę
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Wywołanie handleResponse z odpowiedzią "playerJoin Player1"
        waitingStage.handleResponse(null, "playerJoin Player1");

        // Weryfikacja treści konsoli
        String expectedOutput = "Player : [Player1] joined the game!";
        assertTrue(outContent.toString().contains(expectedOutput));
    }

    @Test
    void testHandleResponse_UnrecognizedMessage() {
        // Tworzenie instancji klasy Waiting
        Waiting waitingStage = new Waiting();

        // Przechwycenie wyjścia na konsolę
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Wywołanie handleResponse z nieznaną odpowiedzią
        waitingStage.handleResponse(null, "unknownMessage");

        // Weryfikacja treści konsoli
        String expectedOutput = "Unrecognised message from server";
        assertTrue(outContent.toString().contains(expectedOutput));
    }
}