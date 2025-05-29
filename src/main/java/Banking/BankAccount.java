package Banking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final ReentrantLock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    public void deposit(int amount) {
        lock.lock();
        try {

        balance += amount;
        }
        finally {
            lock.unlock();
        }

    }

    public void withdraw(int amount) {
        lock.lock();
        try {

            balance -= amount;
        }
        finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount target, int amount) throws InterruptedException {

        BankAccount firstLock = this.id < target.id ? this : target;
        BankAccount secondLock = this.id < target.id ? target : this;

        if (firstLock.lock.tryLock(100, TimeUnit.MILLISECONDS) && secondLock.lock.tryLock(100, TimeUnit.MILLISECONDS)) {
            try {
                this.balance -= amount;
                target.deposit(amount);
            } finally {

                firstLock.lock.unlock();
                secondLock.lock.unlock();
            }
        } else {
            System.out.println("Unable to obtain locks. Transfer not executed.");
        }
    }

    public Lock getLock() {
        return lock;
    }
    public int getId(){
        return  id;
    }
}
