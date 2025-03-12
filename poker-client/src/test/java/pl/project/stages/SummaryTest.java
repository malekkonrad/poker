package pl.project.stages;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SummaryTest {

    private Summary summary;
    private Client mockClient;
    private SocketChannel mockSocketChannel;

    @BeforeEach
    void setUp() {
        // Tworzenie instancji klasy Summary jako Spy, aby móc nadpisywać metody
        summary = spy(new Summary());

        // Mockowanie obiektu Client
        mockClient = mock(Client.class);

        // Mockowanie SocketChannel dla klienta
        mockSocketChannel = mock(SocketChannel.class);
        when(mockClient.getSocketChannel()).thenReturn(mockSocketChannel);

        // Mockowanie metod Client
        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);
        when(mockClient.getHand()).thenReturn(new ArrayList<>(Arrays.asList("Card1", "Card2", "Card3")));
    }

    @Test
    void testExecute_GameFounder() throws IOException {
        // Given: klient jako założyciel gry
        when(mockClient.isGameFounder()).thenReturn(true);

        // Mockowanie metody handleSummaryRequest
        doNothing().when(summary).handleSummaryRequest(mockClient);

        // When: Wywołanie metody execute
        summary.execute(mockClient);

        // Then: Weryfikacja, czy handleSummaryRequest została wywołana
        verify(summary, times(1)).handleSummaryRequest(mockClient);
    }

    @Test
    void testExecute_ScoreToken() throws IOException {
        // Given: klient jako zwykły gracz
        when(mockClient.isGameFounder()).thenReturn(false);
        String response = "score layout 1 winner Player1 winnerLayout 100 500";
        doReturn(response).when(summary).getResponse(mockClient);

        // Mockowanie metody handleScoreToken
        doNothing().when(summary).handleScoreToken(eq(mockClient), any());

        // When: Wywołanie metody execute
        summary.execute(mockClient);

        // Then: Weryfikacja, czy handleScoreToken została wywołana
        verify(summary, times(1)).handleScoreToken(eq(mockClient), any());
    }

    @Test
    void testExecute_FoldWinnerToken() throws IOException {
        // Given: klient jako zwykły gracz
        when(mockClient.isGameFounder()).thenReturn(false);
        String response = "foldWinner Player1 1 100 500";
        doReturn(response).when(summary).getResponse(mockClient);

        // Mockowanie metody handleFoldWinnerToken
        doNothing().when(summary).handleFoldWinnerToken(eq(mockClient), any());

        // When: Wywołanie metody execute
        summary.execute(mockClient);

        // Then: Weryfikacja, czy handleFoldWinnerToken została wywołana
        verify(summary, times(1)).handleFoldWinnerToken(eq(mockClient), any());
    }

    @Test
    void testHandleSummaryRequest() throws IOException {
        // Given: oczekiwany token
        String expectedToken = "summary 1 42 request";
        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);

        // When: Wywołanie metody
        summary.handleSummaryRequest(mockClient);

        // Then: Weryfikacja wysłania tokena
        verify(mockClient).setGameFounder(false);
        verify(mockSocketChannel, times(1)).write(ByteBuffer.wrap(expectedToken.getBytes()));
    }

    @Test
    void testHandleLeft() {
        // Given: lista kart i ustawione ID gry
        List<String> mockHand = new ArrayList<>(Arrays.asList("Card1", "Card2", "Card3"));
        when(mockClient.getHand()).thenReturn(mockHand);
        when(mockClient.getGameID()).thenReturn(1);

        // Przechwycenie wejścia użytkownika
        ByteArrayInputStream inputStream = new ByteArrayInputStream("\n".getBytes());
        System.setIn(inputStream);

        // When: Wywołanie metody
        summary.handleLeft(mockClient);

        // Then: Weryfikacja wyczyszczenia danych
        assertTrue(mockHand.isEmpty());
        verify(mockClient).setGameID(-1);
        verify(mockClient).setStage(any(PreGame.class));
    }



    @Test
    void testHandleFoldWinnerToken_Win() {
        // Given: odpowiedź serwera wskazująca wygraną gracza
        String[] parts = {"foldWinner", "Player1", "42", "100", "500"};
        when(mockClient.getPlayerID()).thenReturn(42);

        // Mockowanie metody handleLeft, aby uniknąć blokowania przez Scanner
        doNothing().when(summary).handleLeft(mockClient);

        // When: Wywołanie metody
        summary.handleFoldWinnerToken(mockClient, parts);

        // Then: Weryfikacja komunikatów
        verify(summary, times(1)).handleWin("100", "500");
        verify(summary, times(1)).handleLeft(mockClient);
    }

    @Test
    void testHandleFoldWinnerToken_Loss() {
        // Given: odpowiedź serwera wskazująca przegraną gracza
        String[] parts = {"foldWinner", "Player2", "1", "100", "500"};
        when(mockClient.getPlayerID()).thenReturn(42);

        // Mockowanie metody handleLeft, aby uniknąć blokowania przez Scanner
        doNothing().when(summary).handleLeft(mockClient);

        // When: Wywołanie metody
        summary.handleFoldWinnerToken(mockClient, parts);

        // Then: Weryfikacja komunikatów
        verify(summary, times(1)).handleLoss("100", "500", "Player2");
        verify(summary, times(1)).handleLeft(mockClient);
    }


    @Test
    void testHandleScoreToken_Win() {
        // Given: odpowiedź serwera wskazująca wygraną gracza
        String[] parts = {"score", "layout1", "42", "Player1", "winnerLayout", "100", "500"};
        when(mockClient.getPlayerID()).thenReturn(42);

        // Mockowanie metody handleLeft, aby uniknąć blokowania przez Scanner
        doNothing().when(summary).handleLeft(mockClient);

        // When: Wywołanie metody
        summary.handleScoreToken(mockClient, parts);

        // Then: Weryfikacja komunikatów
        verify(summary, times(1)).handleWin("100", "500");
        verify(summary, times(1)).handleLeft(mockClient);
    }

    @Test
    void testHandleScoreToken_Loss() {
        // Given: odpowiedź serwera wskazująca przegraną gracza
        String[] parts = {"score", "layout1", "1", "Player2", "winnerLayout", "100", "500"};
        when(mockClient.getPlayerID()).thenReturn(42);

        // Mockowanie metody handleLeft, aby uniknąć blokowania przez Scanner
        doNothing().when(summary).handleLeft(mockClient);

        // When: Wywołanie metody
        summary.handleScoreToken(mockClient, parts);

        // Then: Weryfikacja komunikatów
        verify(summary, times(1)).handleLoss("100", "500", "Player2");
        verify(summary, times(1)).handleLeft(mockClient);
    }

}