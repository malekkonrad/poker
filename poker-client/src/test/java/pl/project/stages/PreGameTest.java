package pl.project.stages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class PreGameTest {


    private PreGame preGame;
    private Client mockClient;

    @BeforeEach
    void setUp() {
        preGame = new PreGame();
        mockClient = mock(Client.class); // Mockowanie klienta
    }

    @Test
    void testPreGameWelcome() {
        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        preGame.preGameWelcome();

        // Oczekiwane wyjście
        assertTrue(outContent.toString().contains("----------------"));
        assertTrue(outContent.toString().contains("Lobby"));
        assertTrue(outContent.toString().contains("----------------"));
        assertTrue(outContent.toString().contains("Welcome to the lobby! You can join or create a game."));

    }

    @Test
    void testHandleCreate() {
        // Mockowanie danych klienta
        when(mockClient.getPlayerID()).thenReturn(123);

        String result = preGame.handleCreate(mockClient);

        // Sprawdzenie, czy metoda zwróciła poprawny wynik
        assertEquals("create 123", result);

        // Weryfikacja, czy klient został ustawiony jako założyciel gry
        verify(mockClient, times(1)).setGameFounder(true);
    }

    @Test
    void testHandleJoin_ValidInput() {
        // Mockowanie wejścia użytkownika
        ByteArrayInputStream inContent = new ByteArrayInputStream("42\n".getBytes());
        System.setIn(inContent);

        when(mockClient.getPlayerID()).thenReturn(123);

        Scanner scanner = new Scanner(System.in);
        String result = preGame.handleJoin(mockClient, scanner);

        assertEquals("join 42 123", result);
    }

    @Test
    void testHandleJoin_InvalidInput() {
        // Mockowanie wielokrotnego nieprawidłowego wejścia, a potem poprawnego
        ByteArrayInputStream inContent = new ByteArrayInputStream("invalid\n42\n".getBytes());
        System.setIn(inContent);

        when(mockClient.getPlayerID()).thenReturn(123);

        Scanner scanner = new Scanner(System.in);
        String result = preGame.handleJoin(mockClient, scanner);

        assertEquals("join 42 123", result);
    }

    @Test
    void testHandleResponse_AcceptedCreate() {
        String[] tokens = {"acceptedCreate", "42"};
        preGame.handleResponse(mockClient, tokens);

        verify(mockClient).setGameID(42);
        verify(mockClient).setStage(any(Waiting.class));
    }

    @Test
    void testHandleResponse_AcceptedJoin() {
        String[] tokens = {"acceptedJoin", "42"};
        preGame.handleResponse(mockClient, tokens);

        verify(mockClient).setGameID(42);
        verify(mockClient).setStage(any(Waiting.class));
    }

    @Test
    void testHandleResponse_RejectedJoin() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] tokens = {"rejectedJoin"};
        preGame.handleResponse(mockClient, tokens);

        assertEquals("Game of ID you entered does not exist\r\n", outContent.toString());

    }

    @Test
    void testHandleResponse_UnrecognizedMessage() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        String[] tokens = {"unknownMessage"};
        preGame.handleResponse(mockClient, tokens);

        assertEquals("Unrecognised message from server.\r\n", outContent.toString());
    }




}