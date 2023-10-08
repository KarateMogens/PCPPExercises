package exercises05;
import java.util.function.IntToDoubleFunction;

import benchmarking.Benchmark;

public class TestVolatile{

    private volatile int vCtr;
    private int ctr;

    public TestVolatile() {
        Benchmark.SystemInfo();
        Benchmark.Mark7("Volatile", i -> {
            vInc();
            return vCtr;
        });

        Benchmark.Mark7("Normal", i -> {
            inc();
            return ctr;
        });
    }

    public void vInc() {
        vCtr++; 
    }

    public void inc() {
        ctr++;
    } 

    public static void main(String[] args) {
        TestVolatile myTestVolatile = new TestVolatile();
    }

    
}
