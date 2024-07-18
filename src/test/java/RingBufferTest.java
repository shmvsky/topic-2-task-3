import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shmvsky.RingBuffer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RingBufferTest {

    private RingBuffer<Integer> ringBuffer;

    @BeforeEach
    public void setUp() {
        ringBuffer = new RingBuffer<>(3);
    }

    @Test
    public void testPutAndTake() throws InterruptedException {
        ringBuffer.put(1);
        ringBuffer.put(2);
        ringBuffer.put(3);

        assertEquals(1, ringBuffer.take());
        assertEquals(2, ringBuffer.take());
        assertEquals(3, ringBuffer.take());
    }

    @Test
    public void testPutBlocksWhenFull() throws InterruptedException {
        ringBuffer.put(1);
        ringBuffer.put(2);
        ringBuffer.put(3);

        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                ringBuffer.put(4);
                latch.countDown();  // should not reach here
            } catch (InterruptedException ignored) {
            }
        }).start();

        assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testTakeBlocksWhenEmpty() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                ringBuffer.take();
                latch.countDown();  // should not reach here
            } catch (InterruptedException ignored) {
            }
        }).start();

        assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

}
