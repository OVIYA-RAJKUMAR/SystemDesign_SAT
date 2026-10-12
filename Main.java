class Wallet {
    int id;
    double balance;

    Wallet(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }
}

class PaymentService {
    void transfer(Wallet from, Wallet to, double amount) {
        if (amount <= 0) {
            System.out.println("Invalid amount");
        } else if (from.balance < amount) {
            System.out.println("Insufficient balance");
        } else {
            from.balance -= amount;
            to.balance += amount;
            System.out.println("Transfer Successful");
            System.out.println("Sender Balance: " + from.balance);
            System.out.println("Receiver Balance: " + to.balance);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Wallet w1 = new Wallet(101, 2000);
        Wallet w2 = new Wallet(102, 1000);

        PaymentService service = new PaymentService();
        service.transfer(w1, w2, 500);
    }
}