package com.drive.collab.websocket;

import com.drive.collab.CollabServiceApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = CollabServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CollabWebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String wsUrl;

    @MockBean
    private RestTemplate restTemplate; // Mock storage service client

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        wsUrl = "ws://localhost:" + port + "/ws/collab";
    }

    @AfterEach
    void tearDown() {
        if (stompClient != null) {
            stompClient.stop();
        }
    }

    @Test
    void testWebSocketConnectionAndPresence() throws Exception {
        // Simulate a valid JWT (bypass for test profile)
        String token = "test-jwt";
        String docId = "test-doc-456";
        String joinDestination = "/app/collab/" + docId + "/join";
        String presenceTopic = "/topic/collab/" + docId + "/presence";

        BlockingQueue<String> presenceQueue = new LinkedBlockingQueue<>();

        StompSession session = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        session.subscribe(presenceTopic, new DefaultStompFrameHandler(presenceQueue));

        // Send join message
        session.send(joinDestination, new JoinRequest("alice"));

        // Expect a presence message
        String presenceMsg = presenceQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(presenceMsg, "Should receive presence message");
        assertTrue(presenceMsg.contains("alice"));
    }

    @Test
    void testUnauthorizedWebSocketConnection() {
        String docId = "test-doc-unauth";
        String wsUrlNoToken = wsUrl; // No token
        assertThrows(Exception.class, () -> {
            stompClient.connect(wsUrlNoToken, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        });
    }

    // Mocked snapshot save test
    @Test
    void testSnapshotSave() throws Exception {
        String token = "test-jwt";
        String docId = "test-doc-snap";
        String saveDestination = "/app/collab/" + docId + "/save";

        StompSession session = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        byte[] encryptedSnapshot = new byte[] {1,2,3,4};
        SaveRequest saveRequest = new SaveRequest(encryptedSnapshot, "alice");
        session.send(saveDestination, saveRequest);
        // No assertion: just ensure no error
    }

    // Invalid input test
    @Test
    void testInvalidJoinInput() throws Exception {
        String token = "test-jwt";
        String docId = "test-doc-invalid";
        String joinDestination = "/app/collab/" + docId + "/join";

        StompSession session = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        // Send invalid join (null username)
        session.send(joinDestination, new JoinRequest(null));
        // No assertion: just ensure no crash
    }

    @Test
    void testSnapshotSaveWithMockedStorage() throws Exception {
        String token = "test-jwt";
        String docId = "test-doc-snap-mock";
        String saveDestination = "/app/collab/" + docId + "/save";

        StompSession session = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        byte[] encryptedSnapshot = new byte[] {9,8,7,6};
        SaveRequest saveRequest = new SaveRequest(encryptedSnapshot, "bob");
        session.send(saveDestination, saveRequest);
        // Verify that RestTemplate.postForEntity was called
        Mockito.verify(restTemplate, Mockito.timeout(1000).atLeastOnce())
            .postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(Void.class));
    }

    @Test
    void testMultiUserEditingAndPresence() throws Exception {
        String token1 = "test-jwt-1";
        String token2 = "test-jwt-2";
        String docId = "test-doc-multi";
        String joinDestination = "/app/collab/" + docId + "/join";
        String presenceTopic = "/topic/collab/" + docId + "/presence";
        String updateDestination = "/app/collab/" + docId + "/update";
        String updateTopic = "/topic/collab/" + docId;

        BlockingQueue<String> presenceQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<String> presenceQueue2 = new LinkedBlockingQueue<>();
        BlockingQueue<byte[]> updateQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<byte[]> updateQueue2 = new LinkedBlockingQueue<>();

        StompSession session1 = stompClient.connect(wsUrl + "?token=" + token1, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        StompSession session2 = stompClient.connect(wsUrl + "?token=" + token2, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        session1.subscribe(presenceTopic, new DefaultStompFrameHandler(presenceQueue1));
        session2.subscribe(presenceTopic, new DefaultStompFrameHandler(presenceQueue2));
        session1.subscribe(updateTopic, new ByteArrayStompFrameHandler(updateQueue1));
        session2.subscribe(updateTopic, new ByteArrayStompFrameHandler(updateQueue2));

        // Both join
        session1.send(joinDestination, new JoinRequest("alice"));
        session2.send(joinDestination, new JoinRequest("bob"));

        // Both should receive presence messages
        String presenceMsg1 = presenceQueue1.poll(2, TimeUnit.SECONDS);
        String presenceMsg2 = presenceQueue2.poll(2, TimeUnit.SECONDS);
        assertNotNull(presenceMsg1);
        assertNotNull(presenceMsg2);
        assertTrue(presenceMsg1.contains("alice") || presenceMsg1.contains("bob"));
        assertTrue(presenceMsg2.contains("alice") || presenceMsg2.contains("bob"));

        // alice sends an update
        byte[] update = new byte[] {1,2,3};
        session1.send(updateDestination, update);
        // bob should receive the update
        byte[] received = updateQueue2.poll(2, TimeUnit.SECONDS);
        assertArrayEquals(update, received);
    }

    @Test
    void testDisconnectAndReconnectPresence() throws Exception {
        String token = "test-jwt-disconnect";
        String docId = "test-doc-disconnect";
        String joinDestination = "/app/collab/" + docId + "/join";
        String presenceTopic = "/topic/collab/" + docId + "/presence";

        BlockingQueue<String> presenceQueue = new LinkedBlockingQueue<>();
        StompSession session = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        session.subscribe(presenceTopic, new DefaultStompFrameHandler(presenceQueue));
        session.send(joinDestination, new JoinRequest("carol"));
        String presenceMsg = presenceQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(presenceMsg);
        session.disconnect();
        // Reconnect
        StompSession session2 = stompClient.connect(wsUrl + "?token=" + token, new WebSocketHttpHeaders(), new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
        session2.subscribe(presenceTopic, new DefaultStompFrameHandler(presenceQueue));
        session2.send(joinDestination, new JoinRequest("carol"));
        String presenceMsg2 = presenceQueue.poll(2, TimeUnit.SECONDS);
        assertNotNull(presenceMsg2);
    }

    // Helper classes
    public static class JoinRequest {
        private String username;
        public JoinRequest() {}
        public JoinRequest(String username) { this.username = username; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
    public static class SaveRequest {
        private byte[] encryptedSnapshot;
        private String savedBy;
        public SaveRequest() {}
        public SaveRequest(byte[] encryptedSnapshot, String savedBy) {
            this.encryptedSnapshot = encryptedSnapshot;
            this.savedBy = savedBy;
        }
        public byte[] getEncryptedSnapshot() { return encryptedSnapshot; }
        public void setEncryptedSnapshot(byte[] encryptedSnapshot) { this.encryptedSnapshot = encryptedSnapshot; }
        public String getSavedBy() { return savedBy; }
        public void setSavedBy(String savedBy) { this.savedBy = savedBy; }
    }
    public static class DefaultStompFrameHandler implements StompFrameHandler {
        private final BlockingQueue<String> queue;
        public DefaultStompFrameHandler(BlockingQueue<String> queue) { this.queue = queue; }
        @Override public Type getPayloadType(StompHeaders headers) { return String.class; }
        @Override public void handleFrame(StompHeaders headers, Object payload) { queue.offer((String) payload); }
    }
    public static class ByteArrayStompFrameHandler implements StompFrameHandler {
        private final BlockingQueue<byte[]> queue;
        public ByteArrayStompFrameHandler(BlockingQueue<byte[]> queue) { this.queue = queue; }
        @Override public Type getPayloadType(StompHeaders headers) { return byte[].class; }
        @Override public void handleFrame(StompHeaders headers, Object payload) { queue.offer((byte[]) payload); }
    }
} 