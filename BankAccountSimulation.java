package bank.account.simulation;
import java.sql.*;
import java.util.*;

class ATM {
    Connection con;
    int pin;

    // Constructor: establishes DB connection
    public ATM() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bankdb", "root", ""); 
            // Replace "" with your MySQL password if you set one
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ PIN Verification
    public void checkpin() {
        System.out.print("Enter your PIN: ");
        Scanner sc = new Scanner(System.in);
        int enteredPin = sc.nextInt();

        try {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM accounts WHERE pin = ?");
            pst.setInt(1, enteredPin);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                pin = enteredPin;
                System.out.println("Welcome " + rs.getString("name") + "!");
                menu();
            } else {
                System.out.println("Invalid PIN! Try again.\n");
                checkpin();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // ✅ Main Menu
    public void menu() {
        System.out.println("\nEnter your choice:");
        System.out.println("1. Check A/C Balance");
        System.out.println("2. Withdraw Money");
        System.out.println("3. Deposit Money");
        System.out.println("4. Exit");

        Scanner sc = new Scanner(System.in);
        int opt = sc.nextInt();

        switch (opt) {
            case 1:  checkBalance();
            case 2: withdrawMoney();
            case 3:  depositMoney();
            case 4: {
                System.out.println("Thank you! Have a nice day.");
                System.exit(0);
            }
            default: {
                System.out.println("Invalid choice! Try again.");
                menu();
            }
        }
    }

    // ✅ Check Balance
    public void checkBalance() {
        try {
            PreparedStatement pst = con.prepareStatement("SELECT balance FROM accounts WHERE pin = ?");
            pst.setInt(1, pin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("Your Balance: ₹" + rs.getFloat("balance"));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        menu();
    }

    // ✅ Withdraw
    public void withdrawMoney() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter amount to withdraw: ");
        float amount = sc.nextFloat();

        try {
            PreparedStatement pst = con.prepareStatement("SELECT balance FROM accounts WHERE pin = ?");
            pst.setInt(1, pin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                float currentBalance = rs.getFloat("balance");
                if (amount > currentBalance) {
                    System.out.println("Insufficient balance!");
                } else {
                    float newBalance = currentBalance - amount;
                    PreparedStatement update = con.prepareStatement("UPDATE accounts SET balance = ? WHERE pin = ?");
                    update.setFloat(1, newBalance);
                    update.setInt(2, pin);
                    update.executeUpdate();
                    System.out.println("Withdrawal successful!");
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        menu();
    }

    // ✅ Deposit
    public void depositMoney() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter amount to deposit: ");
        float amount = sc.nextFloat();

        try {
            PreparedStatement pst = con.prepareStatement("SELECT balance FROM accounts WHERE pin = ?");
            pst.setInt(1, pin);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                float newBalance = rs.getFloat("balance") + amount;
                PreparedStatement update = con.prepareStatement("UPDATE accounts SET balance = ? WHERE pin = ?");
                update.setFloat(1, newBalance);
                update.setInt(2, pin);
                update.executeUpdate();
                System.out.println("Money deposited successfully!");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        menu();
    }
}

public class BankAccountSimulation {
    public static void main(String[] args) {
        ATM obj = new ATM();
        obj.checkpin();
    }
}
