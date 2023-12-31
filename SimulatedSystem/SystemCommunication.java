package SimulatedSystem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
public class SystemCommunication {

    private String name;
    private SystemCommunication prev;
    private SystemCommunication next;
    private Queue<String> inboxQueue;
    private Queue<String> outboxQueue;
    private Stack<String> processingStack;

    public SystemCommunication(String name) {
        this.name = name;
        this.prev = null;
        this.next = null;
        this.inboxQueue = new LinkedList<>();
        this.outboxQueue = new LinkedList<>();
        this.processingStack = new Stack<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SystemCommunication getPrev() {
        return prev;
    }

    public void setPrev(SystemCommunication prev) {
        this.prev = prev;
    }

    public SystemCommunication getNext() {
        return next;
    }

    public void setNext(SystemCommunication next) {
        this.next = next;
    }

    public Queue<String> getInboxQueue() {
        return inboxQueue;
    }

    public void setInboxQueue(Queue<String> inboxQueue) {
        this.inboxQueue = inboxQueue;
    }

    public Queue<String> getOutboxQueue() {
        return outboxQueue;
    }

    public void setOutboxQueue(Queue<String> outboxQueue) {
        this.outboxQueue = outboxQueue;
    }

    public Stack<String> getProcessingStack() {
        return processingStack;
    }

    public void setProcessingStack(Stack<String> processingStack) {
        this.processingStack = processingStack;
    }

    public void connect(SystemCommunication other) {

        if (this.next != null || this.prev != null) {
            System.out.println(this.name + " đã kết nối với một system khác.");
            return;
        }

        if (other.next != null || other.prev != null) {
            System.out.println(other.name + " đã kết nối với một system khác.");
            return;
        }

        this.next = other;
        other.prev = this;

        System.out.println(this.name + " kết nối với " + other.name);
        System.out.println(other.name + " kết nối với " + this.name);
    }

    public void disconnect(SystemCommunication other) {

        if (this.next != other || other.prev != this) {
            System.out.println("Hai system không kết nối với nhau.");
            return;
        }

        this.next = null;
        other.prev = null;
        // Báo cáo ngắt kết nối
        System.out.println(this.name + " ngắt kết nối với " + other.name);
        System.out.println(other.name + " ngắt kết nối với " + this.name);
    }

    public void sendMessage(SystemCommunication other, String message) {
        long startSendMessage = System.nanoTime();
        if (this.next != other || other.prev != this) {
            System.out.println("Hai system không kết nối với nhau.");
            return;
        }
        // Kiểm tra độ dài của tin nhắn
        if (message.isEmpty()) {
            System.out.println("Tin nhắn không được rỗng.");
            return;
        }
        if (message.length() > 250) {
            System.out.println("Tin nhắn quá dài, sẽ được cắt thành nhiều tin nhắn nhỏ hơn.");

            int n = message.length() / 250;
            for (int i = 0; i < n; i++) {
                // Lấy đoạn tin nhắn thứ i
                String subMessage = message.substring(i * 250, Math.min((i + 1) * 250, message.length()));
                // Thêm đoạn tin nhắn vào outboxQueue của system này
                this.outboxQueue.add(subMessage);
                // Báo cáo tin nhắn đã được thêm vào outboxQueue
                System.out.println(this.name + " đã thêm tin nhắn vào outboxQueue: " + subMessage);
            }
        } else {
            // Thêm tin nhắn vào outboxQueue của system này
            this.outboxQueue.add(message);
            // Báo cáo tin nhắn đã được thêm vào outboxQueue
            System.out.println(this.name + " đã thêm tin nhắn vào outboxQueue: " + message);
        }
        long endSendMessage = System.nanoTime();
        long sendMessageTime = endSendMessage - startSendMessage;
        System.out.println("Runtime: " + sendMessageTime + " nano giây");

    }

    public void receiveMessage(SystemCommunication other) {

        if (this.prev != other || other.next != this) {
            System.out.println("Hai system không kết nối với nhau.");
            return;
        }

        if (other.outboxQueue.isEmpty()) {
            System.out.println(other.name + " không có tin nhắn để gửi.");
            return;
        }
        String message = other.outboxQueue.poll();
        this.inboxQueue.add(message);
        System.out.println(this.name + " đã nhận tin nhắn từ " + other.name + " và lưu vào inboxQueue: " + message);
    }

    public void readIncomingMessage() {
        // Kiểm tra xem inboxQueue có rỗng không
        if (this.inboxQueue.isEmpty()) {
            System.out.println(this.name + " không có tin nhắn đến.");
            return;
        }
        // Lấy tin nhắn từ inboxQueue
        String message = this.inboxQueue.poll();
        // Kiểm tra xem tin nhắn có hợp lệ không
        if (message.length() < 4) {
            System.out.println(this.name + " đã nhận được một tin nhắn không hợp lệ: " + message);
            return;
        }
        // Lấy mã của tin nhắn
        String code = message.substring(0, 4);
        // Kiểm tra xem mã có phải là "0000" không
        if (code.equals("0000")) {
            System.out.println(this.name + " đã nhận được một tin nhắn kết thúc: " + message);
            return;
        }
        // Kiểm tra xem mã có phải là "1111" không
        if (code.equals("1111")) {
            System.out.println(this.name + " đã nhận được một tin nhắn báo lỗi: " + message);
            return;
        }
        // Kiểm tra xem mã có phải là "2222" không
        if (code.equals("2222")) {
            System.out.println(this.name + " đã nhận được một tin nhắn thông thường: " + message);
            // Thêm tin nhắn vào processingStack
            this.processingStack.push(message);
            // Báo cáo tin nhắn đã được thêm vào processingStack
            System.out.println(this.name + " đã thêm tin nhắn vào processingStack: " + message);
        }

    }

    public void readOutgoingMessage() {
        // Kiểm tra xem outboxQueue có rỗng không
        if (this.outboxQueue.isEmpty()) {
            System.out.println(this.name + " không có tin nhắn đi.");
            return;
        }
        // Lấy tin nhắn từ outboxQueue
        String message = this.outboxQueue.poll();
        // Kiểm tra xem tin nhắn có hợp lệ không
        if (message.length() < 4) {
            System.out.println(this.name + " đã gửi một tin nhắn không hợp lệ: " + message);
            return;
        }
        // Lấy mã của tin nhắn
        String code = message.substring(0, 4);
        // Kiểm tra xem mã có phải là "0000" không
        if (code.equals("0000")) {
            System.out.println(this.name + " đã gửi một tin nhắn kết thúc: " + message);
            return;
        }
        // Kiểm tra xem mã có phải là "1111" không
        if (code.equals("1111")) {
            System.out.println(this.name + " đã gửi một tin nhắn báo lỗi: " + message);
            return;
        }
        // Kiểm tra xem mã có phải là "2222" không
        if (code.equals("2222")) {
            System.out.println(this.name + " đã gửi một tin nhắn thông thường: " + message);
        }
    }

    public void processMessage() {
        // Kiểm tra xem processingStack có rỗng không
        if (this.processingStack.isEmpty()) {
            System.out.println(this.name + " không có tin nhắn để xử lý.");
            return;
        }
        // Lấy tin nhắn từ processingStack
        String message = this.processingStack.pop();
        System.out.println(this.name + " đã lấy tin nhắn từ processingStack: " + message);

    }
}

class SystemTest{
    public static void main (String[] args) {

        SystemCommunication systemA = new SystemCommunication("System A");
        SystemCommunication systemB = new SystemCommunication("System B");

        systemA.connect(systemB);
        // Gửi tin nhắn từ system A đến system B
        systemA.sendMessage(systemB, "");
        systemA.sendMessage(systemB, "1111 Please check your system");
        systemA.sendMessage(systemB, "2222 Hello from system A");
        systemA.sendMessage(systemB, "0000 Check if the message with the length longer than 250 works,00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        //systemA.readOutgoingMessage();

        systemB.receiveMessage(systemA);
        systemB.receiveMessage(systemA);
        systemB.receiveMessage(systemA);
        systemB.readIncomingMessage();
        systemB.readIncomingMessage();
        systemB.processMessage();

        systemA.disconnect(systemB);
    }
}
