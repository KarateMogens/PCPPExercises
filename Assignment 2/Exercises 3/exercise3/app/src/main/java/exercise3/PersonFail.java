package exercise3;

public class PersonFail {

    private final long id;
    private String name;
    private int zip;
    private String address;
    private static long counter;

    public PersonFail() {
        this.id = counter;
        try {
            System.out.println("Thread " + Thread.currentThread().threadId()  + " Created a person with id: " + this.id);
            Thread.sleep(50);
            counter++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PersonFail(long id) {
        if (counter == 0) {
            counter = id;
            counter++;
            this.id = id;
        } else {
            this.id = counter;
            counter++;
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
        int numberOfPeople = 1000;
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < numberOfPeople; i++) {
                new PersonFail();
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < numberOfPeople; i++) {
                new PersonFail();
            }
        });
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PersonFail p = new PersonFail();
        System.out.println("P has ID: " + p.getId() + ". Should be: " + (2*numberOfPeople));
    }

}
