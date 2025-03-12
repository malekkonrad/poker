package pl.project.stages;

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

class ChangingCardsTest {



    private ChangingCards changingCards;
    private Client mockClient;
    private SocketChannel mockSocketChannel;

    @BeforeEach
    void setUp() {
        changingCards = spy(new ChangingCards());
        mockClient = mock(Client.class);
        mockSocketChannel = mock(SocketChannel.class);

        // Mockowanie SocketChannel w kliencie
        when(mockClient.getSocketChannel()).thenReturn(mockSocketChannel);

        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);
    }

    @Test
    void testChangeCards_YesResponse_ValidCard() throws IOException {
        // Given: Symulowanie odpowiedzi użytkownika
        doReturn("yes").when(changingCards).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        doReturn("2").when(changingCards).getUserInput("Please enter your card number (on the left) you want to exchange: ");

        // When
        changingCards.changeCards(mockClient);

        // Then
        verify(mockClient, times(1)).getGameID();
        verify(mockClient, times(1)).getPlayerID();
        verify(changingCards, times(1)).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        verify(changingCards, times(1)).getUserInput("Please enter your card number (on the left) you want to exchange: ");
    }




    @Test
    void testChangeCards_NoResponse() throws IOException {
        // Given: Symulowanie odpowiedzi użytkownika
        doReturn("no").when(changingCards).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");

        // When
        changingCards.changeCards(mockClient);

        // Then
        verify(mockClient, times(1)).getGameID();
        verify(mockClient, times(1)).getPlayerID();
        verify(changingCards, times(1)).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
    }


    @Test
    void testChangeCards_InvalidCardNumber() throws IOException {
        // Given: Symulowanie odpowiedzi użytkownika
        doReturn("yes").when(changingCards).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        doReturn("10").when(changingCards).getUserInput("Please enter your card number (on the left) you want to exchange: ");
        doReturn("2").when(changingCards).getUserInput("Please enter your card number (on the left) you want to exchange: ");

        // When
        changingCards.changeCards(mockClient);

        // Then
        verify(changingCards, times(1)).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        verify(changingCards, times(1)).getUserInput("Please enter your card number (on the left) you want to exchange: ");
        verify(mockClient, times(1)).getGameID();
        verify(mockClient, times(1)).getPlayerID();
    }

    @Test
    void testChangeCards_InvalidInputFormat() throws IOException {
        // Given: Symulowanie odpowiedzi użytkownika
        doReturn("yes").when(changingCards).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        doReturn("invalid").when(changingCards).getUserInput("Please enter your card number (on the left) you want to exchange: ");
        doReturn("2").when(changingCards).getUserInput("Please enter your card number (on the left) you want to exchange: ");

        // When
        changingCards.changeCards(mockClient);

        // Then
        verify(changingCards, times(1)).getUserInput("If you want to exchange card enter 'yes' and if you don't want to enter 'no': ");
        verify(changingCards, times(1)).getUserInput("Please enter your card number (on the left) you want to exchange: ");
        verify(mockClient, times(1)).getGameID();
        verify(mockClient, times(1)).getPlayerID();
    }

    @Test
    void testDisplayCards() {
        // Given: Mockowane karty użytkownika
        when(mockClient.getHand()).thenReturn(Arrays.asList("Card1", "Card2", "Card3", "Card4", "Card5"));

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody
        changingCards.displayCards(mockClient);

        // Przywrócenie domyślnego wyjścia
        System.setOut(System.out);

        // Then: Weryfikacja zawartości konsoli
        String expectedOutput =
                "---------------\r\n" +
                        "Your cards:\r\n" +
                        "---------------\r\n" +
                        "0: Card1\r\n" +
                        "1: Card2\r\n" +
                        "2: Card3\r\n" +
                        "3: Card4\r\n" +
                        "4: Card5\r\n";

        assertEquals(expectedOutput.trim(), outContent.toString().trim());
    }

    @Test
    void testHandleNextStage() throws IOException {
        // When: Wywołanie metody
        changingCards.handleNextStage(mockClient);

        // Then: Weryfikacja wyświetlonej wiadomości i zmiany etapu
        verify(mockClient, times(1)).setStage(any(Auction.class));

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        changingCards.handleNextStage(mockClient);

        System.setOut(System.out); // Przywrócenie konsoli

        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Every player changed theirs cards, we are going to next stage - second auction cards"));
        assertTrue(consoleOutput.contains("SECOND AUCTION"));
    }

    @Test
    void testHandleRequest_ChangeCards() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "changeCards";
        doNothing().when(changingCards).handleChangeCards(mockClient);

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Then: Weryfikacja wywołania metody handleChangeCards
        verify(changingCards, times(1)).handleChangeCards(mockClient);
    }


    @Test
    void testHandleRequest_AcceptedChange() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "acceptedChange 2 NewCard";
        doNothing().when(changingCards).handleAcceptedChange(eq(mockClient), any());

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Then: Weryfikacja wywołania metody handleAcceptedChange
        verify(changingCards, times(1)).handleAcceptedChange(eq(mockClient), any());
    }


    @Test
    void testHandleRequest_AcceptedEndChanging() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "acceptedEndChanging";

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Przywrócenie konsoli
        System.setOut(System.out);

        // Then: Weryfikacja komunikatu
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Wait for other players to exchange cards"));
    }

    @Test
    void testHandleRequest_NextStage() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "nextStage";
        doNothing().when(changingCards).handleNextStage(mockClient);

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Then: Weryfikacja wywołania metody handleNextStage
        verify(changingCards, times(1)).handleNextStage(mockClient);
    }



    @Test
    void testHandleRequest_DeniedChange() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "deniedChange";

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Przywrócenie konsoli
        System.setOut(System.out);

        // Then: Weryfikacja komunikatu
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("You changed maximum of cards (only 4 changes are allowed)."));
    }


    @Test
    void testHandleRequest_UnrecognizedMessage() throws IOException {
        // Given: Symulowana odpowiedź serwera
        String response = "unknownMessage";

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody
        changingCards.handleRequest(mockClient, response);

        // Przywrócenie konsoli
        System.setOut(System.out);

        // Then: Weryfikacja komunikatu
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("Unrecognised message"));
    }




    @Test
    void testHandleAcceptedChange() throws IOException {
        // Given: Dane wejściowe
        String[] parts = {"acceptedChange", "1", "NewCard"};
        List<String> mockHand = new ArrayList<>(Arrays.asList("Card1", "Card2", "Card3", "Card4", "Card5"));
        when(mockClient.getHand()).thenReturn(mockHand);

        // Zastąpienie wywołania metody `changeCards`
        doNothing().when(changingCards).changeCards(mockClient);

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When: Wywołanie metody
        changingCards.handleAcceptedChange(mockClient, parts);

        // Przywrócenie domyślnego System.out
        System.setOut(System.out);

        // Then: Weryfikacja zmian w ręce gracza
        assertEquals("NewCard", mockHand.get(1));

        // Weryfikacja, czy wyświetlono odpowiednie informacje
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("You correctly changed your card."));
        assertTrue(consoleOutput.contains("Your cards:"));

        // Weryfikacja, że metoda `changeCards` została wywołana
        verify(changingCards, times(1)).changeCards(mockClient);
    }


    @Test
    void testHandleChangeCards() throws IOException {
        // Given
        doNothing().when(changingCards).changeCards(mockClient);
        List<String> mockHand = new ArrayList<>(Arrays.asList("Card1", "Card2", "Card3", "Card4", "Card5"));
        when(mockClient.getHand()).thenReturn(mockHand);

        // Przechwycenie wyjścia na konsolę
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // When
        changingCards.handleChangeCards(mockClient);

        // Przywrócenie domyślnego System.out
        System.setOut(System.out);

        // Then: Weryfikacja, czy wywołano zmianę kart
        verify(changingCards, times(1)).changeCards(mockClient);

        // Weryfikacja wyjścia na konsolę
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("It's your turn, do you want to change cards"));
        assertTrue(consoleOutput.contains("Your cards:"));
    }

    @Test
    void testExecute() throws IOException {
        // Given
        String mockResponse = "changeCards";
        doReturn(mockResponse).when(changingCards).getResponse(mockClient);
        doNothing().when(changingCards).handleRequest(mockClient, mockResponse);

        // When
        changingCards.execute(mockClient);

        // Then
        verify(changingCards, times(1)).getResponse(mockClient);
        verify(changingCards, times(1)).handleRequest(mockClient, mockResponse);
    }



}