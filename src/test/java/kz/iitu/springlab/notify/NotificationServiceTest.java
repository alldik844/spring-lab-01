
package kz.iitu.springlab.notify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private Notifier primary;

    @Mock
    private Notifier console;

    @Mock
    private Notifier custom;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(
                primary,
                console,
                custom,
                List.of(primary, console, custom),
                Map.of(
                        "email", primary,
                        "console", console,
                        "base64", custom
                )
        );
    }

    @Test
    void primaryNotificationIsSentSuccessfully() {
        when(primary.send("Hello"))
                .thenReturn("email: Hello");

        String result = service.viaPrimary("Hello");

        assertThat(result)
                .isEqualTo("email: Hello");

        verify(primary).send("Hello");
    }

    @Test
    void unknownChannelThrowsException() {
        assertThatThrownBy(
                () -> service.viaChannel("sms", "Hello")
        )
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("sms");
    }

    @Test
    void consoleReceivesCorrectMessage() {
        when(console.send("Reminder"))
                .thenReturn("console: Reminder");

        String result = service.viaConsole("Reminder");

        assertThat(result)
                .isEqualTo("console: Reminder");

        ArgumentCaptor<String> captor =
                ArgumentCaptor.forClass(String.class);

        verify(console).send(captor.capture());

        assertThat(captor.getValue())
                .isEqualTo("Reminder");
    }

    @Test
    void disabledNotificationsAreNotSent() {
        String result = service.notifyReader(
                "reader-1",
                "Important!",
                false
        );

        assertThat(result)
                .isEqualTo("notifications disabled");

        verifyNoInteractions(primary, console, custom);
    }

    @Test
    void enabledNotificationsAreSentSuccessfully() {
        when(primary.send("Important!"))
                .thenReturn("email: Important!");

        String result = service.notifyReader(
                "reader-1",
                "Important!",
                true
        );

        assertThat(result)
                .isEqualTo("email: Important!");

        verify(primary).send("Important!");
        verifyNoInteractions(console, custom);
    }
}
