package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import mobilepayment.Bank.BankCommand;
import mobilepayment.Bank.Transaction;

import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Collections;
// Hint: You may generate random numbers using Random::ints
import java.util.Random;
import java.util.stream.IntStream;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

  /* --- Messages ------------------------------------- */
  public interface MobileAppCommand {
  }

  public static final class MakeTransaction implements MobileAppCommand {
    
    private final ActorRef<Bank.BankCommand> bank;
    private final String to;
    private final String from;
    private final double value;

    public MakeTransaction(ActorRef<Bank.BankCommand> bank, String from, String to, double value) {
        this.bank = bank;
        this.to = to;
        this.from = from;
        this.value = value;
    }

    public ActorRef<Bank.BankCommand> getBank() {
      return bank;
    }

    public double getValue() {
        return value;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

  }
  
  public static final class MakePayments implements MobileAppCommand {

    private final ActorRef<Bank.BankCommand> bank;
    private final ArrayList<String> accounts;

    public MakePayments(ActorRef<Bank.BankCommand> bank, ArrayList<String> accounts) {
      this.bank = bank;
      this.accounts = accounts;
    }

    public ArrayList<String> getAccounts() {
      return accounts;
    }

    public ActorRef<Bank.BankCommand> getBank() {
      return bank;
    }
  }
  // Feel free to add message types at your convenience

  /* --- State ---------------------------------------- */
  // To be Implemented
  /* --- Constructor ---------------------------------- */
  // Feel free to extend the contructor at your convenience
  private MobileApp(ActorContext context) {
    super(context);
    context.getLog().info("Mobile app {} started!",
        context.getSelf().path().name());
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<MobileApp.MobileAppCommand> create() {
    return Behaviors.setup(MobileApp::new);
    // You may extend the constructor if necessary
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<MobileAppCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(MakeTransaction.class, this::onMakeTransaction)
        //.onMessage(MakeRandomPayments.class, this::onMakePayments)
        .onMessage(MakePayments.class, this::onMakePayments)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  private Behavior<MobileAppCommand> onMakeTransaction(MakeTransaction message) {
    ActorRef<Bank.BankCommand> bank = message.getBank();
    String from = message.getFrom();
    String to = message.getTo();
    double value = message.getValue();
    
    bank.tell(new Bank.Transaction(from, to, value));

    return this;
  }

   private Behavior<MobileAppCommand> onMakePayments(MakePayments message) {
    
    ActorRef<Bank.BankCommand> bank = message.getBank();
    ArrayList<String> accounts = message.getAccounts();
    
    for (int i = 0; i < 100; i++) {
      Collections.shuffle(accounts);
      double amount = Math.random() * 10000;
    
      bank.tell(new Bank.Transaction(accounts.get(0), accounts.get(1), amount));
    }
    

    return this;
  }
}
