package exercises03;

public class Person {

    private final long id;
    private String name;
    private int zip;
    private String address;
    private static long counter;

    public Person() {
        this.id = counter;
        counter++;
    }

    public Person(long id) {
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
        Thread t1 = new Thread(() -> {
            for (int i = 77; i < 77+20; i++) {
                Person p = new Person();
                System.out.println("T1 is printing: the Id is " + p.getId());
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                Person p = new Person();
                System.out.println("T2 is printing: the ID is " + p.getId());
            }
        });
        t2.start();
    }

}
