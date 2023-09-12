package exercise3;

public class Person {

    private final long id;
    private String name;
    private int zip;
    private String address;
    private static long counter;

    public Person() {
        synchronized (Person.class) {
            this.id = counter;
            try {
                System.out.println("Thread " + Thread.currentThread().threadId()  + " Created a person with id: " + this.id);
                Thread.sleep(50);
                counter++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Person(long id) {
        synchronized (Person.class) {   
            if (counter == 0) {
                counter = id;
                counter++;
                this.id = id;
            } else {
                this.id = counter;
                counter++;
            }
        }
    }

    public synchronized void change(int newZip, String newAddress) {
        zip = newZip;
        address = newAddress;
    }

    public synchronized long getId() {
        return id;
    }

    public synchronized String getName() {
        return name;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public synchronized int getZip() {
        return zip;
    }

    public synchronized String getAddress() {
        return address;
    }

    public static void main(String[] args) {
        new Person(7);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                new Person();
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                new Person();
            }
        });
        t2.start();
    }

}
