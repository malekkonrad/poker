package pl.project.stages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import pl.project.communication.ReadToken;
import pl.project.communication.SendToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;

class AuctionTest {


    private Auction auction;
    private Client mockClient;
    private SocketChannel mockSocketChannel;

    @BeforeEach
    void setUp() {
        auction = spy(new Auction());
        mockClient = mock(Client.class);
        mockSocketChannel = mock(SocketChannel.class);

        // Mockowanie SocketChannel w kliencie
        when(mockClient.getSocketChannel()).thenReturn(mockSocketChannel);

        // Mockowanie metody getHand()
        when(mockClient.getHand()).thenReturn(Arrays.asList("Card1", "Card2", "Card3"));
    }

    @Test
    void testExecute_StartAuction() throws IOException {
        // Given: Odpowiedź serwera "startAuction"
        String response = "startAuction 500 50 100";
        doReturn(response).when(auction).getResponse(mockClient);

        // Mockowanie metody getAction
        doReturn(true).when(auction).getAction(any(), any(), anyInt(), anyInt(), anyInt());

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).printCards(mockClient);
        verify(auction, times(1)).printCashState(500, 100, 50);
    }

    @Test
    void testExecute_PlayerBet() throws IOException {
        // Given: Odpowiedź serwera "playerBet"
        String response = "playerBet 1 raise 150";
        doReturn(response).when(auction).getResponse(mockClient);

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).handlePlayerBetToken(response.split(" "));
    }

    @Test
    void testExecute_AcceptedBet() throws IOException {
        // Given: Odpowiedź serwera "playerBet"
        String response = "acceptedBet";
        doReturn(response).when(auction).getResponse(mockClient);

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).handleAcceptedBetToken();
    }

    @Test
    void testExecute_NextStage() throws IOException {
        // Given: Odpowiedź serwera "playerBet"
        String response = "nextStage";
        doReturn(response).when(auction).getResponse(mockClient);

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).handleNextStageToken(mockClient);
    }


    @Test
    void testExecute_LastStage() throws IOException {
        // Given: Odpowiedź serwera "playerBet"
        String response = "lastStage";
        doReturn(response).when(auction).getResponse(mockClient);

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).handleLastStageToken(mockClient);
    }

    @Test
    void testExecute_Winner() throws IOException {
        // Given: Odpowiedź serwera "playerBet"
        String response = "winner";
        doReturn(response).when(auction).getResponse(mockClient);

        // When: Wywołanie metody execute
        auction.execute(mockClient);

        // Then: Weryfikacja, czy poprawnie obsłużono odpowiedź
        verify(auction, times(1)).handleResponse(mockClient, response);
        verify(auction, times(1)).handleWinnerToken(mockClient);
    }



    @Test
    void testHandleNextStageToken() {
        // When: Wywołanie metody handleNextStageToken
        auction.handleNextStageToken(mockClient);

        // Then: Weryfikacja, czy zmieniono etap na ChangingCards
        verify(mockClient, times(1)).setStage(any(ChangingCards.class));
    }

    @Test
    void testHandleLastStageToken() {
        // When: Wywołanie metody handleLastStageToken
        auction.handleLastStageToken(mockClient);

        // Then: Weryfikacja, czy zmieniono etap na Summary
        verify(mockClient, times(1)).setStage(any(Summary.class));
    }

    @Test
    void testHandleWinnerToken() {
        // When: Wywołanie metody handleWinnerToken
        auction.handleWinnerToken(mockClient);

        // Then: Weryfikacja, czy zmieniono etap na Summary
        verify(mockClient, times(1)).setStage(any(Summary.class));
    }


    @Test
    void testCreateTokens() {
        // Given: Dane klienta
        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);

        // When: Wywołanie metod tokenów
        String foldToken = auction.createFoldToken(mockClient);
        String callToken = auction.createCallToken(mockClient, 50);
        String raiseToken = auction.createRaiseToken(mockClient, 50, 100);
        String allInToken = auction.createBetAllInToken(mockClient, 150);

        // Then: Weryfikacja poprawności tokenów
        assertEquals("bet 1 42 fold 0", foldToken);
        assertEquals("bet 1 42 call 50", callToken);
        assertEquals("bet 1 42 raise 150", raiseToken);
        assertEquals("bet 1 42 allIn 150", allInToken);
    }

    @Test
    void testHandleAcceptedBetToken() {
        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Tworzenie instancji klasy Auction
        Auction auction1 = new Auction();

        // Wywołanie metody
        auction1.handleAcceptedBetToken();

        // Oczekiwany komunikat
        String expectedOutput = "Your bet was submitted, wait for others players to bet";

        // Weryfikacja
        assertTrue(outContent.toString().contains(expectedOutput));

        // Przywrócenie domyślnego System.out
        System.setOut(System.out);
    }


    @Test
    void testPrepareAllIn() {
        // Given: Dane wejściowe
        int playerCash = 1000;
        int stake = 500;
        int minimumBet = 100;

        // Mockowanie wywołania createBetAllInToken
        String expectedToken = "bet 1 42 allIn 100";
        doReturn(expectedToken).when(auction).createBetAllInToken(mockClient, minimumBet);

        // When: Wywołanie metody prepareAllIn
        String result = auction.prepareAllIn(mockClient, playerCash, stake, minimumBet);

        // Then: Weryfikacja wyniku
        assertEquals(expectedToken, result);

        // Weryfikacja wywołania printCashState
        verify(auction, times(1)).printCashState(playerCash, playerCash+stake, minimumBet);

        // Weryfikacja wywołania createBetAllInToken
        verify(auction, times(1)).createBetAllInToken(mockClient, minimumBet);
    }

    @Test
    void testHandleCall_ValidCall() throws Exception {
        // Given: Dane wejściowe
        int playerCash = 500;
        int stake = 200;
        int minimumBet = 100;


        // Mockowanie createCallToken
        String expectedToken = "bet 1 42 call 100";
        doReturn(expectedToken).when(auction).createCallToken(mockClient, minimumBet);

        // When: Wywołanie metody handleCall
        auction.handleCall(mockClient, playerCash, stake, minimumBet);

        // Then: Weryfikacja
        verify(auction, times(1)).printCashState(playerCash - minimumBet, stake + minimumBet, minimumBet);
        verify(auction, times(1)).createCallToken(mockClient, minimumBet);
        // Pomijamy weryfikację SendToken.send
    }


    @Test
    void testHandleFold() throws Exception {
        // Given: Oczekiwany token
        String expectedToken = "bet 1 42 fold 0";

        // Mockowanie metody createFoldToken
        doReturn(expectedToken).when(auction).createFoldToken(mockClient);

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody handleFold
        auction.handleFold(mockClient);

        // Then: Weryfikacja wywołań
        verify(auction, times(1)).createFoldToken(mockClient); // Sprawdzenie, czy token został wygenerowany

        // Sprawdzenie komunikatu na konsoli
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("You have folded."));

        // Przywrócenie standardowego System.out
        System.setOut(System.out);
    }


    @Test
    void testHandleRaise_ValidRaise() throws Exception {
        // Given: Dane wejściowe
        int playerCash = 500;
        int stake = 200;
        int minimumBet = 100;

        // Symulacja wejścia użytkownika
        String simulatedInput = "50\n"; // Użytkownik wpisuje 50
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        // Mockowanie createRaiseToken
        String expectedToken = "bet 1 42 raise 150";
        doReturn(expectedToken).when(auction).createRaiseToken(mockClient, minimumBet, 50);

        // When: Wywołanie metody handleRaise
        boolean result = auction.handleRaise(mockClient, scanner, playerCash, stake, minimumBet);

        // Then: Weryfikacja
        assertTrue(result);
        verify(auction, times(1)).printCashState(playerCash - (minimumBet + 50), stake + minimumBet + 50, minimumBet + 50);
        verify(auction, times(1)).createRaiseToken(mockClient, minimumBet, 50);
    }

    @Test
    void testHandleRaise_NotEnoughMoney() throws Exception {
        // Given: Dane wejściowe
        int playerCash = 500;
        int stake = 200;
        int minimumBet = 100;

        // Symulacja wejścia użytkownika
        String simulatedInput = "500\n"; // Użytkownik wpisuje 500 (za wysoka wartość raise)
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        // When: Wywołanie metody handleRaise
        boolean result = auction.handleRaise(mockClient, scanner, playerCash, stake, minimumBet);

        // Then: Weryfikacja
        assertFalse(result);
    }

    @Test
    void testHandleRaise_InvalidInput() throws Exception {
        // Given: Dane wejściowe
        int playerCash = 500;
        int stake = 200;
        int minimumBet = 100;

        // Symulacja niepoprawnego wejścia użytkownika
        String simulatedInput = "invalid\n"; // Użytkownik wpisuje tekst zamiast liczby
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Scanner scanner = new Scanner(inputStream);

        // When: Wywołanie metody handleRaise
        boolean result = auction.handleRaise(mockClient, scanner, playerCash, stake, minimumBet);

        // Then: Weryfikacja
        assertFalse(result);
    }



    @Test
    void testGetAction_Fold_WhenLowCash() throws Exception {
        // Given
        int playerCash = 50;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("fold\n".getBytes()));

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertTrue(result);
        verify(auction, times(1)).handleFold(mockClient);
    }


    @Test
    void testGetAction_AllIn_WhenLowCash() throws Exception {
        // Given
        int playerCash = 50;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("allIn\n".getBytes()));

        // Mockowanie handleAllIn
        doReturn(true).when(auction).handleAllIn(mockClient, 0, stake + playerCash, playerCash);

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertTrue(result);
        verify(auction, times(1)).handleAllIn(mockClient, 0, stake + playerCash, playerCash);
    }


    @Test
    void testGetAction_InvalidCommand_WhenLowCash() throws Exception {
        // Given
        int playerCash = 50;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("invalid\n".getBytes()));

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertFalse(result);
        verify(auction, never()).handleFold(mockClient);
        verify(auction, never()).handleAllIn(any(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void testGetAction_Call_WhenEnoughCash() throws Exception {
        // Given
        int playerCash = 500;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("call\n".getBytes()));

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertTrue(result);
        verify(auction, times(1)).handleCall(mockClient, playerCash, stake, minimumBet);
    }

    @Test
    void testGetAction_Raise_WhenEnoughCash() throws Exception {
        // Given
        int playerCash = 500;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("raise\n50\n".getBytes()));

        // Mockowanie handleRaise
        doReturn(true).when(auction).handleRaise(mockClient, scanner, playerCash, stake, minimumBet);

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertTrue(result);
        verify(auction, times(1)).handleRaise(mockClient, scanner, playerCash, stake, minimumBet);
    }

    @Test
    void testGetAction_InvalidCommand_WhenEnoughCash() throws Exception {
        // Given
        int playerCash = 500;
        int minimumBet = 100;
        int stake = 200;

        // Symulacja wejścia użytkownika
        Scanner scanner = new Scanner(new ByteArrayInputStream("invalid\n".getBytes()));

        // When
        boolean result = auction.getAction(mockClient, scanner, playerCash, minimumBet, stake);

        // Then
        assertFalse(result);
        verify(auction, never()).handleCall(any(), anyInt(), anyInt(), anyInt());
        verify(auction, never()).handleRaise(any(), any(), anyInt(), anyInt(), anyInt());
    }



}