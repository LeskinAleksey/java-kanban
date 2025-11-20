import java.time.Duration;
import java.time.LocalDateTime;

public class BaseTest {
    protected LocalDateTime currentDateTime = LocalDateTime.now().withNano(0);

    protected LocalDateTime getStartTime(int taskIndex) {
        return currentDateTime.plusHours(taskIndex * 3L);
    }

    protected Duration getDuration() {
        return Duration.ofHours(2);
    }
}
