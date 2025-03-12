package pl.project.stages;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.project.client.Client;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PokerGameTest {


    private PokerGame pokerGame;
    private Client mockClient;

    @BeforeEach
    void setUp() {
        pokerGame = spy(new PokerGame());
        mockClient = mock(Client.class);

        // Mockowanie metody getHand()
        when(mockClient.getHand()).thenReturn(new ArrayList<>());
    }

    @Test
    void testGoToAuction() {
        // Wywołanie metody
        pokerGame.goToAuction(mockClient);

        // Weryfikacja ustawienia nowego etapu
        verify(mockClient, times(1)).setStage(any(Auction.class));
    }

    @Test
    void testCreateHandCardsToken() {
        // Mockowanie danych klienta
        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);

        // Wywołanie metody
        String token = pokerGame.createHandCardsToken(mockClient);

        // Weryfikacja tokena
        assertEquals("handCards 1 42 request", token);
    }

    @Test
    void testCreateAcceptedHandCardsToken() {
        // Mockowanie danych klienta
        when(mockClient.getGameID()).thenReturn(1);
        when(mockClient.getPlayerID()).thenReturn(42);

        // Wywołanie metody
        String token = pokerGame.createAcceptedHandCardsToken(mockClient);

        // Weryfikacja tokena
        assertEquals("handCards 1 42 accepted", token);
    }



}